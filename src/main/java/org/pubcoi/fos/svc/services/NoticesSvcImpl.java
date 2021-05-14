/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.services;

import org.pubcoi.cdm.cf.AwardDetailType;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.exceptions.endpoint.FosEndpointRecordNotFoundException;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dto.NoticeNodeDTO;
import org.pubcoi.fos.svc.repos.gdb.jpa.NoticesGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.NoticesMDBRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticesSvcImpl implements NoticesSvc {
    private static final Logger logger = LoggerFactory.getLogger(NoticesSvcImpl.class);

    final NoticesMDBRepo noticesMDBRepo;
    final NoticesGraphRepo noticesGRepo;
    final AwardsSvc awardsSvc;

    public NoticesSvcImpl(
            NoticesMDBRepo noticesMDBRepo,
            NoticesGraphRepo noticesGRepo,
            AwardsSvc awardsSvc
    ) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.noticesGRepo = noticesGRepo;
        this.awardsSvc = awardsSvc;
    }

    @Override
    public FullNotice addNotice(FullNotice notice) {
        // remove data we don't want / need
        notice.getNotice().setContactDetails(null);
        notice.setCreatedByUser(null);
        noticesMDBRepo.save(notice);
        for (AwardDetailType awardDetail : notice.getAwards().getAwardDetails()) {
            CFAward award = new CFAward(notice, awardDetail);
            // guess what - the bastarts will sometimes set the award ID to NULL ... WTF ?!?!?!?
            if (!awardIdIsValid(award)) {
                logger.error(Ansi.Red.format("Award GUID '%s' is not valid, using an internal ID", award.getId()));
                award.setId(String.format("FOS_GENERATED:%s:%s", notice.getId(), award.hashCode()));
            }
            awardsSvc.addAward(award);
        }
        return notice;
    }

    @Override
    public NoticeNodeDTO getNoticeDTO(String noticeId) {
        return new NoticeNodeDTO(noticesMDBRepo.findById(noticeId).orElseThrow(FosEndpointRecordNotFoundException::new));
    }

    @Override
    public List<FullNotice> getNoticesByClientId(String clientId) {
        return noticesGRepo.findAllNoticesByClientNode(clientId).stream()
                .map(n -> noticesMDBRepo.findById(n.getFosId()).orElseThrow())
                .sorted(Comparator.comparing(FullNotice::getCreatedDate))
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(String noticeId) {
        return noticesMDBRepo.existsById(noticeId);
    }

    @Override
    public FullNotice getNotice(String noticeId) {
        return noticesMDBRepo.findFullNoticeById(noticeId).orElseThrow();
    }

    boolean awardIdIsValid(CFAward award) {
        // we probably shouldn't expect it to be a guid
        if (null != award.getId()) {
            if (award.getId().matches("^[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}$")) {
                return true;
            } else if (award.getId().matches("[a-zA-Z0-9-]{10,}")) {
                logger.warn(Ansi.Yellow.format("Award GUID %s does not match usual regex", award.getId()));
                return true;
            }
        }
        return false;
    }
}

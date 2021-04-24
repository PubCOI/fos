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
import org.pubcoi.fos.svc.exceptions.ItemNotFoundException;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dto.NoticeNodeDTO;
import org.pubcoi.fos.svc.repos.gdb.jpa.NoticesGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.NoticesMDBRepo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticesSvcImpl implements NoticesSvc {

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
    public void addNotice(FullNotice notice) {
        // remove data we don't want / need
        notice.getNotice().setContactDetails(null);

        noticesMDBRepo.save(notice);
        for (AwardDetailType awardDetail : notice.getAwards().getAwardDetails()) {
            awardsSvc.addAward(new CFAward(notice, awardDetail));
        }
    }

    @Override
    public NoticeNodeDTO getNoticeDTO(String noticeId) {
        return new NoticeNodeDTO(noticesMDBRepo.findById(noticeId).orElseThrow(ItemNotFoundException::new));
    }

    @Override
    public List<FullNotice> getNoticesByClientId(String clientId) {
        return noticesGRepo.findAllNoticesByClientNode(clientId).stream()
                .map(n -> noticesMDBRepo.findById(n.getId()).orElseThrow())
                .sorted(Comparator.comparing(FullNotice::getCreatedDate))
                .collect(Collectors.toList());
    }
}

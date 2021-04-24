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

import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.models.core.CFAward;
import org.pubcoi.fos.svc.models.dto.AttachmentDTO;
import org.pubcoi.fos.svc.models.dto.AwardDTO;
import org.pubcoi.fos.svc.models.neo.nodes.AwardNode;
import org.pubcoi.fos.svc.repos.gdb.jpa.AwardsGraphRepo;
import org.pubcoi.fos.svc.repos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.svc.repos.mdb.NoticesMDBRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AwardsSvcImpl implements AwardsSvc {

    final AwardsMDBRepo awardsMDBRepo;
    final AttachmentSvc attachmentSvc;
    final AwardsGraphRepo awardsGraphRepo;
    final NoticesMDBRepo noticesMDBRepo;

    public AwardsSvcImpl(
            AwardsMDBRepo awardsMDBRepo,
            AttachmentSvc attachmentSvc,
            AwardsGraphRepo awardsGraphRepo,
            NoticesMDBRepo noticesMDBRepo) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.attachmentSvc = attachmentSvc;
        this.awardsGraphRepo = awardsGraphRepo;
        this.noticesMDBRepo = noticesMDBRepo;
    }

    @Override
    public void addAward(CFAward cfAward) {
        awardsMDBRepo.save(cfAward);
    }

    @Override
    public Set<AwardDTO> getAwardsForNotice(String noticeID) {
        return awardsMDBRepo.findAllByNoticeId(noticeID).stream().map(AwardDTO::new).collect(Collectors.toSet());
    }

    @Override
    public AwardDTO getAwardDetailsDTOWithAttachments(String awardId) {
        final CFAward award = awardsMDBRepo.findById(awardId).orElseThrow();
        AwardDTO awardDTO = new AwardDTO(award);
        List<AttachmentDTO> attachments = attachmentSvc.findAttachmentsByNoticeId(award.getNoticeId());
        if (null != award.getFosOrganisation() && null != award.getFosOrganisation().getId()) {
            awardDTO.setSupplierNumTotalAwards(awardsGraphRepo.countAwardsToSupplier(
                    award.getFosOrganisation().getId()
            ));
        }
        FullNotice notice = noticesMDBRepo.findById(award.getNoticeId()).orElseThrow();
        awardDTO.setNoticeTitle(notice.getNotice().getTitle());
        return awardDTO.setAttachments(attachments);
    }

    @Override
    public List<AwardNode> getAwardsForOrg(String orgId) {
        return awardsGraphRepo.getAwardsForSupplier(orgId);
    }
}

package org.pubcoi.fos.services;

import org.pubcoi.fos.gdb.AwardsGraphRepo;
import org.pubcoi.fos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.neo.nodes.AwardNode;
import org.pubcoi.fos.models.neo.nodes.Organisation;
import org.pubcoi.fos.models.neo.relationships.OrgLELink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GraphsOrchestrationImpl implements GraphsOrchestration {
    private static final Logger logger = LoggerFactory.getLogger(GraphsOrchestrationImpl.class);

    AwardsGraphRepo awardsGraphRepo;
    OrganisationsGraphRepo orgGraphRepo;
    NoticesMDBRepo noticesMDBRepo;

    public GraphsOrchestrationImpl(AwardsGraphRepo awardsGraphRepo, OrganisationsGraphRepo orgGraphRepo, NoticesMDBRepo noticesMDBRepo) {
        this.awardsGraphRepo = awardsGraphRepo;
        this.orgGraphRepo = orgGraphRepo;
        this.noticesMDBRepo = noticesMDBRepo;
    }


    @Override
    public void populateOne() {
        Optional<FullNotice> notice = noticesMDBRepo.findAll().stream().findFirst();
        if (!notice.isPresent()) {
            return;
        }

        for (AwardDetailParentType.AwardDetail awardDetail : notice.get().getAwards().getAwardDetail()) {
            awardsGraphRepo.save(new AwardNode().setId(awardDetail.getAwardGuid()));
        }
    }

    @Override
    public void deleteAll() {
        awardsGraphRepo.deleteAll();
        orgGraphRepo.deleteAll();
    }

    @Override
    public void createOneRelationship() {
        Optional<AwardNode> node = awardsGraphRepo.findAll().stream().findFirst();
        if (!node.isPresent()) return;

        AwardDetailParentType.AwardDetail award = null;
        parent : for (FullNotice notice : noticesMDBRepo.findAll()) {
            for (AwardDetailParentType.AwardDetail awardDetail : notice.getAwards().getAwardDetail()) {
                if (awardDetail.getAwardGuid().equals(node.get().getId())) {
                    logger.info("Found match");
                    award = awardDetail;
                    break parent;
                }
            }
            logger.warn("Could not find matching node");
            return;
        }

        if (null == award) return;

        awardsGraphRepo.save(
                node.get().setOrganisation(
                        new Organisation()
                                .setId(UUID.randomUUID().toString()).setLegalEntity(new OrgLELink(UUID.randomUUID().toString(), new Organisation().setVerified(true).setId("parent:" + UUID.randomUUID().toString()))),
                        award.getAwardedDate().toZonedDateTime(), award.getStartDate().toZonedDateTime(), award.getEndDate().toZonedDateTime()
                )
        );
    }
}

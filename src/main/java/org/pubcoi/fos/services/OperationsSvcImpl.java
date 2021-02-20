package org.pubcoi.fos.services;

import org.pubcoi.fos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.models.cf.AwardDetailParentType;
import org.pubcoi.fos.models.cf.FullNotice;
import org.pubcoi.fos.models.core.Award;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OperationsSvcImpl implements OperationsSvc {
    private static final Logger logger = LoggerFactory.getLogger(OperationsSvcImpl.class);

    NoticesMDBRepo noticesMDBRepo;
    AwardsMDBRepo awardsMDBRepo;

    public OperationsSvcImpl(NoticesMDBRepo noticesMDBRepo, AwardsMDBRepo awardsMDBRepo) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.awardsMDBRepo = awardsMDBRepo;
    }

    @Override
    public void saveNotice(FullNotice notice) {
        logger.debug("Saving notice {}", noticesMDBRepo.save(notice));
        for (AwardDetailParentType.AwardDetail awardDetail : notice.getAwards().getAwardDetail()) {
            logger.debug("Saving award {}", awardsMDBRepo.save(new Award(notice, awardDetail)));
        }
    }
}

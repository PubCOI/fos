package org.pubcoi.fos.services;

import org.pubcoi.fos.gdb.AwardsGraphRepo;
import org.pubcoi.fos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GraphsOrchestrationImpl implements GraphsOrchestration {
    private static final Logger logger = LoggerFactory.getLogger(GraphsOrchestrationImpl.class);

    AwardsGraphRepo awardsGraphRepo;
    OrganisationsGraphRepo orgGraphRepo;

    public GraphsOrchestrationImpl(AwardsGraphRepo awardsGraphRepo, OrganisationsGraphRepo orgGraphRepo, NoticesMDBRepo noticesMDBRepo) {
        this.awardsGraphRepo = awardsGraphRepo;
        this.orgGraphRepo = orgGraphRepo;
    }

    @Override
    public void deleteAll() {
        awardsGraphRepo.deleteAll();
        orgGraphRepo.deleteAll();
    }
}

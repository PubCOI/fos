package org.pubcoi.fos.services;

import org.pubcoi.fos.exceptions.FOSException;
import org.pubcoi.fos.gdb.AwardsGraphRepo;
import org.pubcoi.fos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.mdb.FOSOrganisationRepo;
import org.pubcoi.fos.mdb.OCCompaniesRepo;
import org.pubcoi.fos.models.core.FOSOCCompany;
import org.pubcoi.fos.models.core.FOSOrganisation;
import org.pubcoi.fos.models.neo.nodes.AwardNode;
import org.pubcoi.fos.models.neo.nodes.OrganisationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GraphSvcImpl implements GraphSvc {
    private static final Logger logger = LoggerFactory.getLogger(GraphSvcImpl.class);

    AwardsMDBRepo awardsMDBRepo;
    AwardsGraphRepo awardsGraphRepo;
    FOSOrganisationRepo fosOrganisationRepo;
    OrganisationsGraphRepo orgGraphRepo;
    OCCompaniesRepo ocCompaniesRepo;

    public GraphSvcImpl(AwardsMDBRepo awardsMDBRepo, AwardsGraphRepo awardsGraphRepo, FOSOrganisationRepo fosOrganisationRepo, OrganisationsGraphRepo orgGraphRepo, OCCompaniesRepo ocCompaniesRepo) {
        this.awardsMDBRepo = awardsMDBRepo;
        this.awardsGraphRepo = awardsGraphRepo;
        this.fosOrganisationRepo = fosOrganisationRepo;
        this.orgGraphRepo = orgGraphRepo;
        this.ocCompaniesRepo = ocCompaniesRepo;
    }

    @Override
    public void populateAwardsGraphFromMDB() {
        awardsMDBRepo.findAll().forEach(award -> {
            logger.debug("inspecting {}", award.getId());
            if (null != award.getFosOrganisation() && award.getFosOrganisation() instanceof FOSOCCompany) {
                FOSOrganisation org = award.getFosOrganisation();
                try {
                    orgGraphRepo.save(new OrganisationNode()
                            .setId(org.getId())
                            .setVerified(true)
                            .setCompanyName(ocCompaniesRepo.findById(org.getId()).orElseThrow(() -> new FOSException()).getName())
                    );
                    awardsGraphRepo.save(new AwardNode()
                            .setId(award.getId())
                            .setValue(award.getValue())
                            .setOrganisation(
                                    orgGraphRepo.findById(org.getId()).orElseThrow(() -> new FOSException()),
                                    award.getAwardedDate().toZonedDateTime(),
                                    award.getStartDate().toZonedDateTime(),
                                    award.getEndDate().toZonedDateTime()
                            )
                    );
                    logger.debug("saved award {}", award.getId());
                } catch (FOSException e) {
                    logger.error("Unable to insert entry into graph: is source MDB fully populated?");
                }
            }
        });
    }

}

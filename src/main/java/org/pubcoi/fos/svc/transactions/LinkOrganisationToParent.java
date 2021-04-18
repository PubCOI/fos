package org.pubcoi.fos.svc.transactions;

import org.pubcoi.fos.svc.repos.gdb.OrganisationsGraphRepo;
import org.pubcoi.fos.svc.models.core.NodeReference;
import org.pubcoi.fos.svc.models.neo.nodes.OrganisationNode;
import org.pubcoi.fos.svc.models.neo.relationships.OrgLELink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkOrganisationToParent implements IFosTransaction {
    private static final Logger logger = LoggerFactory.getLogger(LinkOrganisationToParent.class);

    final OrganisationsGraphRepo orgGraphRepo;
    OrganisationNode source;
    OrganisationNode target;
    final FosTransaction transaction;

    public LinkOrganisationToParent(
            OrganisationsGraphRepo orgGraphRepo,
            OrganisationNode source,
            OrganisationNode target,
            FosTransaction transaction
    ) {
        this.orgGraphRepo = orgGraphRepo;
        this.source = orgGraphRepo.findOrgHydratingPersons(source.getId()).orElse(orgGraphRepo.findOrgNotHydratingPersons(source.getId()).orElseThrow());
        this.target = orgGraphRepo.findOrgHydratingPersons(target.getId()).orElse(orgGraphRepo.findOrgNotHydratingPersons(target.getId()).orElseThrow());
        this.transaction = transaction;
    }

    @Override
    public FosTransaction exec() {
        logger.debug("Linking {} to parent {}", source.getId(), target.getId());
        source.setLegalEntity(new OrgLELink(
                source, target, transaction.id
        ));
        orgGraphRepo.save(source);
        return getTransaction();
    }

    @Override
    public IFosTransaction fromTransaction(FosTransaction transaction) {
        this.source = new OrganisationNode(transaction.getSource());
        this.target = new OrganisationNode(transaction.getTarget());
        return this;
    }

    @Override
    public FosTransaction getTransaction() {
        return new FosTransaction()
                .setTransactionImpl(this.getClass())
                .setSource(new NodeReference(source))
                .setTarget(new NodeReference(target))
                .setUid(transaction.getUid());
    }
}

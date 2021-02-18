package org.pubcoi.fos.services;

import org.pubcoi.fos.gdb.AwardsGraphRepo;
import org.springframework.stereotype.Service;

@Service
public class GraphSvcImpl implements GraphSvc {

    AwardsGraphRepo awardsGraphRepo;

    public GraphSvcImpl(AwardsGraphRepo awardsGraphRepo) {
        this.awardsGraphRepo = awardsGraphRepo;
    }

}

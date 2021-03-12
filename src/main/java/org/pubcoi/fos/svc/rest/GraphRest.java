package org.pubcoi.fos.svc.rest;

import org.pubcoi.fos.svc.gdb.ClientNodeFTS;
import org.pubcoi.fos.svc.models.dao.AwardDAO;
import org.pubcoi.fos.svc.models.dao.ClientNodeDAO;
import org.pubcoi.fos.svc.models.dao.ClientNodeFTSDAOResponse;
import org.pubcoi.fos.svc.models.dao.NoticeNodeDAO;
import org.pubcoi.fos.svc.services.AwardsSvc;
import org.pubcoi.fos.svc.services.ClientsSvc;
import org.pubcoi.fos.svc.services.NoticesSvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GraphRest {

    final ClientNodeFTS clientNodeFTS;
    final ClientsSvc clientSvc;
    final NoticesSvc noticesSvc;
    final AwardsSvc awardsSvc;

    public GraphRest(ClientNodeFTS clientNodeFTS, ClientsSvc clientSvc, NoticesSvc noticesSvc, AwardsSvc awardsSvc) {
        this.clientNodeFTS = clientNodeFTS;
        this.clientSvc = clientSvc;
        this.noticesSvc = noticesSvc;
        this.awardsSvc = awardsSvc;
    }

    /**
     * Return canonical client nodes that match the current client name (if any)
     *
     * @param query The search parameters
     * @return List of top responses, ordered by best -> worst match
     */
    @GetMapping("/api/ui/graphs/clients")
    public List<ClientNodeFTSDAOResponse> runQuery(@RequestParam String query, @RequestParam(required = false) String currentNode) {
        if (query.isEmpty()) return new ArrayList<>();
        return clientNodeFTS.findAllDTOProjectionsWithCustomQuery(query)
                .stream()
                .filter(c -> !c.getId().equals(currentNode))
                .limit(5)
                .map(ClientNodeFTSDAOResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/ui/graphs/clients/{clientID}")
    public ClientNodeDAO getClient(@PathVariable String clientID) {
        return clientSvc.getClientNode(clientID);
    }

    @GetMapping("/api/ui/graphs/notices/{noticeID}")
    public NoticeNodeDAO getNotice(@PathVariable String noticeID) {
        NoticeNodeDAO noticeNodeDAO = noticesSvc.getNotice(noticeID);
        for (AwardDAO awardDAO : awardsSvc.getAwardsForNotice(noticeID)) {
            noticeNodeDAO.addAward(awardDAO);
        }
        return noticeNodeDAO;
    }

}

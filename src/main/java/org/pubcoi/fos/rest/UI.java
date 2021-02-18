package org.pubcoi.fos.rest;

import org.pubcoi.fos.mdb.AwardsMDBRepo;
import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.models.dao.AwardDAO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UI {

    NoticesMDBRepo noticesMDBRepo;
    AwardsMDBRepo awardsMDBRepo;

    public UI(NoticesMDBRepo noticesMDBRepo, AwardsMDBRepo awardsMDBRepo) {
        this.noticesMDBRepo = noticesMDBRepo;
        this.awardsMDBRepo = awardsMDBRepo;
    }

    @GetMapping("/api/ui/awards")
    public List<AwardDAO> getContractAwards() {
        return awardsMDBRepo.findAll().stream().map(AwardDAO::new).collect(Collectors.toList());
    }

}

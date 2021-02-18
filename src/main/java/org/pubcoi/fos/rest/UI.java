package org.pubcoi.fos.rest;

import org.pubcoi.fos.mdb.NoticesMDBRepo;
import org.pubcoi.fos.models.dao.AwardDAO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UI {

    NoticesMDBRepo noticesMDBRepo;

    public UI(NoticesMDBRepo noticesMDBRepo) {
        this.noticesMDBRepo = noticesMDBRepo;
    }

    @GetMapping("/api/ui/awards")
    public List<AwardDAO> getContractAwards() {
        return noticesMDBRepo.findAll().stream().map(AwardDAO::new).collect(Collectors.toList());
    }

}

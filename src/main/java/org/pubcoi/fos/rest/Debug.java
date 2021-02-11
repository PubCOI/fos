package org.pubcoi.fos.rest;

import org.pubcoi.fos.dao.NoticesRepo;
import org.pubcoi.fos.models.cf.FullNotice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Debug {

    NoticesRepo noticesRepo;

    public Debug(NoticesRepo noticesRepo) {
        this.noticesRepo = noticesRepo;
    }

    @GetMapping(value = "/api/notices")
    public List<FullNotice> getNotices() {
        return noticesRepo.findAll();
    }

}

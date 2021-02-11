package org.pubcoi.fos.rest;

import org.pubcoi.fos.dao.NoticesRepo;
import org.pubcoi.fos.models.cf.ArrayOfFullNotice;
import org.pubcoi.fos.models.cf.FullNotice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Debug {
    private static final Logger logger = LoggerFactory.getLogger(Debug.class);
    NoticesRepo noticesRepo;

    public Debug(NoticesRepo noticesRepo) {
        this.noticesRepo = noticesRepo;
    }

    @GetMapping(value = "/api/notices")
    public List<FullNotice> getNotices() {
        return noticesRepo.findAll();
    }

    @PutMapping(value = "/api/notices", consumes = {MediaType.APPLICATION_XML_VALUE})
    public String putNotices(@RequestBody ArrayOfFullNotice array) {
        for (FullNotice fullNotice : array.getFullNotice()) {
            logger.info("Inserting: notice " + fullNotice.getId());
            noticesRepo.save(fullNotice);
        }
        return "ok";
    }

}

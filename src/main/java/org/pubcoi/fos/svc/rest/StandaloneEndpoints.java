/*
 * Copyright (c) 2021 PubCOI.org. This file is part of Fos@PubCOI.
 *
 * Fos@PubCOI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fos@PubCOI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fos@PubCOI.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.pubcoi.fos.svc.rest;

import org.pubcoi.cdm.cf.ArrayOfFullNotice;
import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.exceptions.FosBadRequestException;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.models.dto.TransactionDTO;
import org.pubcoi.fos.svc.services.*;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Profile({"standalone", "debug"})
@RestController
public class StandaloneEndpoints {

    final NoticesSvc noticesSvc;
    final TransactionOrchestrationSvc transactionOrch;
    final ScheduledSvc scheduledSvc;
    final GraphSvc graphSvc;

    public StandaloneEndpoints(
            NoticesSvc noticesSvc,
            TransactionOrchestrationSvc transactionOrch,
            ScheduledSvc scheduledSvc,
            GraphSvc graphSvc) {
        this.noticesSvc = noticesSvc;
        this.transactionOrch = transactionOrch;
        this.scheduledSvc = scheduledSvc;
        this.graphSvc = graphSvc;
    }

    @PutMapping("/api/transactions")
    public String playbackTransactions(@RequestBody List<TransactionDTO> transactions) {
        AtomicBoolean hasErrors = new AtomicBoolean(false);
        AtomicInteger numErrors = new AtomicInteger(0);
        transactions.stream()
                .sorted(Comparator.comparing(TransactionDTO::getTransactionDT))
                .forEachOrdered(transaction -> {
                    boolean success = transactionOrch.exec(transaction);
                    if (!success) {
                        hasErrors.set(true);
                        numErrors.getAndIncrement();
                    }
                });
        return String.format("Transaction playback complete with %d errors", numErrors.get());
    }

    @PostMapping("/api/contracts")
    public String uploadContracts(MultipartHttpServletRequest request) {
        MultipartFile file = request.getFile("file");
        if (null == file) {
            throw new FosBadRequestException("Empty file");
        }
        try {
            JAXBContext context = JAXBContext.newInstance(ArrayOfFullNotice.class);
            Unmarshaller u = context.createUnmarshaller();
            JAXBElement<ArrayOfFullNotice> array = (JAXBElement<ArrayOfFullNotice>) u.unmarshal(new ByteArrayInputStream(file.getBytes()));
            for (FullNotice notice : array.getValue().getFullNotice()) {
                noticesSvc.addNotice(notice);
            }
            scheduledSvc.populateFosOrgsMDBFromAwards();
            graphSvc.populateGraphFromMDB();
        } catch (IOException | JAXBException e) {
            throw new FosException("Unable to read file stream");
        }
        return "ok";
    }
}

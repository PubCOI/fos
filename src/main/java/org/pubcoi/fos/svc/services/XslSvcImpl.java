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

package org.pubcoi.fos.svc.services;

import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.cdm.cf.search.response.NoticeSearchResponse;
import org.pubcoi.cdm.pw.PWRootType;
import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class XslSvcImpl implements XslSvc {

    private static final String UNABLE_TO_TRANSFORM = "Unable to transform content";
    Source pwData1XFS;
    Source pwData2XFS;
    Source cfsDataXFS;
    Transformer pwData1XF;
    Transformer pwData2XF;
    Transformer cfsDataXF;
    JAXBContext pwCtx;
    JAXBContext searchNoticeCtx;
    JAXBContext getNoticeCtx;

    @Value("${fos.xsl.pw.step_1:xsl/CleanROI_step1.xsl}")
    String pwStep1Xsl;

    @Value("${fos.xsl.pw.step_2:xsl/CleanROI_step2.xsl}")
    String pwStep2Xsl;

    @Value("${fos.xsl.cfs:xsl/CleanNoticesSearchResponse.xsl}")
    String cfsXsl;

    @PostConstruct
    public void postConstruct() {
        try {
            this.pwCtx = JAXBContext.newInstance(PWRootType.class);
            this.searchNoticeCtx = JAXBContext.newInstance(NoticeSearchResponse.class);
            this.getNoticeCtx = JAXBContext.newInstance(FullNotice.class);
        } catch (JAXBException e) {
            throw new FosRuntimeException(String.format(
                    "Unable to instantiate JAXBContext for %s", PWRootType.class.getCanonicalName()
            ));
        }
        ClassPathResource pwStep1 = new ClassPathResource(pwStep1Xsl);
        ClassPathResource pwStep2 = new ClassPathResource(pwStep2Xsl);
        ClassPathResource cfsStep = new ClassPathResource(cfsXsl);
        checkExists(pwStep1);
        checkExists(pwStep2);
        checkExists(cfsStep);
        try (
                InputStream pwStep1InputStream = pwStep1.getInputStream();
                InputStream pwStep2InputStream = pwStep2.getInputStream();
                InputStream cfsStepInputStream = cfsStep.getInputStream()
        ) {
            this.pwData1XFS = new StreamSource(pwStep1InputStream);
            this.pwData2XFS = new StreamSource(pwStep2InputStream);
            this.cfsDataXFS = new StreamSource(cfsStepInputStream);
            this.pwData1XF = TransformerFactory.newDefaultInstance().newTransformer(pwData1XFS);
            this.pwData2XF = TransformerFactory.newDefaultInstance().newTransformer(pwData2XFS);
            this.cfsDataXF = TransformerFactory.newDefaultInstance().newTransformer(cfsDataXFS);
        } catch (IOException | TransformerConfigurationException e) {
            throw new FosRuntimeException(e.getMessage(), e);
        }
    }

    private void checkExists(ClassPathResource resource) {
        if (!resource.exists()) {
            throw new FosRuntimeException(String.format("Unable to find %s", resource.getFilename()));
        }
    }

    @Override
    public PWRootType cleanPWData(String pwRootType) {
        try (
                ByteArrayOutputStream step1Baos = new ByteArrayOutputStream();
                ByteArrayOutputStream step2Baos = new ByteArrayOutputStream()
        ) {
            // throw through converters
            Source inputStep1 = new StreamSource(new ByteArrayInputStream(pwRootType.getBytes()));
            Result step1Output = new StreamResult(step1Baos);
            pwData1XF.transform(inputStep1, step1Output);

            // now run through step 2
            Source inputStep2 = new StreamSource(new ByteArrayInputStream(step1Baos.toByteArray()));
            Result step2Output = new StreamResult(step2Baos);
            pwData2XF.transform(inputStep2, step2Output);

            // now return the object
            Unmarshaller u = pwCtx.createUnmarshaller();
            return (PWRootType) u.unmarshal(new ByteArrayInputStream(step2Baos.toByteArray()));
        } catch (JAXBException | IOException | TransformerException e) {
            throw new FosException(UNABLE_TO_TRANSFORM);
        }
    }

    @Override
    public NoticeSearchResponse cleanNoticeSearchResponse(String noticeInput) {
        return cleanMessage(noticeInput, NoticeSearchResponse.class, searchNoticeCtx);
    }

    @Override
    public FullNotice cleanGetNoticeResponse(String noticeInput) {
        return cleanMessage(noticeInput, FullNotice.class, getNoticeCtx);
    }

    private <T> T cleanMessage(String input, Class<T> type, JAXBContext context) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            Source inputStep1 = new StreamSource(new ByteArrayInputStream(input.getBytes()));
            Result step1Output = new StreamResult(baos);
            cfsDataXF.transform(inputStep1, step1Output);

            Unmarshaller u = context.createUnmarshaller();
            return type.cast(u.unmarshal(new ByteArrayInputStream(baos.toByteArray())));
        } catch (JAXBException | IOException | TransformerException e) {
            throw new FosException(UNABLE_TO_TRANSFORM);
        }
    }

}

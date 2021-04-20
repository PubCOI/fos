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

    Transformer regMemStep1Xf;
    Transformer regMemStep2Xf;
    Source regMemStep1Src;
    Source regMemStep2Src;
    JAXBContext xfOm;

    @Value("${fos.xsl.pw.step_1:xsl/CleanROI_step1.xsl}")
    String step1Xsl;

    @Value("${fos.xsl.pw.step_2:xsl/CleanROI_step2.xsl}")
    String step2Xsl;

    @PostConstruct
    public void postConstruct() {
        try {
            this.xfOm = JAXBContext.newInstance(PWRootType.class);
        } catch (JAXBException e) {
            throw new FosRuntimeException(String.format(
                    "Unable to instantiate JAXBContext for %s", PWRootType.class.getCanonicalName()
            ));
        }
        ClassPathResource step1 = new ClassPathResource(step1Xsl);
        ClassPathResource step2 = new ClassPathResource(step2Xsl);
        if (!(step1.exists() && step2.exists())) {
            throw new FosRuntimeException(String.format("Unable to find %s or %s", step1, step2));
        }
        try (
                InputStream step1is = step1.getInputStream();
                InputStream step2is = step2.getInputStream()
        ) {
            this.regMemStep1Src = new StreamSource(step1is);
            this.regMemStep2Src = new StreamSource(step2is);
            this.regMemStep1Xf = TransformerFactory.newDefaultInstance().newTransformer(regMemStep1Src);
            this.regMemStep2Xf = TransformerFactory.newDefaultInstance().newTransformer(regMemStep2Src);
        } catch (IOException | TransformerConfigurationException e) {
            throw new FosRuntimeException(e.getMessage(), e);
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
            regMemStep1Xf.transform(inputStep1, step1Output);

            // now run through step 2
            Source inputStep2 = new StreamSource(new ByteArrayInputStream(step1Baos.toByteArray()));
            Result step2Output = new StreamResult(step2Baos);
            regMemStep2Xf.transform(inputStep2, step2Output);

            // now return the object
            Unmarshaller u = xfOm.createUnmarshaller();
            return (PWRootType) u.unmarshal(new ByteArrayInputStream(step2Baos.toByteArray()));
        } catch (JAXBException | IOException | TransformerException e) {
            throw new FosException("Unable to transform content");
        }
    }

}

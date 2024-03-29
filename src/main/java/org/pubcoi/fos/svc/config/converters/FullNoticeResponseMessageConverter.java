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

package org.pubcoi.fos.svc.config.converters;

import org.pubcoi.cdm.cf.FullNotice;
import org.pubcoi.fos.svc.exceptions.core.FosCoreRuntimeException;
import org.pubcoi.fos.svc.services.XslSvc;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FullNoticeResponseMessageConverter extends AbstractHttpMessageConverter<FullNotice> {

    final JAXBContext ctx;
    final XslSvc xslSvc;

    public FullNoticeResponseMessageConverter(XslSvc xslSvc) {
        this.xslSvc = xslSvc;
        try {
            ctx = JAXBContext.newInstance(FullNotice.class);
            List<MediaType> mediaTypeList = new ArrayList<>();
            mediaTypeList.add(MediaType.APPLICATION_XML);
            super.setSupportedMediaTypes(mediaTypeList);
        } catch (JAXBException e) {
            throw new FosCoreRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return FullNotice.class.isAssignableFrom(clazz);
    }

    @Override
    protected FullNotice readInternal(Class<? extends FullNotice> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (null == contentType) {
            throw new FosCoreRuntimeException("Unable to convert data: missing charset");
        }
        Charset charset = (contentType.getCharset() != null ? contentType.getCharset() : StandardCharsets.UTF_8);
        return xslSvc.cleanGetNoticeResponse(StreamUtils.copyToString(inputMessage.getBody(), charset));
    }

    @Override
    protected void writeInternal(FullNotice fullNotice, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        throw new FosCoreRuntimeException("Should not be using this class to write FullNotice objects");
    }
}

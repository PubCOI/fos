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

package org.pubcoi.fos.svc.models.dto.search;

import org.pubcoi.fos.svc.exceptions.FosException;
import org.pubcoi.fos.svc.exceptions.FosRuntimeException;
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
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NoticeSearchResponseMessageConverter extends AbstractHttpMessageConverter<NoticeSearchResponseWrapper> {

    final JAXBContext ctx;
    final XslSvc xslSvc;

    public NoticeSearchResponseMessageConverter(XslSvc xslSvc) {
        this.xslSvc = xslSvc;
        try {
            ctx = JAXBContext.newInstance(NoticeSearchResponseWrapper.class);
            List<MediaType> mediaTypeList = new ArrayList<>();
            mediaTypeList.add(MediaType.APPLICATION_XML);
            super.setSupportedMediaTypes(mediaTypeList);
        } catch (JAXBException e) {
            throw new FosRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return NoticeSearchResponseWrapper.class.isAssignableFrom(clazz);
    }

    @Override
    protected NoticeSearchResponseWrapper readInternal(Class<? extends NoticeSearchResponseWrapper> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (null == contentType) {
            throw new FosRuntimeException("Unable to convert data: missing charset");
        }
        Charset charset = (contentType.getCharset() != null ? contentType.getCharset() : StandardCharsets.UTF_8);

        return new NoticeSearchResponseWrapper().setNoticeSearchResponse(
                xslSvc.cleanNoticeSearchResponse(StreamUtils.copyToString(inputMessage.getBody(), charset))
        );
    }

    @Override
    protected void writeInternal(NoticeSearchResponseWrapper noticeSearchResponse, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            Marshaller m = ctx.createMarshaller();
            try (OutputStream os = outputMessage.getBody()) {
                m.marshal(noticeSearchResponse, os);
            }
        } catch (JAXBException e) {
            throw new FosException(e.getMessage(), e);
        }
    }
}

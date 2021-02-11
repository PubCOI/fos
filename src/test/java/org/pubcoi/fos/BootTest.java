package org.pubcoi.fos;

import com.google.common.io.Resources;
import org.junit.Test;
import org.pubcoi.fos.models.cf.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.UUID;

public class BootTest {
    @Test
    public void test01() throws IOException, XMLStreamException, JAXBException {

        JAXBContext ctx = JAXBContext.newInstance(ArrayOfFullNotice.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        ArrayOfFullNotice arr = (ArrayOfFullNotice) unmarshaller.unmarshal(Resources.getResource("test-resources/notices.xml"));
        System.out.println("test");

    }

    @Test
    public void test02() throws JAXBException {
        ArrayOfFullNotice arrayOfFullNotice = new ArrayOfFullNotice()
                .withFullNotice(new FullNotice()
                        .withId(UUID.randomUUID().toString())
                        .withNotice(new Notice()
                                .withId(UUID.randomUUID().toString())
                                .withContactDetails(new ContactDetailsType()
                                        .withName("a names")
                                )
                        )
                        .withAwards(
                                new AwardDetailParentType().withAwardDetail(
                                        new AwardDetailParentType.AwardDetail().withId(UUID.randomUUID().toString()),
                                        new AwardDetailParentType.AwardDetail().withId(UUID.randomUUID().toString())
                                )
                        )
                );
        JAXBContext ctx = JAXBContext.newInstance(ArrayOfFullNotice.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(arrayOfFullNotice, System.out);
    }
}
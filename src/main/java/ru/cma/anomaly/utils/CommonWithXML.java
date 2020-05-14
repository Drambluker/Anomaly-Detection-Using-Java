package ru.cma.anomaly.utils;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.cma.core.utils.Common;

public class CommonWithXML extends Common {
  private static Logger log = LoggerFactory.getLogger(CommonWithXML.class);

  public static <T> String toFormattedXmlOrNull(T src) {
    try {
      JAXBContext context = JAXBContext.newInstance(src.getClass());
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      StringWriter sw = new StringWriter();
      m.marshal(src, sw);
      return sw.toString();
    } catch (JAXBException e) {
      log.error("Can't serialize object", e);
      return null;
    }
  }
}

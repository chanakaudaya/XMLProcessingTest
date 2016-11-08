package org.wso2.xml;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.InputStream;

public class TestBasicStaxParsing {

    public static void main(String[] args) throws Exception {
        (new TestBasicStaxParsing()).execute("test.xml");
    }

    private void execute(String xmlFileName) throws Exception {

        //InputStream xmlInputStream = getClass().getResourceAsStream(xmlFileName);
        InputStream xmlInputStream = new FileInputStream(xmlFileName);
        //Load Aalto's StAX parser factory
        XMLInputFactory2 xmlInputFactory =
                (XMLInputFactory2) XMLInputFactory.newFactory("javax.xml.stream.XMLInputFactory",
                        this.getClass().getClassLoader());
        //XMLInputFactory2 xmlInputFactory = (XMLInputFactory2)XMLInputFactory.newInstance();
        XMLStreamReader2 xmlStreamReader = (XMLStreamReader2) xmlInputFactory.createXMLStreamReader(xmlInputStream);
        while (xmlStreamReader.hasNext()) {
            int eventType = xmlStreamReader.next();
            switch (eventType) {
                case XMLEvent.START_ELEMENT:
                    System.out.print("<" + xmlStreamReader.getName().toString() + ">");
                    break;
                case XMLEvent.CHARACTERS:
                    System.out.print(xmlStreamReader.getText());
                    break;
                case XMLEvent.END_ELEMENT:
                    System.out.println("</" + xmlStreamReader.getName().toString() + ">");
                    break;
                default:
                    //do nothing
                    break;
            }
        }

    }

}

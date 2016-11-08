package org.wso2.xml;

import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import static org.junit.Assert.assertEquals;


public class TestXMLProcessingPerf {

    private String filename = "test3.xml";
    private int repeatCount = 10;

    @Test
    public void testUnMarshallUsingJAXB() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            JAXBContext jc = JAXBContext.newInstance(PersonList.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            PersonList obj = (PersonList) unmarshaller.unmarshal(new File(filename));
            for (Person p : obj.getPersons()) {
                System.out.println(p.getId() + " : " + p.getName());
            }

        }

    }

    @Test
    public void testUnMarshallUsingJAXBStreamSource() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            JAXBContext jc = JAXBContext.newInstance(PersonList.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StreamSource source = new StreamSource(new File(filename));
            PersonList obj = (PersonList) unmarshaller.unmarshal(source);
            for (Person p : obj.getPersons()) {
                System.out.println(p.getId() + " : " + p.getName());
            }
        }
    }

    @Test
    public void testUnMarshallingWithStAX() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            FileReader fr = new FileReader(filename);
            JAXBContext jc = JAXBContext.newInstance(PersonList.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            XMLStreamReader xmler = xmlif.createXMLStreamReader(fr);
            PersonList obj = (PersonList) unmarshaller.unmarshal(xmler);
            for (Person p : obj.getPersons()) {
                System.out.println(p.getId() + " : " + p.getName());
            }
        }
    }

    @Test
    public void testParsingWithDom() throws Exception {
        for (int j = 0; j < repeatCount; j++) {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(filename);
            doc.getDocumentElement().normalize();
            List<Person> personsAsList = new ArrayList<Person>();
            NodeList persons = doc.getElementsByTagName("person");
            for (int i = 0; i < persons.getLength(); i++) {
                Element person = (Element) persons.item(i);

                System.out.println(person.getElementsByTagName("id").item(0).getTextContent() + " - " +
                        person.getElementsByTagName("name").item(0).getTextContent());
            }
        }

    }

    @Test
    public void testParsingWithSAX() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            final List<Person> persons = new ArrayList<Person>();
            DefaultHandler handler = new DefaultHandler() {
                boolean bpersonId = false;
                boolean bpersonName = false;

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("id")) {
                        bpersonId = true;
                        Person person = new Person();
                        persons.add(person);
                    } else if (qName.equalsIgnoreCase("name")) {
                        bpersonName = true;
                    }
                }

                public void endElement(String uri, String localName, String qName) throws SAXException {
                }

                public void characters(char ch[], int start, int length) throws SAXException {
                    if (bpersonId) {
                        String personID = new String(ch, start, length);
                        bpersonId = false;
                        Person person = persons.get(persons.size() - 1);
                        person.setId(personID);
                    } else if (bpersonName) {
                        String name = new String(ch, start, length);
                        bpersonName = false;
                        Person person = persons.get(persons.size() - 1);
                        person.setName(name);
                    }
                }
            };
            saxParser.parse(filename, handler);
            for (Person p : persons) {
                System.out.println(p.getId() + " : " + p.getName());
            }
        }
    }

    @Test
    public void testAaltoSAXParsing() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            //InputStream xmlInputStream = getClass().getResourceAsStream(xmlFileName);
            InputStream xmlInputStream = new FileInputStream(filename);
            //Load Aalto's StAX parser factory
            XMLInputFactory2 xmlInputFactory = (XMLInputFactory2) XMLInputFactory.newFactory("javax.xml.stream.XMLInputFactory", this.getClass().getClassLoader());
            //XMLInputFactory2 xmlInputFactory = (XMLInputFactory2)XMLInputFactory.newInstance();
            xmlInputFactory.configureForSpeed();
            XMLStreamReader2 xmlStreamReader = (XMLStreamReader2) xmlInputFactory.createXMLStreamReader(xmlInputStream);
            while (xmlStreamReader.hasNext()) {
                int eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLEvent.START_ELEMENT:
                        //System.out.print("<" + xmlStreamReader.getName().toString() + ">");
                        break;
                    case XMLEvent.CHARACTERS:
                        System.out.print(xmlStreamReader.getText());
                        break;
                    case XMLEvent.END_ELEMENT:
                        //System.out.println("</" + xmlStreamReader.getName().toString() + ">");
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
        }


    }

    @Test
    public void testAxiomParserFull() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            StAXOMBuilder staxBuilder = new StAXOMBuilder(new FileInputStream(filename));


            OMElement documentElement = staxBuilder.getDocumentElement();
            Iterator nodeElement = documentElement.getChildrenWithName(new QName("person"));

            while (nodeElement.hasNext()) {
                OMElement om = (OMElement) nodeElement.next();
                System.out.println(((OMElement) om.getChildrenWithLocalName("id").next()).getText() + " - "
                        + ((OMElement) om.getChildrenWithLocalName("name").next()).getText());
            }
        }
    }

    @Test
    public void testAxiomParserWithFragements() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            // Create a builder for the file and get the root element
            InputStream in = new FileInputStream(filename);
            // Create an XMLStreamReader without building the object model
            XMLStreamReader reader =
                    OMXMLBuilderFactory.createOMBuilder(in).getDocument().getXMLStreamReader(false);
            while (reader.hasNext()) {
                if (reader.getEventType() == XMLStreamReader.START_ELEMENT &&
                        reader.getName().equals(new QName("person"))) {
                    // A matching START_ELEMENT event was found. Build a corresponding OMElement.
                    OMElement element =
                            OMXMLBuilderFactory.createStAXOMBuilder(reader).getDocumentElement();
                    // Make sure that all events belonging to the element are consumed so
                    // that the XMLStreamReader points to a well defined location (namely the
                    // event immediately following the END_ELEMENT event).
                    element.build();
                    // Now process the element.
                    processFragment(element);
                } else {
                    reader.next();
                }
            }
            // Because Axiom uses deferred parsing, the stream must be closed AFTER
            // processing the document (unless OMElement#build() is called)
            in.close();
        }

    }

    private void processFragment(OMElement element) {
        OMElement el1 = (OMElement) element.getChildrenWithLocalName("id").next();
        OMElement el2 = (OMElement) element.getChildrenWithLocalName("name").next();
        System.out.println(el1.getText() +
                " - " + el2.getText());

    }

    @Test
    public void testAsyncUsingWrapperAalto() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            try {
                InputStream xmlInputStream = new FileInputStream(filename);
                String xmlString = getStringFromInputStream(xmlInputStream);

                AsyncXMLInputFactory xmlInputFactory = new InputFactoryImpl();
                AsyncXMLStreamReader asyncXMLStreamReader = xmlInputFactory.createAsyncForByteArray();

                AsyncReaderWrapper wrapper = new AsyncReaderWrapperForByteArray(asyncXMLStreamReader, 1, xmlString);

                int type = wrapper.nextToken();
                while (type != XMLEvent.END_DOCUMENT) {
                    switch (type) {
                        case XMLEvent.START_DOCUMENT:
                            // System.out.println("start document");
                            break;
                        case XMLEvent.START_ELEMENT:
                            System.out.println("-");
                            //System.out.println("start element: " + asyncXMLStreamReader.getName());
                            break;
                        case XMLEvent.CHARACTERS:
                            if (!(asyncXMLStreamReader.getText().equalsIgnoreCase(" "))) {
                                System.out.print(asyncXMLStreamReader.getText());
                            }
                            break;
                        case XMLEvent.END_ELEMENT:
                            //System.out.println("-");
                            //System.out.println("end element: " + asyncXMLStreamReader.getName());
                            break;
                        case XMLEvent.END_DOCUMENT:
                            //System.out.println("end document");
                            break;
                        default:
                            break;
                    }
                    type = wrapper.nextToken();
                }
                asyncXMLStreamReader.close();


            } catch (RuntimeException t) {
                t.printStackTrace();
            }
        }

    }

    private String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    @Test
    public void testBasicStaxParsing() throws Exception {
        for (int i = 0; i < repeatCount; i++) {
            //InputStream xmlInputStream = getClass().getResourceAsStream(xmlFileName);
            InputStream xmlInputStream = new FileInputStream(filename);
            //Load Aalto's StAX parser factory
            XMLInputFactory2 xmlInputFactory = (XMLInputFactory2) XMLInputFactory.newFactory("javax.xml.stream.XMLInputFactory", this.getClass().getClassLoader());
            //XMLInputFactory2 xmlInputFactory = (XMLInputFactory2)XMLInputFactory.newInstance();
            XMLStreamReader2 xmlStreamReader = (XMLStreamReader2) xmlInputFactory.createXMLStreamReader(xmlInputStream);
            while (xmlStreamReader.hasNext()) {
                int eventType = xmlStreamReader.next();
                switch (eventType) {
                    case XMLEvent.START_ELEMENT:
                        //System.out.print("<" + xmlStreamReader.getName().toString() + ">");
                        break;
                    case XMLEvent.CHARACTERS:
                        System.out.print(xmlStreamReader.getText());
                        break;
                    case XMLEvent.END_ELEMENT:
                        //System.out.println("</" + xmlStreamReader.getName().toString() + ">");
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
        }
    }

}

package org.wso2.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jaxen.XPath;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.dom4j.Dom4jXPath;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class TestJaxenXPath {

    @Test
    public void testXPath() throws Exception{

        String filename = "test3.xml";
        String xpathSt = "//persons/person[100]/id";
        int repeatCount = 10;

        for(int i=0; i<repeatCount; i++) {

            File xmlFile = new File(filename);
            SAXReader reader = new SAXReader();

            Document dom4jDocument = null;
            XPath path = null;
            List<Element> results = null;

            try {

                dom4jDocument = reader.read(xmlFile);
                path = new Dom4jXPath(xpathSt);
                results = path.selectNodes(dom4jDocument);

                for (Element element : results) {

                    System.out.println(element.getData());
                }

            } catch (JaxenException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

    }
}

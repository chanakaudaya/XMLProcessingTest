package org.wso2.xml;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.xpath.XPathFactoryImpl;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.xpath.XPathAPI;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;


public class TestXPathProcessingPerf {

    private String filename = "test.xml";
    private String xpathSt = "//persons/person[last()]/id";

    private int iteration = 10;

    // The boolean argument indicates whether this is the licensed edition or not.
    private Processor processor = new Processor(false);
    private XPathCompiler xPathCompiler = processor.newXPathCompiler();

    @Test
    public void testAxiomXPath() throws Exception {
        for (int i = 0; i < iteration; i++) {
            StAXOMBuilder staxBuilder = new StAXOMBuilder(new FileInputStream(filename));
            OMElement documentElement = staxBuilder.getDocumentElement();
            TestXPath testXPath = new TestXPath(xpathSt);
            System.out.println(testXPath.evaluate(documentElement));
        }
    }

    @Test
    public void testSaxonXPathwithAxiom() throws Exception {
        for (int i = 0; i < iteration; i++) {
            StAXOMBuilder staxBuilder = new StAXOMBuilder(new FileInputStream(filename));
            OMElement documentElement = staxBuilder.getDocumentElement();
            StringReader reader = new StringReader(documentElement.toString());
            DocumentBuilder builder = processor.newDocumentBuilder();
            XdmNode doc = builder.build(new StreamSource(reader));
            XPathSelector selector = xPathCompiler.compile(xpathSt).load();
            selector.setContextItem(doc);
            System.out.println(selector.evaluate().toString());
        }
    }

    @Test
    public void testSaxonXPath() throws XPathFactoryConfigurationException, XPathExpressionException, Exception {
        for (int i = 0; i < iteration; i++) {
            System.setProperty("javax.xml.xpath.XPathFactory:" +
                    NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
            XPathFactory xPathFactory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
            XPath xPath = xPathFactory.newXPath();
            InputSource inputSource = new InputSource(new File(filename).toURI().toString());
            SAXSource saxSource = new SAXSource(inputSource);
            Configuration config = ((XPathFactoryImpl) xPathFactory).getConfiguration();
            DocumentInfo document = config.buildDocument(saxSource);
            XPathExpression xPathExpression = xPath.compile(xpathSt);
            List matches = (List) xPathExpression.evaluate(document, XPathConstants.NODESET);
            if (matches != null) {
                for (Iterator iter = matches.iterator(); iter.hasNext(); ) {
                    NodeInfo node = (NodeInfo) iter.next();
                    System.out.println(node.getDisplayName() + " - " + node.getStringValue());
                }
            }
        }
    }

    @Test
    public void testSaxonXPathXOM() throws XPathFactoryConfigurationException, XPathExpressionException, Exception {
        for (int i = 0; i < iteration; i++) {
            System.setProperty("javax.xml.xpath.XPathFactory:" +
                    NamespaceConstant.OBJECT_MODEL_XOM, "net.sf.saxon.xpath.XPathFactoryImpl");
            XPathFactory xPathFactory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_XOM);
            XPath xPath = xPathFactory.newXPath();
            InputSource inputSource = new InputSource(new File(filename).toURI().toString());
            SAXSource saxSource = new SAXSource(inputSource);
            Configuration config = ((XPathFactoryImpl) xPathFactory).getConfiguration();
            DocumentInfo document = config.buildDocument(saxSource);
            XPathExpression xPathExpression = xPath.compile(xpathSt);
            List matches = (List) xPathExpression.evaluate(document, XPathConstants.NODESET);
            if (matches != null) {
                for (Iterator iter = matches.iterator(); iter.hasNext(); ) {
                    NodeInfo node = (NodeInfo) iter.next();
                    System.out.println(node.getDisplayName() + " - " + node.getStringValue());
                }
            }
        }
    }

    @Test
    public void testSaxonXPathDOM4J() throws XPathFactoryConfigurationException, XPathExpressionException, Exception {
        for (int i = 0; i < iteration; i++) {
            System.setProperty("javax.xml.xpath.XPathFactory:" +
                    NamespaceConstant.OBJECT_MODEL_DOM4J, "net.sf.saxon.xpath.XPathFactoryImpl");
            XPathFactory xPathFactory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_DOM4J);
            XPath xPath = xPathFactory.newXPath();
            InputSource inputSource = new InputSource(new File(filename).toURI().toString());
            SAXSource saxSource = new SAXSource(inputSource);
            Configuration config = ((XPathFactoryImpl) xPathFactory).getConfiguration();
            DocumentInfo document = config.buildDocument(saxSource);
            XPathExpression xPathExpression = xPath.compile(xpathSt);
            List matches = (List) xPathExpression.evaluate(document, XPathConstants.NODESET);
            if (matches != null) {
                for (Iterator iter = matches.iterator(); iter.hasNext(); ) {
                    NodeInfo node = (NodeInfo) iter.next();
                    System.out.println(node.getDisplayName() + " - " + node.getStringValue());
                }
            }
        }
    }

    @Test
    public void testSaxonXPathJDOM() throws XPathFactoryConfigurationException, XPathExpressionException, Exception {
        for (int i = 0; i < iteration; i++) {
            System.setProperty("javax.xml.xpath.XPathFactory:" +
                    NamespaceConstant.OBJECT_MODEL_JDOM, "net.sf.saxon.xpath.XPathFactoryImpl");
            XPathFactory xPathFactory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_JDOM);
            XPath xPath = xPathFactory.newXPath();
            InputSource inputSource = new InputSource(new File(filename).toURI().toString());
            SAXSource saxSource = new SAXSource(inputSource);
            Configuration config = ((XPathFactoryImpl) xPathFactory).getConfiguration();
            DocumentInfo document = config.buildDocument(saxSource);
            XPathExpression xPathExpression = xPath.compile(xpathSt);
            List matches = (List) xPathExpression.evaluate(document, XPathConstants.NODESET);
            if (matches != null) {
                for (Iterator iter = matches.iterator(); iter.hasNext(); ) {
                    NodeInfo node = (NodeInfo) iter.next();
                    System.out.println(node.getDisplayName() + " - " + node.getStringValue());
                }
            }
        }
    }

    @Test
    public void testSaxonXPathAXIOM() throws XPathFactoryConfigurationException, XPathExpressionException, Exception {
        for (int i = 0; i < iteration; i++) {
            System.setProperty("javax.xml.xpath.XPathFactory:" +
                    NamespaceConstant.OBJECT_MODEL_AXIOM, "net.sf.saxon.xpath.XPathFactoryImpl");
            XPathFactory xPathFactory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_AXIOM);
            XPath xPath = xPathFactory.newXPath();
            InputSource inputSource = new InputSource(new File(filename).toURI().toString());
            SAXSource saxSource = new SAXSource(inputSource);
            Configuration config = ((XPathFactoryImpl) xPathFactory).getConfiguration();
            DocumentInfo document = config.buildDocument(saxSource);
            XPathExpression xPathExpression = xPath.compile(xpathSt);
            List matches = (List) xPathExpression.evaluate(document, XPathConstants.NODESET);
            if (matches != null) {
                for (Iterator iter = matches.iterator(); iter.hasNext(); ) {
                    NodeInfo node = (NodeInfo) iter.next();
                    System.out.println(node.getDisplayName() + " - " + node.getStringValue());
                }
            }
        }
    }

    @Test
    public void testJavaXPath() throws Exception {
        for (int i = 0; i < iteration; i++) {
            // 1. Instantiate an XPathFactory.
            javax.xml.xpath.XPathFactory factory =
                    javax.xml.xpath.XPathFactory.newInstance();

            // 2. Use the XPathFactory to create a new XPath object
            javax.xml.xpath.XPath xpath = factory.newXPath();

            // 3. Compile an XPath string into an XPathExpression
            javax.xml.xpath.XPathExpression expression = xpath.compile(xpathSt);

            // 4. Evaluate the XPath expression on an input document
            String result = expression.evaluate(new org.xml.sax.InputSource(filename));

            System.out.println(result);
        }
    }

    @Test
    public void textXalanXPath() throws Exception {
        for (int i = 0; i < iteration; i++) {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document document = builder.parse(filename);
            long start = System.currentTimeMillis();
            try {

//            for(int ii=0;ii<100;ii++)
//            {

                Node node = XPathAPI.selectSingleNode(document, xpathSt);
                if (node != null) {
                    String val = node.getTextContent();
                    System.out.println(val);
                }
//            }
            } catch (Exception e) {
                e.printStackTrace();
            }


            long end = System.currentTimeMillis();

//          System.err.println("W3C xpath  time :"  + (end-start) );
            System.out.print(end - start);
        }
    }


}

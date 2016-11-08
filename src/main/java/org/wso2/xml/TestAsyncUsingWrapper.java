package org.wso2.xml;

import java.io.*;


import javax.xml.stream.events.XMLEvent;

import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;

public class TestAsyncUsingWrapper {

    private void execute(String xmlFileName) throws Exception {
        try{
            InputStream xmlInputStream = new FileInputStream(xmlFileName);
            String xmlString = getStringFromInputStream(xmlInputStream);

            AsyncXMLInputFactory xmlInputFactory = new InputFactoryImpl();
            AsyncXMLStreamReader asyncXMLStreamReader = xmlInputFactory.createAsyncForByteArray();

            AsyncReaderWrapper wrapper = new AsyncReaderWrapperForByteArray(asyncXMLStreamReader,1, xmlString);

            int type = wrapper.nextToken();
            while(type != XMLEvent.END_DOCUMENT) {
                switch (type) {
                    case XMLEvent.START_DOCUMENT:
                        System.out.println("start document");
                        break;
                    case XMLEvent.START_ELEMENT:
                        System.out.println("start element: " + asyncXMLStreamReader.getName());
                        break;
                    case XMLEvent.CHARACTERS:
                        System.out.println("    characters: " + asyncXMLStreamReader.getText());
                        break;
                    case XMLEvent.END_ELEMENT:
                        System.out.println("end element: " + asyncXMLStreamReader.getName());
                        break;
                    case XMLEvent.END_DOCUMENT:
                        System.out.println("end document");
                        break;
                    default:
                        break;
                }
                type = wrapper.nextToken();
            }
            asyncXMLStreamReader.close();



        } catch(RuntimeException t) {
            t.printStackTrace();
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




    public static void main(String[] args) throws Exception {
        (new TestAsyncUsingWrapper()).execute("test.xml");
    }

}

package org.red5.server.utils;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class XMLUtils {
	
    protected static Log log =
        LogFactory.getLog(XMLUtils.class.getName());
	
	public static Document stringToDoc(String str) throws IOException {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return db.parse(new InputSource(str));
		} catch(Exception ex){
			throw new IOException("Error converting from string to doc "+ex.getMessage());
		}
	}
	
	public static String docToString(Document dom){
		return XMLUtils.docToString1(dom);
	}
	
	/**
     * Convert a DOM tree into a String using Dom2Writer
     */
	public static String docToString1(Document dom){
		StringWriter sw = new StringWriter();
		DOM2Writer.serializeAsXML(dom,sw);
		return sw.toString();
	}
	
	/**
     * Convert a DOM tree into a String using transform
     */
    public static String docToString2(Document domDoc) throws IOException {
        try {
		TransformerFactory transFact = TransformerFactory.newInstance();
         Transformer trans = transFact.newTransformer();
         trans.setOutputProperty(OutputKeys.INDENT, "no");
         StringWriter sw = new StringWriter();
         Result result = new StreamResult(sw);
         trans.transform(new DOMSource(domDoc), result);
         return sw.toString();
        } catch (Exception ex){
			throw new IOException("Error converting from doc to string "+ex.getMessage());
        }
    }
    
}

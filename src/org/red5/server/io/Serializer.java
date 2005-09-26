package org.red5.server.io;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.BeanMap;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.utils.XMLUtils;
import org.springframework.beans.BeanUtils;
import org.w3c.dom.Document;

public class Serializer {

	protected static Log log =
        LogFactory.getLog(Serializer.class.getName());
	
	// Any
	public void serialize(Output out, Object any) {
		// TODO Auto-generated method stub
		log.debug("serialize");
		if(writeBasic(out,any)){
			log.debug("write basic");
			return;
		}
		
		// Extension point
		if(out.hasReference(any)){
			log.debug("write ref");
			out.writeReference(any);
			return;
		}

		log.debug("Store reference: "+any);
		out.storeReference(any);
		if(!writeComplex(out,any))
			log.debug("Unable to serialize: "+any);
	}
		
	// Basic
	protected boolean writeBasic(Output out, Object basic){
		if(basic==null) 
			out.writeNull();
		else if(basic instanceof Boolean) 
			out.writeBoolean((Boolean) basic);
		else if(basic instanceof Number)
			out.writeNumber((Number) basic);
		else if(basic instanceof String)
			out.writeString((String) basic);
		else if(basic instanceof Date)
			out.writeDate((Date) basic);
		else return false;
		return true;
	}
	
	// Complex
	public boolean writeComplex(Output out, Object complex){
		log.debug("writeComplex");
		if(writeArrayType(out,complex)) return true;
		else if(writeXMLType(out,complex)) return true;
		else if(writeCustomType(out, complex)) return true;
		else if(writeObjectType(out,complex)) return true;
		else return false;
	}
	
	// Arrays, Collections, etc
	protected boolean writeArrayType(Output out, Object arrType){
		log.debug("writeArrayType");
		if(arrType instanceof Collection) {
			
			writeCollection(out, (Collection) arrType);
		} else if(arrType instanceof Iterator) {
			
			writeIterator(out, (Iterator) arrType);
		// Need a replacement here. Perhaps copy from spring BeanUtils to util class
		} else if(BeanUtils.isPrimitiveArray(arrType.getClass())) {
			writePrimitiveArray(out, arrType);
		} else if(arrType instanceof Object[]) {
			writeObjectArray(out, (Object[]) arrType);
		} else return false;
		return true;
	}
	
	protected void writeCollection(Output out, Collection col){
		log.debug("writeCollection");
		out.writeStartArray(col.size());
		Iterator it = col.iterator();
		boolean isFirst = true;
		while(it.hasNext()){
			if(!isFirst) out.markElementSeparator();
			else isFirst = false;
			serialize(out, it.next());
		}
		out.markEndArray();
	}
	
	protected void writePrimitiveArray(Output out, Object array){
		//out.writeS
		log.debug("write primitive array");
		out.writeStartArray(Array.getLength(array));
		Iterator it = IteratorUtils.arrayIterator(array);
		while(it.hasNext()){
			serialize(out, it.next());
			if(it.hasNext()) out.markElementSeparator();
		}
		out.markEndArray();
	}
	
	protected void writeObjectArray(Output out, Object[] array){
		//out.writeS
		log.debug("write object array");
		out.writeStartArray(array.length);

		for (int i = 0; i < array.length; i++) {
			if(i>0) out.markElementSeparator();
			//log.info(i);
			serialize(out, array[i]);
		}

		out.markEndArray();
	}
	
	protected void writeIterator(Output out,Iterator it){
		log.debug("writeIterator");
		LinkedList list = new LinkedList();
		while(it.hasNext()) list.addLast(it.next());
		writeCollection(out, list);
	}
	
	// XML
	protected boolean writeXMLType(Output out, Object xml){		
		log.debug("writeXMLType");
		if(xml instanceof Document) 
			writeDocument(out, (Document) xml);
		else return false;
		return true;
	}
	
	protected void writeDocument(Output out, Document doc){
		out.writeXML(XMLUtils.docToString(doc));
	}
	
	// Object
	protected boolean writeObjectType(Output out, Object obj){		
		if(obj instanceof Map) 
			writeMap(out, (Map) obj);
		else writeBean(out, obj);
		return true;
	}
	
	public void writeMap(Output out, Map map){
		log.debug("writeMap");
		out.writeStartObject(null);
		Set set = map.entrySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			out.writePropertyName(entry.getKey().toString());
			serialize(out,entry.getValue());
			if(it.hasNext()) out.markPropertySeparator();
		}
		out.markEndObject();
	}
	
	public void writeBean(Output out, Object bean){
		log.debug("writeBean");
		out.writeStartObject(bean.getClass().getName());
		BeanMap beanMap = new BeanMap(bean);
		Set set = beanMap.entrySet();
		Iterator it = set.iterator();
		while(it.hasNext()){
			BeanMap.Entry entry = (BeanMap.Entry) it.next();
			if(entry.getKey().toString().equals("class")) continue;
			out.writePropertyName(entry.getKey().toString());
			
			log.info(entry.getKey().toString()+" = "+entry.getValue());
			serialize(out,entry.getValue());
			if(it.hasNext()) out.markPropertySeparator();
		}
		out.markEndObject();
	}
	
	// Extension points
	
	public Object preProcessExtension(Object any){
		// Does nothing right now but will later
		return any;
	}
	
	protected boolean writeCustomType(Output out, Object obj){
		if(out.isCustom(obj)){
			out.writeCustom(obj);
			return true;
		} else return false;
	}

}

package com.liuqi.utils;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.util.StringUtils;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * xml与bean的转转
 * @author tanyan
 * 2016-9-10 上午10:09:12
 */
public class XmlAndBeanUtil {
	
	/**
	 * 对象转换为xml字符串
	 * @param obj 要装换对象
	 * @param formatted 是否格式化
	 * @param charset 编码格式 "UTF-8"
	 * @throws Exception 
	 * @returna
	 */
	public static String converString(Object obj,boolean formatted,String charset) throws Exception{
		 if(StringUtils.isEmpty(charset)){
			charset="UTF-8";
		 }
		 String result = null;
         JAXBContext context = JAXBContext.newInstance(obj.getClass());
         Marshaller marshaller = context.createMarshaller();
         //决定是否在转换成xml时同时进行格式化（即按标签自动换行，否则即是一行的xml）
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
        //xml的编码方式
         marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);

         StringWriter writer = new StringWriter();
         marshaller.marshal(obj, writer);
         result = writer.toString();
        
        return result;
	}
	
	/**
	 * 对象转换为xml字符串
	 * @param obj 要装换对象
	 * @param formatted 是否格式化
	 * @param charset 编码格式 "UTF-8"
	 * @param xmlns 设置命名空间前缀
	 * @throws Exception 
	 * @returna
	 */
	public static String converString(Object obj,boolean formatted,String charset,final String xmlns) throws Exception{
		 if(StringUtils.isEmpty(charset)){
			charset="UTF-8";
		 }
		 String result = null;
         JAXBContext context = JAXBContext.newInstance(obj.getClass());
         Marshaller marshaller = context.createMarshaller();
         //决定是否在转换成xml时同时进行格式化（即按标签自动换行，否则即是一行的xml）
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
        //xml的编码方式
         marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);
         NamespacePrefixMapper mapper = new NamespacePrefixMapper() {
			@Override
			public String getPreferredPrefix(String namespaceUri, String suggestion,
					boolean requirePrefix) {
				return xmlns;
			}
		};
		 marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper); 
         StringWriter writer = new StringWriter();
         marshaller.marshal(obj, writer);
         result = writer.toString();
        
        return result;
	}
    
    /**
     * xml转化为对象
     * @param xmlStr 字符串
     * @param c 转换的对象
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public static <T>  T conver2Obj(String xmlStr,Class<T> c) throws Exception{
	     JAXBContext context = JAXBContext.newInstance(c);
	     Unmarshaller unmarshaller = context.createUnmarshaller();
	     return (T) unmarshaller.unmarshal(new StringReader(xmlStr));
    }
    
    /**
     * xml转化为对象
     * @param in 字符串
     * @param c 转换的对象
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T>  T conver2Obj(InputStream in,Class<T> c) throws Exception{
    	JAXBContext context = JAXBContext.newInstance(c);
	    Unmarshaller unmarshaller = context.createUnmarshaller();
	    T t=(T) unmarshaller.unmarshal(in);
	    in.close();
	    return t;
    }
}

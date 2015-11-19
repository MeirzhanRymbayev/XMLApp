package com.epam.mrymbayev.parser;

import com.epam.mrymbayev.annotation.IsSimleType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class sAxParser implements Parser {



    public <T> List<T> parseList(InputStream in, Class<T> clazz) throws IOException, SAXException, ParserConfigurationException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(in, new MySAXHandler());

        Field[] clazzDeclaredFields = clazz.getDeclaredFields();
        Method[] clazzDeclaredMethods = clazz.getDeclaredMethods();
        //List<Object> simpleTypeFields = new ArrayList<Object>();// Here we put simple type Fields
        List<Object> complexTypeFields = new ArrayList<Object>(); // Here we put complex type Fields
        for (Field field: clazzDeclaredFields) {
            if(field.getAnnotation(IsSimleType.class).value()){
                field.se
            } else{
                field
            }
        }

        return null;
    }

    private <T> Class<T> setObjectFields(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T t;
        Field[] clazzDeclaredFields = clazz.getDeclaredFields();
        Method[] clazzDeclaredMethods = clazz.getDeclaredMethods();
        for(Field clazzField : clazzDeclaredFields){
            if(clazzField.getAnnotation(IsSimleType.class).value()){

            } else {

                setObjectFields(clazzField.getClass());
            }
        }
    }



    public class MySAXHandler extends DefaultHandler{
        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            super.ignorableWhitespace(ch, start, length);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }
    }
}

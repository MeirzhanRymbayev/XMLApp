package com.epam.mrymbayev.parser;

import com.epam.mrymbayev.parser.converter.Converter;
import com.epam.mrymbayev.parser.exception.SetterException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;

public class UniversalSAXParser implements Parser {
    private Class rootClass;
    private Class currentObjClass; // Voucher example
    List<Object> listObject;
    private String qNameCurrent;

    final Set<Class> sympleTypesSet = new HashSet<Class>(Arrays.asList(
            String.class,
            Integer.class, int.class,
            Character.class, char.class,
            Boolean.class, boolean.class,
            Double.class, double.class,
            Float.class, float.class));

    public <T> List<T> parseList(InputStream in, Class<T> clazz) throws IOException, SAXException, ParserConfigurationException {
        listObject = new ArrayList<>();
                rootClass = clazz;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(in, new MySAXHandler());

        return (List<T>) listObject;
    }

    private <T> T parse(Class<T> clazz){

        return null;

    }

    /**
     * The method returns capitalized field name of the object to construct method name
     *
     * @param fieldName The fieldName of the object
     * @return The method returns capitalized field name of the object to construct method name
     */
    private String getCapitalizedFieldName(String fieldName) {
        StringBuilder sb = new StringBuilder(fieldName);
        sb = sb.replace(0, 1, String.valueOf(sb.charAt(0)).toUpperCase());
        return sb.toString();
    }

    class MySAXHandler extends DefaultHandler {
        Object rootObject;
        Deque<Object> objectsStack;
        Object currentObject;
        String className;
        private StringBuilder characters = new StringBuilder();

        @Override
        public void startDocument() throws SAXException {
            objectsStack = new ArrayDeque<>();
            className = rootClass.getSimpleName();  //имя тега(тип объекта)
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            qNameCurrent = qName;
            characters.setLength(0);     // Чистим characters()
            if (className.equals(qName)) {
                try {
                    rootObject = rootClass.newInstance();
                    objectsStack.add(rootObject);

                    ParameterizedType genericType = (ParameterizedType) rootObject.getClass().getDeclaredField("list").getGenericType();

                    Type[] actualTypeArguments = genericType.getActualTypeArguments();
                    Type typeIL = actualTypeArguments[0]; // Type of object into list
                    Class typeILClass = (Class) typeIL;  // Here was most important decision for me
                    objectsStack.add(typeILClass.newInstance());
                    //setObjectFields(qName); //добавили в очередь и вызвали сетОбжектПоля
                } catch (IllegalAccessException | InstantiationException|NoSuchFieldException  ignored) {
                    ignored.printStackTrace();
                }
            }
        }

        private void setObjectFields(String qNameCurrent, String characters) throws IllegalAccessException, InstantiationException,
                                                                 NoSuchMethodException, SetterException {
            currentObject = objectsStack.getLast(); //Берем последний элемент в очереди
            currentObjClass = currentObject.getClass(); //достаем его объект класс

            Field[] clazzFields = currentObjClass.getDeclaredFields(); // достаем поля этого объекта

            for (Field clazzField : clazzFields) {
                    String fieldName = clazzField.getName(); // берем имя поля. Должен быть какой то конвеншн
                if(qNameCurrent.equals(fieldName)) {
                    if (sympleTypesSet.contains(clazzField.getType())) { //является ли поле простым? если да
                        String capitalizedFieldName = getCapitalizedFieldName(fieldName);
                        Method setMethod = currentObjClass.getMethod("set" + capitalizedFieldName, clazzField.getType());
                        System.out.println("clazzField.getType() = " + clazzField.getType());
                        try {
                            setMethod.invoke(currentObject, Converter.convert(clazzField.getType(), characters));
                            fieldName = "";
                        } catch (InvocationTargetException e) {
                            throw new SetterException();
                        }
                    } else { // Иначе если элемент сложный то заносим его в деку и вызываем setObjectFields()
                        Object complexType = clazzField.getType().newInstance(); //Создаем объект из филда
                        objectsStack.addLast(complexType); //кидаем его в деку
                    }
                }
            }

            listObject.add(currentObject); //TODO: Проверить здесь ли нужно отправлять объект в лист
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            characters = characters.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
                try {
                    qNameCurrent = qName;
                    setObjectFields(qNameCurrent, characters.toString());
                } catch (IllegalAccessException | InstantiationException |
                        NoSuchMethodException | SetterException ignored) {
                    ignored.printStackTrace();
                }
            if (className.equals(qName)) {
                objectsStack.removeLast(); //удаляем заполненный элемент
            }

        }

        @Override
        public void endDocument() throws SAXException {
        }
    }
}

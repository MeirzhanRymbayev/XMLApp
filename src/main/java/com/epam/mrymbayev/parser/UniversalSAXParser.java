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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class UniversalSAXParser implements Parser {
    private Class tClass;
    List<Object> listObject;

    final Set<Class> sympleTypesSet = new HashSet<Class>(Arrays.asList(
            String.class,
            Integer.class, int.class,
            Character.class, char.class,
            Boolean.class, boolean.class,
            Double.class, double.class,
            Float.class, float.class));

    public <T> List<T> parseList(InputStream in, Class<T> clazz) throws IOException, SAXException, ParserConfigurationException {
        listObject = new ArrayList<>();
        tClass = clazz;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(in, new MySAXHandler());

        return (List<T>) listObject;
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
        Object o;
        Deque<Object> objectsStack;
        Object currentObject;
        String className;
        private StringBuilder characters;

        @Override
        public void startDocument() throws SAXException {
            objectsStack = new ArrayDeque<>();
            className = tClass.getSimpleName();  //имя тега(тип объекта)
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (className.equals(localName)) {
                try {
                    o = tClass.newInstance();
                    objectsStack.addLast(o);
                } catch (InstantiationException | IllegalAccessException ignored) {
                    //never happened
                }
            } else {
                try {
                    setObjectFields(localName);// String localname
                } catch (IllegalAccessException | InstantiationException |
                        NoSuchMethodException | SetterException ignored) {
                    //never happened
                }
            }
        }

        private void setObjectFields(String localName) throws IllegalAccessException, InstantiationException,
                                                                 NoSuchMethodException, SetterException {
            currentObject = objectsStack.getLast(); //Берем последний элемент в очереди
            Class<?> anyClass = currentObject.getClass(); //достаем его объект класс
            Field[] clazzFields = anyClass.getDeclaredFields(); // достаем поля этого объекта
            for (Field clazzField : clazzFields) {
                if (sympleTypesSet.contains(clazzField.getType())) { //является ли поле простым? если да

                    String fieldName = clazzField.getName(); // берем имя поля. Должен быть какой то конвеншн
                    if (fieldName.equals(localName)) {
                        Method setMethod = anyClass.getMethod("set" + getCapitalizedFieldName(fieldName));
                        try {
                            setMethod.invoke(currentObject, Converter.convert(
                                                                                clazzField.getType(),
                                                                                String.valueOf(characters)));
                        } catch (InvocationTargetException e) {
                            throw new SetterException();
                        }
                    }
                } else { // Иначе если элемент сложный то заносим его в деку и вызываем setObjectFields()
                    Object complexType = clazzField.getType().newInstance(); //Создаем объект из филда
                    objectsStack.addLast(complexType); // кидаем его в деку
                    setObjectFields(localName); // и вызываем setObjectFields(localName)
                }
            }
            listObject.add(currentObject); //TODO: Проверить здесь ли нужно отправлять объект в лист
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            if (className.equals(localName)) {
                objectsStack.removeLast(); //удаляем заполненный элемент
            }
            characters.setLength(0);     // Чистим characters()
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            characters = new StringBuilder(String.valueOf(ch));
        }

        @Override
        public void endDocument() throws SAXException {
        }

    }
}

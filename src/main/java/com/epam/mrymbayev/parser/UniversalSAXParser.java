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
        rootClass = clazz;

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
            //System.out.println("Element " + qName + " started.");
            if (className.equals(qName)) {
                try {
                    rootObject = rootClass.newInstance();
                    objectsStack.add(rootObject);
                    try {
                        ParameterizedType genericType = (ParameterizedType) rootObject.getClass().getDeclaredField("list").getGenericType();
                        Type rawType = genericType.getRawType(); //interface java.util.List
                        System.out.println("rawType.toString() = " + rawType.toString());
                        Type[] actualTypeArguments = genericType.getActualTypeArguments();

                        Type typeIL = actualTypeArguments[0]; // Type of object into list
                            System.out.println("typeIL = " + typeIL); // Достал тип вложенный в лист!!!!!!!!
                            Class<? extends Type> classOfTypeIL = typeIL.getClass(); // Class object of typeIL
                            System.out.println("classOfTypeIL = " + classOfTypeIL);
                        Object objectIL = classOfTypeIL.newInstance();
                        objectsStack.add(objectIL);
                    } catch (NoSuchFieldException e) {
                        //no op
                    }
                    //TODO:хотя для корневого элемента TouristVoucher надо чтобы его лист заполнялся Voucher элементами

                    setObjectFields(qName); //добавили в очередь и вызвали сетОбжектПоля
                } catch (IllegalAccessException | InstantiationException |
                        NoSuchMethodException | SetterException ignored) {
                    //never happened
                }
            } else {
                try {
                    setObjectFields(qName);// String localname
                } catch (IllegalAccessException | InstantiationException |
                        NoSuchMethodException | SetterException ignored) {
                    //never happened
                }
            }
        }

        private void setObjectFields(String qName) throws IllegalAccessException, InstantiationException,
                                                                 NoSuchMethodException, SetterException {
            currentObject = objectsStack.getLast(); //Берем последний элемент в очереди
            Class<?> anyClass = currentObject.getClass(); //достаем его объект класс
            Field[] clazzFields = anyClass.getDeclaredFields(); // достаем поля этого объекта
            for (Field clazzField : clazzFields) {
                if (sympleTypesSet.contains(clazzField.getType())) { //является ли поле простым? если да

                    String fieldName = clazzField.getName(); // берем имя поля. Должен быть какой то конвеншн
                    if (fieldName.equals(qName)) {
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
                    objectsStack.addLast(complexType); //кидаем его в деку
                    setObjectFields(qName); // и вызываем setObjectFields(localName)
                }
            }
            Object newObject = new Object();

            newObject = currentObject;
            listObject.add(currentObject); //TODO: Проверить здесь ли нужно отправлять объект в лист
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            if (className.equals(qName)) {
                objectsStack.removeLast(); //удаляем заполненный элемент
            }
            characters.setLength(0);     // Чистим characters()
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            for(int i =start; i < length; i++){
                characters = characters.append(ch[i]);
            }
        }

        @Override
        public void endDocument() throws SAXException {
        }

    }
}

package com.epam.mrymbayev.parser;

import com.epam.mrymbayev.parser.converter.Converter;
import com.epam.mrymbayev.parser.exception.DefineGenericTypeInRootElementException;
import com.epam.mrymbayev.parser.exception.SetterException;
import org.apache.log4j.Logger;
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
    private static Logger log = Logger.getLogger("xml.app");

    private Class rootClass;
    private Class currentObjClass; // Voucher example
    Object t;

    final Set<Class> sympleTypesSet = new HashSet<Class>(Arrays.asList(
            String.class,
            Integer.class, int.class,
            Character.class, char.class,
            Boolean.class, boolean.class,
            Double.class, double.class,
            Float.class, float.class));

    public <T> T parseList(InputStream in, Class<T> clazz) throws IOException, SAXException, ParserConfigurationException {
        log.trace("Parsing is being started...");
        rootClass = clazz;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        log.trace("New instance of SAXParserFactory was created");
        SAXParser parser = factory.newSAXParser();
        parser.parse(in, new MySAXHandler());

        log.info("Parsing was successfully finished.");
        return (T) t;
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
        Deque<Object> objectsDeque;
        Object currentObject;
        Class genericTypeOfListInRootElement;
        String rootElementName;
        private StringBuilder characters = new StringBuilder();

        @Override
        public void startDocument() throws SAXException {
            log.trace("startDocument() method called.");
            objectsDeque = new ArrayDeque<>();
            rootElementName = rootClass.getSimpleName();  //имя тега(тип объекта)
            log.trace("Variable was assigned to rootElementName : " + rootElementName);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            log.trace("startElement() method to element: " + qName);
            characters.setLength(0);     // Чистим characters()
            log.trace("Check: Does rootElementName equals qName? ");
            log.trace("[]rootElementName: " + rootElementName + ", []qName: " + qName);
            log.trace("objectsDeque : " + objectsDeque.toString() + ", size: " + objectsDeque.size());

            if (rootElementName.equals(qName)) {
                log.trace("TRUE, rootElementName = " + rootElementName + ", thus create root element with List field");
                try {
                    createAndAddToDequeRootElement();
                } catch (DefineGenericTypeInRootElementException e) {
                    e.printStackTrace();
                    log.trace("Can't throw further. SAXParser API don't give possibility. ");
                }
            } else {
                log.trace("No");
                // Check and add object to objectsDeque if qName is equals to generic type of list of the root element
                if (genericTypeOfListInRootElement.getSimpleName().equalsIgnoreCase(qName)) {
                    try {
                        Object newInstance = genericTypeOfListInRootElement.newInstance();
                        objectsDeque.addLast(newInstance);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    Class<?> last = objectsDeque.getLast().getClass();
                    Field[] fields = last.getDeclaredFields();
                    log.trace("Searching corresponding field or new object will be added to objectsDeque");
                    for (Field field : fields) {
                        log.trace("Checking1... Is there field in object? ");
                        if (sympleTypesSet.contains(field.getType()) && field.getName().equalsIgnoreCase(qName))
                            return; // To continue setting data in endElement() method
                        if (!sympleTypesSet.contains(field.getType()) && (field.getName().equalsIgnoreCase(qName))) {
                            log.trace("Checking1 result: [true] ");
                            log.trace("Checking2... Does field complex? ");
                            log.trace("Checking2 result: " + field.getName() + " is complex. We will add it to objectsDeque");
                            try {
                                Object newInstance = field.getType().newInstance();
                                log.trace("New object: " + field.getName() + " form complex type was created.");
                                objectsDeque.addLast(newInstance);
                                log.trace(field.getName() + " object was added to objectsDeque.");
                                return; // Return to proceed fulfilling parsing and will not get stack
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        private void createAndAddToDequeRootElement() throws DefineGenericTypeInRootElementException {
            try {
                rootObject = rootClass.newInstance();
                t = rootObject;
                objectsDeque.add(rootObject);
                log.trace("Root element " + rootObject.getClass().getSimpleName() + " object was added to objectsDeque");


                genericTypeOfListInRootElement = defineGenericTypeInRootElementList(rootObject.getClass());
                log.trace("Generic type into root element was defined. Type: " + genericTypeOfListInRootElement.getSimpleName());


            } catch (IllegalAccessException | InstantiationException ignored) {
                ignored.printStackTrace();
            }
        }

        /**
         * Method defines generic type of root element's list. E.g. List<Generic Type T> list
         *
         * @return generic type of list which placed into root element object.
         **/
        private Class defineGenericTypeInRootElementList(Class<?> rootClass) throws DefineGenericTypeInRootElementException {

            Field voucherList = null;
            try {
                voucherList = rootClass.getDeclaredField("voucherList");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new DefineGenericTypeInRootElementException(e);
            }
            ParameterizedType genericType = (ParameterizedType) voucherList.getGenericType();
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            Type typeInList = actualTypeArguments[0]; // Type of object into list
            Class typeInListClass = (Class) typeInList;  // Here was most important decision for me
            return typeInListClass;
        }

        /**
         * Method sets values of object's fields, if any.
         */
        private void setFieldValue(String qName, String characters) throws IllegalAccessException, InstantiationException,
                NoSuchMethodException, SetterException {

            currentObject = objectsDeque.getLast(); //Берем последний элемент в очереди
            currentObjClass = currentObject.getClass(); //достаем его объект класс

            Field[] fields = currentObjClass.getDeclaredFields(); // достаем поля этого объекта

            for (Field field : fields) {
                String fieldName = field.getName(); // берем имя поля. Должен быть какой то конвеншн
                if (qName.equals(fieldName)) {
                    if (sympleTypesSet.contains(field.getType())) { //является ли поле простым? если да
                        String capitalizedFieldName = getCapitalizedFieldName(fieldName);
                        Method setMethod = currentObjClass.getMethod("set" + capitalizedFieldName, field.getType());

                        try {
                            setMethod.invoke(currentObject, Converter.convert(field.getType(), characters));
                            log.trace("Value: " + characters + " was assigned to '" + fieldName + "' field. ");
                            return; // Return from loop if field was assigned.
                        } catch (InvocationTargetException e) {
                            throw new SetterException();
                        }
                    } else { // Иначе если элемент сложный то заносим его в деку и вызываем setFieldValue()
                        Object complexType = field.getType().newInstance(); //Создаем объект из филда
                        objectsDeque.addLast(complexType); //кидаем его в деку
                        if (fieldName.equalsIgnoreCase("cost") || fieldName.equalsIgnoreCase("hotel")) {
                            System.out.println("Tut chto to nado predpriniat' \n\n");
                        }
                    }
                }
            }

        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            characters = characters.append(ch, start, length);
        }

        /**
         * Method controls file parsing process
         *
         * @param uri
         * @param localName
         * @param qName
         * @throws SAXException
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

            try {
                if (genericTypeOfListInRootElement.getSimpleName().equalsIgnoreCase(qName)) {
                    Object element = objectsDeque.pollLast();
                    addObjectToList(element);
                    log.trace("Element: " + element.getClass().getSimpleName() + " was added to result list.");
                } else {
                    setFieldValue(qName, characters.toString());
                }

                deleteFromDequeIfFieldWasFilled(qName);
            } catch (IllegalAccessException | InstantiationException | SetterException | NoSuchMethodException e) {
                e.printStackTrace();
            }


        }

        private void addObjectToList(Object object) {
            Object rootElem = objectsDeque.getFirst();
            Class<?> root = rootElem.getClass();
            try {
                Method addMethod = root.getMethod("add", genericTypeOfListInRootElement);
                try {
                    addMethod.invoke(rootElem, object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        private void deleteFromDequeIfFieldWasFilled(String qName) {

            String lastInDeq = objectsDeque.peekLast().getClass().getSimpleName();
            String genericType = genericTypeOfListInRootElement.getSimpleName();

            if ((lastInDeq.equalsIgnoreCase(qName)) && !(qName.equalsIgnoreCase(rootElementName) && !(qName.equalsIgnoreCase(genericType)))) {
                Object last = objectsDeque.removeLast();
                System.out.println("last = " + last);
                Object lastButOne = objectsDeque.getLast();
                System.out.println("lastButOne = " + lastButOne);


                try {
                    Method method = lastButOne.getClass().getMethod("set" + getCapitalizedFieldName(lastInDeq), last.getClass());
                    method.invoke(lastButOne, last);
                    System.out.println(method + "was called.");
                    System.out.println("For " + lastButOne.getClass().getSimpleName() + " element "
                            + method.getName() + "  method was called with " + last + " element.\n\n");

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                log.trace("Last element in objectsDeque was removed.");

            }
        }

        @Override
        public void endDocument() throws SAXException {
        }
    }
}

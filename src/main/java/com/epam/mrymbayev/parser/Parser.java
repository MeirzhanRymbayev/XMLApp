package com.epam.mrymbayev.parser;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

interface Parser {
    <T> T parseList(InputStream in, Class<T> clazz) throws IOException, SAXException, ParserConfigurationException;
}


package com.epam.mrymbayev.parser;

import java.io.InputStream;
import java.util.List;

interface Parser {
    <T> List<T> parseList(InputStream in, Class<T> clazz);
}


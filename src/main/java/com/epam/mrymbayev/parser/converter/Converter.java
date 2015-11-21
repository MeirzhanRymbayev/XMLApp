package com.epam.mrymbayev.parser.converter;



public final class Converter {
    public static <T> T convert(Class<T> clazz, String character){
        T t;
        if(clazz.equals(String.class)) {
            return (T) character;
        } else if(clazz.equals(Integer.class)||clazz.equals(int.class)){
            return (T)(Integer)Integer.parseInt(character);
        } else if(clazz.equals(Boolean.class)||clazz.equals(boolean.class)){
            return (T)(Boolean)Boolean.parseBoolean(character);
        } else if(clazz.equals(Float.class)||clazz.equals(float.class)){
            return (T)(Float)Float.parseFloat(character);
        } else if(clazz.equals(Double.class)||clazz.equals(double.class)){
            return (T)(Double)Double.parseDouble(character);
        } else if(clazz.equals(Character.class)||clazz.equals(char.class)){
            return (T)(Character) character.charAt(0);
        } else return null;

    }

}

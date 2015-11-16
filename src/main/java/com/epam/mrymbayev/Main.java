package com.epam.mrymbayev;

/*13.	Туристические путевки.
Туристические путевки, предлагаемые агентством, имеют следующие характеристики:
•	Type – тип путевки (выходного дня, экскурсионная, отдых, паломничество и т.д.).
•	Country – страна для путешествия.
•	Number days/nights – количество дней и ночей.
•	Тransport – вид перевозки туристов (авиа, ж/д, авто, лайнер).
•	Hotel characteristic (должно быть несколько) – количество звезд, включено ли питание и какое (HB, BB, Al), какой номер (1,2,3-х местные), есть ли телевизор, кондиционер и т.д..
•	Сost – стоимость путевки (сколько и что включено).
Корневой элемент назвать Тourist voucher.
*/

import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args ) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse("TouristVaucher.xml", new DefaultHandler());
    }
}

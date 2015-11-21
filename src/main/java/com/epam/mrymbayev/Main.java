package com.epam.mrymbayev;

/*13.	Туристические путевки.
Туристические путевки, предлагаемые агентством, имеют следующие характеристики:
•	Type – тип путевки (выходного дня, экскурсионная, отдых, паломничество и т.д.).
•	Country – страна для путешествия.
•	Number days/nights – количество дней и ночей.
•	Тransport – вид перевозки туристов (авиа, ж/д, авто, лайнер).
•	Hotel characteristic (должно быть несколько) – количество звезд,
 включено ли питание и какое (HB, BB, Al), какой номер (1,2,3-х местные), есть ли телевизор, кондиционер и т.д..
•	Сost – стоимость путевки (сколько и что включено).
Корневой элемент назвать Тourist voucher.
*/

import com.epam.mrymbayev.entity.*;
import com.epam.mrymbayev.parser.UniversalSAXParser;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args ) throws Exception {
        InputStream is = Main.class.getClassLoader().getResourceAsStream("TouristVoucher.xml");
        UniversalSAXParser parser = new UniversalSAXParser();
        List<TouristVoucher> touristVouchers = parser.parseList(is, TouristVoucher.class);
        System.out.println(touristVouchers.toString());


        /*char[] ch = new char[]{'d','a','s','t','a','n'};
        String sch = String.valueOf(ch);
        System.out.println(sch);*/
    }
}

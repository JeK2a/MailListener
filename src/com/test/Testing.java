package com.test;

import com.classes.*;
import com.db.DB;
import com.service.QuotedPrintable;
import com.service.SettingsMail;
import com.sun.mail.imap.IMAPFolder;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.mail.FetchProfile;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testing {

    public static void main(String[] args) {




//        String flegs_json = "{" +
//                "'Deleted':1," +
//                "'Answered':1," +
//                "'Draft':1," +
//                "'Flagged':1," +
//                "'Seen':1"     +
//                "}";
//        String domin    = "https://my.tdfort.ru/index.php";
//        String params = "page=ordersapi&action=flagstate&email=vipjonpc@mail.ru&folder=&uid=12345678&flag=" + flegs_json;



//        try {
//            String urlString = domin + "?" + params;
//
//            urlString = "https://my.tdfort.ru/launchers/orders_from_emails.php?&action=flagstate&email=zakupkiship@tdfort.ru&folder=%D0%A4%D0%BE%D1%80%D1%82|%D0%9C%D0%9E%D0%98%20%D0%97%D0%90%D0%9F%D0%A0%D0%9E%D0%A1%D0%AB&uid=5362&flag={'Deleted':0,'Answered':0,'Draft':0,'Flagged':0,'Seen':1}";
//
//            URL url = new URL(urlString);
//            URLConnection yc = url.openConnection();
//            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                System.out.println(inputLine);
//            }
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        String from = " =?UTF-8?B?0JDQu9C10LrRgdC10Lkg0JHQuNC30Y/QtdCy?= <alexey_spb1979@mail.ru>";
//        String from = "<alex_eyspb1979@mail.ru>";
//        String from = "=?utf-8?B?0JPRg9C70Y8g0JDQu9C40LXQstCw?= <mira-eva@yandex.ru>";
//        String from = "=?utf-8?b?0JLQsNGI0LAg0K/QvdC00LXQutGBLtCc0YPQt9GL0LrQsCDwn5GL?= <hello@yandex-team.ru>";
//        String from = "=?gbk?Q?=A7=A1=A7=D5=A7=DE=A7=DA=A7=DF=A7=DA=A7=E3=A7=E4=A7=E2=A7=D1?=";
//
//
//        String from_decode = Email.getDecode(from);
//
//        System.out.println(from);
//        System.out.println(from_decode);
//
//        DB db = new DB();
//        db.addEmailTest();

//            System.out.println(from_decode.toString());
//
//        String text = "la conférence, commencera à 10 heures 😅";
//        String regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
//        String result = text.replaceAll(regex, "");
//
//        String bear = "🐻";
//
//        // If the previous line doesn't show up in your editor,
//        // you can comment it out and use this declaration instead:
//        // String bear = "\ud83d\udc3b";
//
//        int bearCodepoint = bear.codePointAt(bear.offsetByCodePoints(0, 0));
//        int mysteryAnimalCodepoint = bearCodepoint + 1;
//
//        char mysteryAnimal[] = {Character.highSurrogate(mysteryAnimalCodepoint),
//                Character.lowSurrogate(mysteryAnimalCodepoint)};
//        System.out.println("The Coderland Zoo's latest attraction: "
//                + String.valueOf(mysteryAnimal));





//        Pattern p = Pattern.compile("=\\?(.*)\\?=");// скомпилировали регулярное выражение в представление
//        Pattern p = Pattern.compile("\\?");// скомпилировали регулярное выражение в представление
//        Pattern p = Pattern.compile("\\r\\n|\\r|\\n");
//        Matcher m = p.matcher(from);//создали поисковик в тексте “aaaaab” по шаблону "a*b"

//        System.out.println(m.replaceAll(""));

    }
}

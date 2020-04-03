package com;

import com.classes.*;
import com.db.DB;
import com.service.QuotedPrintable;
import com.service.SettingsMail;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.FetchProfile;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testing {

    public static void main(String[] args) {
//        String from = " =?UTF-8?B?0JDQu9C10LrRgdC10Lkg0JHQuNC30Y/QtdCy?= <alexey_spb1979@mail.ru>";
//        String from = "<alex_eyspb1979@mail.ru>";
        String from = "=?utf-8?B?0JPRg9C70Y8g0JDQu9C40LXQstCw?= <mira-eva@yandex.ru>";
//        String from = "=?utf-8?B?0JPRg9C70Y8g0JDQu9C40LXQstCw?=\\r <mira-eva@yandex.ru>";

        System.out.println(Email.getDecode("to", from));


//        Pattern p = Pattern.compile("=\\?(.*)\\?=");// скомпилировали регулярное выражение в представление
//        Pattern p = Pattern.compile("\\?");// скомпилировали регулярное выражение в представление
//        Pattern p = Pattern.compile("\\r\\n|\\r|\\n");
//        Matcher m = p.matcher(from);//создали поисковик в тексте “aaaaab” по шаблону "a*b"

//        System.out.println(m.replaceAll(""));

    }
}

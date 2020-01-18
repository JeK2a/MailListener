package com;

import com.classes.*;
import com.db.DB;
import com.service.SettingsMail;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.FetchProfile;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import java.util.ArrayList;
import java.util.Arrays;

public class Testing {

    public static void main(String[] args) {


        System.out.println(Arrays.asList(new String[]{"Отправленые", "Sent", "Черновик", "Черновики", "Draft", "Drafts"}));

//        if(Arrays.asList(new String[]{"Отправленые", "Sent"}).contains("Sent")) {
//            System.out.println("Test!!!");
//        }




//        DB db = new DB();
//
//        ArrayList<User> users = db.getUsers(); // Получение списка пользователей
//
//        for (User user : users) {
//            EmailAccount emailAccount = new EmailAccount(user);
//            MyProperties myProperties = new MyProperties(emailAccount.getUser()); // Настройка подключение текущего пользователя
//
//            Session session = Session.getDefaultInstance(myProperties, null); // Создание сессии
//            session.setDebug(SettingsMail.getSession_debug());          // Включение дебага
//
//            try {
//                Store store = session.getStore("imap");
//
//                store.connect(
//                    emailAccount.getUser().getHost(),
//                    emailAccount.getUser().getEmail(),
//                    emailAccount.getUser().getPassword()
//                );
//
//                IMAPFolder[] imap_folders = {(IMAPFolder) store.getFolder("Отправленные")}; // Получение списка папок для текушего подключения
//
//                FetchProfile fp = new FetchProfile();
//
//                fp.add(FetchProfile.Item.ENVELOPE); // From, To, Cc, Bcc, ReplyTo, Subject and Date
//                fp.add(FetchProfile.Item.CONTENT_INFO); // ContentType, ContentDisposition, ContentDescription, Size and LineCount
////                    fp.add(FetchProfile.Item.SIZE); // Ограничение по объему предварительно загруженных писем
////                    fp.add(FetchProfile.Item.FLAGS); //
//                fp.add("Message-ID");
//                fp.add("X-Tdfid");
//
//
//                for (IMAPFolder imapFolder : imap_folders) {
//                    imapFolder.open(IMAPFolder.READ_ONLY);
//
////                    Message message = imapFolder.getMessageByUID(13760);
////                    Message[] messages = imapFolder.getMessages();
//                    Message[] messages = imapFolder.getMessagesByUID(13000, 15000);
//
//                    imapFolder.fetch(messages, fp);
//
//                    for (Message message : messages) {
//
//                        Email email = new Email(user.getUser_id(), user.getEmail(), message, imapFolder.getFullName(), imapFolder);
//
//                        System.out.print(email.getUid());
//
//                        if (email.getDirection().equals("in")) {
//                            System.out.println(email);
//                        }
//                    }
//                }
//
//            } catch (Exception e) {
//                emailAccount.setException(e);
//            }



//        }
    }
}

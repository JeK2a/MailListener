package com.test;

import com.Main;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.User;
import com.db.DB;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IdleManager;

import javax.mail.*;
import javax.mail.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class Test {
    public static void main(String[] args) {
//        String string = "=?iso-8859-5?Q?=B0=DB=EC=D1=D5=E0=E2_=C3=E1=DC=D0=DD=DA=E3=DB=DE=D2?=";
//        String string = "=?koi8-r?Q?=EE=C9=CB=CF=CC=C1=CA?= <zaxitovih57@mail.ru>";
//        String string = "=?UTF8?Q?=8F_=28=D0=BD=D0=B0=D1=81=D1=82=D0=BE=D0=BB=D1=8C=D0=BD=D1=8B=D0?=";
//        String string = "=?UTF8?Q?=B9=29?=";
//        String string = "=?ISO-8859-1?Q?serdar_g=FCven?=";
//        String string = "=?utf-8?Q?=D0=A2=D1=80=D0=B0=D0=BD=D1=81=D0=93=D0=B0=D1=80=D0=B0=D0=BD=D1=82=20?=";
//        String string = "=?gbk?Q?=A7=A1=A7=D5=A7=DE=A7=DA=A7=DF=A7=DA=A7=E3=A7=E4=A7=E2=A7=D1?=";
//        String string = "=?gbk?Q?=A7=E4=A7=E0=A7=E2_=A7=B4=A7=A5_=A7=B6=A7=E0=A7=E2=A7=E4?=";
//        String string = "=?gb18030?Q?=A7=A1=A7=D5=A7=DE=A7=DA=A7=DF=A7=DA=A7=E3=A7=E4=A7=E2=A7=D1?=";
        String string = "=?windows-1251?B?wtLBIDI0ICjHwM4p====?=";

        System.out.println(Email.stringDecode(string));




//        try {
//            DB db = new DB();
//
//            ArrayList<User> users = db.getUsers(); // Получение списка пользователей
//
//            for (User user : users) {
//
//                EmailAccount emailAccount = new EmailAccount(user);
//                MyProperties myProperties = new MyProperties(emailAccount.getUser());
//
//                Session session = Session.getDefaultInstance(myProperties, null);
//
////                ExecutorService es = Executors.newCachedThreadPool();
//                ExecutorService es = myProperties.getEs();
////                final IdleManager idleManager = new IdleManager(session, es);
//                IdleManager idleManager = new IdleManager(session, es);
//
//                Store store = session.getStore("imap");
//
//                try {
//                    store.connect(
//                        emailAccount.getUser().getHost(),
//                        emailAccount.getUser().getEmail(),
//                        emailAccount.getUser().getPassword()
//                    );
//                } catch (Exception e) {
//                    continue;
//                }
//
////                Folder folder = store.getFolder("INBOX");
//
////                IMAPFolder[] folders = (IMAPFolder[]) store.getDefaultFolder().list("*");
//                IMAPFolder[] folders = {(IMAPFolder) store.getFolder("Отправленные")};
//
////                IMAPFolder folder = (IMAPFolder) store.getDefaultFolder().list("INBOX")[0];
//                for (IMAPFolder folder : folders) {
//
//                    folder.open(Folder.READ_WRITE);
//
////                    folder.addMessageCountListener(new MessageCountListener() {
////                        @Override
////                        public void messagesAdded(MessageCountEvent messageCountEvent) {
////                            try {
////                                IMAPFolder tmp_folder = (IMAPFolder) messageCountEvent.getMessages()[0].getFolder();
////
////                                System.out.println("messagesAdded");
////                                idleManager.watch(tmp_folder);
////
////                                System.out.println("(" + Thread.activeCount() + ")");
////                            } catch (MessagingException ex) {
////                                ex.printStackTrace();
////                            }
////                        }
////
////                        @Override
////                        public void messagesRemoved(MessageCountEvent messageCountEvent) {
////                            try {
////                                IMAPFolder tmp_folder = (IMAPFolder) messageCountEvent.getMessages()[0].getFolder();
////
////                                System.out.println("messagesRemoved");
////                                idleManager.watch(tmp_folder);
////
////                                System.out.println("(" + Thread.activeCount() + ")");
////                            } catch (MessagingException ex) {
////                                ex.printStackTrace();
////                            }
////                        }
////                    });
//
////                idleManager.watch(folder); // keep watching for new messages // TODO ??????????????
//
////                    folder.addMessageChangedListener((MessageChangedEvent messageChangedEvent) -> {
//////                        IMAPFolder tmp_folder = (IMAPFolder) messageChangedEvent.getMessage().getFolder();
////
////
////                        IMAPMessage imapMessage = (IMAPMessage) messageChangedEvent.getMessage();
////
//////                        System.out.println("messageChanged");
//////                            idleManager.watch(tmp_folder);
////
////                        System.out.println("(" + Thread.activeCount() + ")");
////                    });
//
////                    System.out.println(emailAccount.getEmailAddress() + " - " + folder.getFullName());
//
//                    String out = (String) folder.doCommand(imapProtocol -> {
////                    Response[] responses = imapProtocol.command("FETCH " + imap_message.getMessageNumber() + " (FLAGS UID)", null); // TODO
//                        Response[] responses = imapProtocol.command("UID SEARCH UID 9267", null);
//
//                        return responses[0].toString();
//                    });
//
//                    System.out.println(out);
//
////                    idleManager.watch(folder);
//
////                    while (true) {
////                        Thread.sleep(290000);
////                        idleManager.watch(folder);
////                    }
//                }
//            }
//
////            showThreads();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private static void showThreads() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootGroup.getParent()) != null) {
            rootGroup = parent;
        }

//                listThreads(rootGroup, "");
        System.out.println("=================================================================================");
        Main.countThreads(rootGroup, "");
    }

}
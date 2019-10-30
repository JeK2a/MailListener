package com;

import com.classes.EmailAccount;
import com.classes.User;
import com.db.DB;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IdleManager;

import javax.mail.*;
import javax.mail.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class Test {
    public static void main(String[] args) {
        try {
            DB db = new DB();

            ArrayList<User> users = db.getUsers(); // Получение списка пользователей

            for (User user : users) {

                EmailAccount emailAccount = new EmailAccount(user);
                MyProperties myProperties = new MyProperties(emailAccount.getUser());

                Session session = Session.getDefaultInstance(myProperties, null);

//                ExecutorService es = Executors.newCachedThreadPool();
                ExecutorService es = myProperties.getEs();
//                final IdleManager idleManager = new IdleManager(session, es);
                IdleManager idleManager = new IdleManager(session, es);

                Store store = session.getStore("imap");

                try {
                    store.connect(
                        emailAccount.getUser().getHost(),
                        emailAccount.getUser().getEmail(),
                        emailAccount.getUser().getPassword()
                    );
                } catch (Exception e) {
                    continue;
                }

//                Folder folder = store.getFolder("INBOX");

                IMAPFolder[] folders = (IMAPFolder[]) store.getDefaultFolder().list("*");

//                IMAPFolder folder = (IMAPFolder) store.getDefaultFolder().list("INBOX")[0];
                for (IMAPFolder folder : folders) {

                    folder.open(Folder.READ_WRITE);

                    folder.addMessageCountListener(new MessageCountListener() {
                        @Override
                        public void messagesAdded(MessageCountEvent messageCountEvent) {
                            try {
                                IMAPFolder tmp_folder = (IMAPFolder) messageCountEvent.getMessages()[0].getFolder();

                                System.out.println("messagesAdded");
                                idleManager.watch(tmp_folder);

                                System.out.println("(" + Thread.activeCount() + ")");
                            } catch (MessagingException ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void messagesRemoved(MessageCountEvent messageCountEvent) {
                            try {
                                IMAPFolder tmp_folder = (IMAPFolder) messageCountEvent.getMessages()[0].getFolder();

                                System.out.println("messagesRemoved");
                                idleManager.watch(tmp_folder);

                                System.out.println("(" + Thread.activeCount() + ")");
                            } catch (MessagingException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

//                idleManager.watch(folder); // keep watching for new messages // TODO ??????????????

                    folder.addMessageChangedListener((MessageChangedEvent messageChangedEvent) -> {
//                        IMAPFolder tmp_folder = (IMAPFolder) messageChangedEvent.getMessage().getFolder();


                        IMAPMessage imapMessage = (IMAPMessage) messageChangedEvent.getMessage();

//                        System.out.println("messageChanged");
//                            idleManager.watch(tmp_folder);

                        System.out.println("(" + Thread.activeCount() + ")");
                    });

                    System.out.println(emailAccount.getEmailAddress() + " - " + folder.getFullName());

                    idleManager.watch(folder);

//                    while (true) {
//                        Thread.sleep(290000);
                        idleManager.watch(folder);
//                    }
                }
            }

            showThreads();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
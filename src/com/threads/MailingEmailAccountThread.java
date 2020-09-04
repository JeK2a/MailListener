package com.threads;

import com.Main;
import com.MyProperties;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.db.DB;
import com.service.SettingsMail;
import com.sun.mail.iap.BadCommandException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IdleManager;

import javax.mail.*;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class MailingEmailAccountThread implements Runnable {

    private static DB db = Main.db;
    private EmailAccount emailAccount;
    private static int index = 0;

    public MailingEmailAccountThread(EmailAccount emailAccount) {
        this.emailAccount = emailAccount;
    }

    public static int getIndex() {
        return ++index;
    }

    @Override
    public void run() {
        emailAccount.setStatus("start");

        MyProperties myProperties = new MyProperties(emailAccount.getUser()); // Настройка подключение текущего пользователя

        Session session = Session.getDefaultInstance(myProperties, null); // Создание сессии
        session.setDebug(SettingsMail.getSession_debug());          // Включение дебага

        try {
            ExecutorService es = myProperties.getEs();
            Store store        = session.getStore("imap");
            String status_tmp  = connectToStore(store);

            emailAccount.setStatus(status_tmp);

            if (!status_tmp.equals("connect")) { return; }

            addStoreListeners(store);

//            IMAPFolder[] imap_folders = {
//                    (IMAPFolder) store.getFolder("INBOX")
//                    ,
//                    (IMAPFolder) store.getFolder("Отправленные")
//            };
//            IMAPFolder[] imap_folders = {(IMAPFolder) store.getFolder("INBOX")}; // Получение списка папок для текушего подключения
            IMAPFolder[] imap_folders = (IMAPFolder[]) store.getDefaultFolder().list("*"); // Получение списка папок для текушего подключения

            for (IMAPFolder imap_folder: imap_folders) {
                addFolder(imap_folder, session, es);
            }

            ConcurrentHashMap<String, MyFolder> myFoldersMap_tmp = emailAccount.getFoldersMap();

            emailAccount.setStatus("wait");

            if (SettingsMail.getWaitUser()) {
                while (!Thread.interrupted()) {
                    int n = 0;

                    for (Map.Entry<String, MyFolder> entry : myFoldersMap_tmp.entrySet()) {
                        status_tmp = entry.getValue().getStatus();

                        if (
                            !(
                                status_tmp.equals("error")                  ||
                                status_tmp.equals("listening")              ||
                                status_tmp.equals("end_add_message_folder") ||
                                status_tmp.equals("closed")                 ||
                                status_tmp.equals("close")
                            )
                        ) {
                            n++;
                        }
                    }

                    if (n == 0) { break; }

                    Thread.sleep(1000);
                }
            }

            emailAccount.setStatus("end");

            while (!Thread.interrupted()) {
//                connectToStore(store);
                Thread.sleep(30000); // TODO java.lang.InterruptedException: sleep interrupted
            }

        } catch (Exception e) {
            emailAccount.setException(e);
        }

        emailAccount.setStatus("stop");
    }

    private void addStoreListeners(Store store) {
        store.addConnectionListener(new ConnectionListener() {
            @Override
            public void opened(ConnectionEvent connectionEvent) {
                emailAccount.setStatus("restart");
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                emailAccount.setStatus("disconnected");
                connectToStore(store);
            } // TODO нужно ли реконектится

            @Override
            public void closed(ConnectionEvent connectionEvent) {
//                emailAccount.setStatus("closed");
                connectToStore(store);
            }
        });

        store.addFolderListener(new FolderListener() { // Подключение отслеживания действий с папками в текущем подключении пользователя
            @Override
            public void folderCreated(FolderEvent folderEvent) { // Действие при создании папки
                try {
                    addFolder(folderEvent.getFolder());
                } catch (Exception e) {
                    emailAccount.setException(e);
                }
            }

            @Override
            public void folderDeleted(FolderEvent folderEvent) { // Действие при удалении папки
                try {
                    emailAccount.setException(folderEvent.getFolder().getFullName() + " folder deleted"); // TODO тзменять статус папки

                    IMAPFolder folder_tmp  = (IMAPFolder) folderEvent.getFolder();
                    String folder_name     = folder_tmp.getFullName();
                    IMAPMessage[] messages = (IMAPMessage[]) folder_tmp.getMessages();

                    for (IMAPMessage imap_message : messages) {
                        db.setDeleteFlag(emailAccount.getEmailAddress(), folder_name, folder_tmp.getUID(imap_message)); // TODO изменение флага сообщенией на удаленное (проверить)
                    }

                    // TODO изменить статус у папки и остановить прослушивание данной папки
                } catch (Exception e) {
                    emailAccount.setException(e);
                }
            }

            @Override
            public void folderRenamed(FolderEvent folderEvent) { // Действие при переименовании папки
                try {
                    IMAPFolder imap_folder = (IMAPFolder) folderEvent.getFolder();

                    String old_folder_name = folderEvent.getFolder().getFullName();
                    String new_folder_name = folderEvent.getNewFolder().getFullName();
                    int user_id = emailAccount.getUser().getUser_id();

                    String email_address = emailAccount.getEmailAddress();

                    IMAPMessage[] messages = (IMAPMessage[]) folderEvent.getFolder().getMessages();

                    for (IMAPMessage imap_message : messages) {
                        db.changeFolderName(new Email(user_id, email_address, imap_message, old_folder_name, imap_folder), new_folder_name); // TODO проверить, добавить проверку результата
                    }

                    emailAccount.setException("folder renamed, new folder name " + new_folder_name);
                } catch (Exception e) {
                    emailAccount.setException(e);
                }
            }
        });

        store.addStoreListener(storeEvent -> {
            emailAccount.setException("store notification message - " + storeEvent.getMessage());

//            connectToStore(store); // не особо нужно
        });

    } // Добавление слушалки на аккаунт

    // Подключение к аккаунту
    private String connectToStore(Store store) {
        String status = "error";

        try {
            emailAccount.setThread_problem(1);
            long start = System.currentTimeMillis();

            if (store.isConnected()) {
                emailAccount.incrementCount_restart_noop();
            } else {
                store.connect(
                    emailAccount.getUser().getHost(),
                    emailAccount.getUser().getEmail(),
                    emailAccount.getUser().getPassword()
                );
                emailAccount.incrementCount_restart_success();
            }

            long stop = System.currentTimeMillis();

            emailAccount.setThread_problem(0);
            emailAccount.setTime_reconnect(stop - start);

            db.updateSuccess(emailAccount.getUser().getEmail(), 1);
            status = "connect";
//        } catch (AuthenticationFailedException | MessagingException  e ) {
//        } catch (AuthenticationFailedException e) {
//            status = "error"; // TODO не попадает сюда
        } catch (MessagingException e) {
            emailAccount.setException(e);
            emailAccount.incrementCount_restart_fail();
        } catch (Exception e) {
            emailAccount.setException(e);
            emailAccount.incrementCount_restart_fail();
            Thread.sleep(5000);
//            status = connectToStore(store);
            // TODO добавить ограничение на количество попыток
            // TODO разобраться из-за чего может многократно не подключаться к аккаунту
        } finally {
            return status;
        }
    }

    public void addFolder(IMAPFolder imap_folder, Session session, ExecutorService es) {
        MyFolder myFolder = new MyFolder(imap_folder, session, es);
        emailAccount.addMyFolder(myFolder);
        Thread myTreadAllMails = new Thread(
                new AddNewMessageThread(
                        emailAccount,
                        myFolder,
                        imap_folder,
                        session,
                        es
                )
        ); // Создание потока для синхронизации всего почтового ящика
        myFolder.setThread(myTreadAllMails);
        myTreadAllMails.setName("AddNewMessageThread " + AddNewMessageThread.getIndex());
        myTreadAllMails.setDaemon(true);
        myTreadAllMails.start();
    }

    private void addFolder(Folder folder) {
        addFolder((IMAPFolder) folder);
    }

    private void removeFolder(String folder_name) {
        emailAccount.getFoldersMap().remove(folder_name);
    }

}

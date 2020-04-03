package com.threads;

import com.Main;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.User;
import com.db.DB;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Message;
import javax.mail.Store;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Mailing implements Runnable {

    private DB db = Main.db;
    public volatile static ConcurrentHashMap<String, EmailAccount> emailAccounts = new ConcurrentHashMap<>();

    private static int index = 0;

    public static int getIndex() {
        return ++index;
    }

    @Override
    public void run() {
        try {
            ArrayList<User> users = db.getUsers(); // Получение списка пользователей

            for (User user : users) {

                EmailAccount emailAccount = new EmailAccount(user);

                addEmailAccount(emailAccount);

//                while (true) {
//                    // разделить условия в println выводить revive try / close on error / wait
//                    if (
//                        emailAccount.getStatus().equals("end")                  ||
//                        emailAccount.getStatus().equals("stop")                 ||
//                        emailAccount.getStatus().equals("error")                ||
//                        emailAccount.getStatus().equals("AuthenticationFailed") ||
//                        emailAccount.getStatus().equals("closed")               ||
//                        emailAccount.getStatus().equals("close")
//                    ) {
//                        break;
//                    }

                Thread.sleep(5000); // TODO 15000
//                checkAccounts();
//                }
            }

            while (true) {
                checkAccounts();
                Thread.sleep(120000);
//                Thread.sleep(20000);
            }

        } catch (Exception e) {
            System.err.println("ERROR");
            e.printStackTrace();
        }
    }

    private void checkAccounts() {
        for (Map.Entry<String, EmailAccount> accountEntry : emailAccounts.entrySet()) {

            EmailAccount emailAccount = accountEntry.getValue();
            MailingEmailAccountThread mailingEmailAccount_tmp = new MailingEmailAccountThread(emailAccount);

//            if (
//                    !(
//                        emailAccount.getStatus().equals("AuthenticationFailed") ||
//                        emailAccount.getStatus().equals("error")
//                    ) &&
//                    emailAccount.getThread_problem() > 0 &&
//                    emailAccount.getTime_reconnect() < (new Date().getTime() / 1000 - 360)
//            ) {
//                for (Map.Entry<String, MyFolder> folderEntry : emailAccount.getMyFoldersMap().entrySet()) {
//                    rebootFolder(folderEntry.getValue());
//                    emailAccount.getMyFoldersMap().remove(folderEntry.getKey());
//                }
//
//                accountEntry.getValue().getThread().stop();
//                emailAccounts.remove(accountEntry.getKey());
//                addEmailAccount(emailAccount);
//
//                continue;
//            }

            for (Map.Entry<String, MyFolder> folderEntry : emailAccount.getMyFoldersMap().entrySet()) {
//                MyFolder myFolder = folderEntry.getValue();

                MyFolder myFolder_tmp = folderEntry.getValue();

                String status = myFolder_tmp.getStatus();

                if (status.equals("sleep")) {
                    try {
                        IMAPFolder imap_folder_tmp = reopenFolder(myFolder_tmp);
                        int messages_count = imap_folder_tmp.getMessageCount();

                        if (messages_count > 0) {
                            Message message_tmp = imap_folder_tmp.getMessage(messages_count);

                            LocalDate now = LocalDate.now();
                            LocalDate message_date = message_tmp.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                            Period period = Period.between(message_date, now);

                            System.err.println("== " + period.getDays() + " ==");

                            if (period.getDays() <= 7) {
                                restartFolder(emailAccount, myFolder_tmp);
//                                stopFolder(folderEntry.getValue());
//                                emailAccount.getMyFoldersMap().remove(folderEntry.getKey());
//                                mailingEmailAccount_tmp.addFolder(folderEntry.getValue().getImap_folder(), folderEntry.getValue().getSession(), folderEntry.getValue().getEs());
                                continue;
                            }
                        }

                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (
//                        true
//                        myFolder_tmp.getThread_problem() > 0 &&
                    status.equals("error")    ||
                    status.equals("close")    ||
                    status.equals("closed")   ||
                    myFolder_tmp.getCount_restart_success() > 1 ||
                    (
                        myFolder_tmp.getThread_problem() > 0 &&
                        myFolder_tmp.getTime_last_noop() < (new Date().getTime() / 1000 - 360)
                    )
                ) {
                    stopFolder(folderEntry.getValue());
                    emailAccount.getMyFoldersMap().remove(folderEntry.getKey());
                    mailingEmailAccount_tmp.addFolder(
                        folderEntry.getValue().getImap_folder(),
                        folderEntry.getValue().getSession(),
                        folderEntry.getValue().getEs()
                    );
                }
            }
        }
    }

    public static void stopFolder(MyFolder folder_tmp) {
        try {
            IMAPFolder imapFolder_tmp = folder_tmp.getImap_folder();

            if (imapFolder_tmp.isOpen()) {
                imapFolder_tmp.close();
            }

            Thread.sleep(1000);

            if (imapFolder_tmp.isOpen()) {
                imapFolder_tmp.forceClose();
            }

            folder_tmp.getThread().stop();
            folder_tmp.getIdleManager().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addEmailAccount(EmailAccount emailAccount) {
        Thread startMailThread = new Thread(new MailingEmailAccountThread(emailAccount)); // Создание потока для синхронизации всего почтового ящика // TODO old_messages
        emailAccount.setThread(startMailThread);
        startMailThread.setName("MailingEmailAccountThread " + MailingEmailAccountThread.getIndex());
        emailAccounts.put(emailAccount.getEmailAddress(), emailAccount);
        startMailThread.setDaemon(true);
        startMailThread.start(); // Запус потока
    }

    public static void restartAccount(String account_name) {
        EmailAccount emailAccount_tmp = emailAccounts.get(account_name);

        ConcurrentHashMap<String, MyFolder> myFolders_tmp = emailAccount_tmp.getMyFoldersMap();

        if (myFolders_tmp.size() > 0) {
            for (Map.Entry<String, MyFolder> folderEntry : myFolders_tmp.entrySet()) {
                MyFolder myFolder_tmp = folderEntry.getValue();
                stopFolder(myFolder_tmp);
            }
        }

        emailAccount_tmp.getThread().stop();
        emailAccounts.remove(account_name);
        addEmailAccount(emailAccount_tmp);
    }

    public IMAPFolder reopenFolder(MyFolder myFolder) {
        IMAPFolder imap_folder;

        try {
            myFolder.setThread_problem(1);
            long start = System.currentTimeMillis();
            imap_folder = myFolder.getImap_folder();

            if (imap_folder.isOpen()) {
                myFolder.incrementCount_restart_noop();
            } else {
                Store tmp_store = imap_folder.getStore();
                if (!tmp_store.isConnected()) {
                    Thread.sleep(1000);
                    if (!tmp_store.isConnected()) {
                        tmp_store.connect();
                    }
                }

                if (!imap_folder.isOpen()) {
                    Thread.sleep(1000);
                    if (!imap_folder.isOpen()) {
                        imap_folder.open(IMAPFolder.READ_ONLY); // TODO javax.mail.MessagingException: * BYE JavaMail Exception: java.net.SocketException: Connection or outbound has closed; || This operation is not allowed on an open folder
                    }
                }
//                fetchMessages(imap_folder.getMessages(), true); // TODO доработать (при перепоплючении проверять сообщения)
//                FetchProfile fp = new FetchProfile();
//                Message[] messages = new Message[0];
//                imap_folder.fetch(messages, fp);
                myFolder.incrementCount_restart_success();
            }

            long stop = System.currentTimeMillis();

            myFolder.setTime_reconnect(stop - start);
        } catch (Exception e) {
            myFolder.setException(e);
            myFolder.incrementCount_restart_fail();
            return null;
        } finally {
            myFolder.setThread_problem(0);
        }

        return imap_folder;
    }

    public static void restartFolder(String account_name, String folder_name) {
        EmailAccount emailAccount = emailAccounts.get(account_name);
        MyFolder myFolder = emailAccount.getFoldersMap().get(folder_name);

        restartFolder(emailAccount, myFolder);
    }

    public static void restartFolder(EmailAccount emailAccount, MyFolder myFolder) {
        stopFolder(myFolder);
        emailAccount.getMyFoldersMap().remove(myFolder.getFolder_name());

        emailAccount.addMyFolder(myFolder);
        Thread myTreadAllMails = new Thread(
                new AddNewMessageThread(
                        emailAccount,
                        myFolder,
                        myFolder.getImap_folder(),
                        myFolder.getSession(),
                        myFolder.getEs()
                )
        ); // Создание потока для синхронизации всего почтового ящика
        myFolder.setThread(myTreadAllMails);
        myTreadAllMails.setName("AddNewMessageThread " + AddNewMessageThread.getIndex());
        myTreadAllMails.setDaemon(true);
        myTreadAllMails.start();
    }

}
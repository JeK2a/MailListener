package com.threads;

import com.Main;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.MyMessage;
import com.db.DB;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IdleManager;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import javax.mail.*;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

public class AddNewMessageThread implements Runnable {

    private static DB db = Main.db;

    private MyFolder     myFolder;
    private IMAPFolder   imap_folder;
    private EmailAccount emailAccount;

    private int user_id = 0;
    private String email_address;
    private String folder_name;

    private static int index = 0;

    private Session session;
    private ExecutorService es;


//    public AddNewMessageThread(EmailAccount emailAccount, MyFolder myFolder, IMAPFolder imap_folder) {
//        if (db == null) {
//            db = new DB();
//        }
//        this.myFolder      = myFolder;
//        this.emailAccount  = emailAccount;
//        this.imap_folder   = imap_folder;
//
//        this.folder_name   = imap_folder.getFullName();
//        this.email_address = emailAccount.getEmailAddress();
//    }

    public AddNewMessageThread(EmailAccount emailAccount, MyFolder myFolder, IMAPFolder imap_folder, Session session, ExecutorService es) {
        if (db == null) {
            db = new DB();
        }
        this.myFolder      = myFolder;
        this.emailAccount  = emailAccount;
        this.imap_folder   = imap_folder;

        this.folder_name   = imap_folder.getFullName();
        this.email_address = emailAccount.getEmailAddress();

        this.session = session;
        this.es = es;
    }

    public static int getIndex() {
        return ++index;
    }

    @Override
    public void run() {
        long messages_count_mail;
        long messages_count_db;

        try {
            myFolder.setIdleManager(new IdleManager(session, es));

            if (!reopenFolder("start")) {
                myFolder.setStatus("closed");
                return;
            }  // Открыть папку на чтение если она не открыта

            myFolder.setStatus("start");

//            addFolderListenersConnection(imap_folder);

            user_id       = emailAccount.getUser().getUser_id();
            email_address = emailAccount.getEmailAddress();
            folder_name   = imap_folder.getFullName();

            myFolder.setStatus("for start");

            checkOldMails(email_address, folder_name);

            myFolder.setStatus("for end");

            messages_count_mail = imap_folder.getMessageCount();
            messages_count_db   = db.getCountMessages(email_address, folder_name);

            if (messages_count_db > 0) {
                myFolder.setStatus("checkFlags start");
                checkFlags(email_address, folder_name);
                myFolder.setStatus("checkFlags end");

//                checkRemoved(user_id, folder_name, messages_count_db); // Пометить удаленные сообщения в базе
            }

            myFolder.setStatus("end_add_message_folder");

            addFolderListenersMessages(imap_folder);

            this.myFolder.setMessages_count(messages_count_mail);
            this.myFolder.setMessages_db_count(db.getCountMessages(email_address, folder_name));

            addFolderListenersConnection(imap_folder);

            myFolder.setStatus("listening");

            int noop_sleep = 30000;

            switch (folder_name) {
                case "INBOX": noop_sleep = 60000;  break;
                default:      noop_sleep = 290000; break;
            }

            while (!Thread.interrupted()) {
//                if (reopenFolder("noop")) {
//                    myFolder.updateTime_last_noop();
//                }

                if (!imap_folder.isOpen()) {
                    Thread.sleep(1000);
                    if (!imap_folder.isOpen()) {
                        imap_folder.open(Folder.READ_ONLY);
                    }
                }

                myFolder.getIdleManager().watch(imap_folder); // TODO

//                idleManager.stop();

                Thread.sleep(noop_sleep);
            }

        } catch (Exception e) {
            myFolder.setException(e);
        } finally {
            myFolder.setStatus("closed");
        }
    }


    private boolean checkOldMails(String email_address, String folder_name) throws MessagingException {

        myFolder.setStatus("checkOldMails start");

        Message[] messages;

        try {
            if (!imap_folder.isOpen()) {
                Thread.sleep(1000);
                if (!imap_folder.isOpen()) {
                    imap_folder.open(IMAPFolder.READ_ONLY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long messages_count_db   = db.getCountMessages(email_address, folder_name);
        long messages_count_mail = this.imap_folder.getMessageCount();

        this.myFolder.setMessages_count(messages_count_mail);
        this.myFolder.setMessages_db_count(messages_count_db);

        if (messages_count_db == 0) { // Если нет сообщений, то просто выйти
            if (messages_count_mail == 0) {
                return true;
            } else {
                messages = imap_folder.getMessages();
                fetchMessages(messages);
            }
        } else {
            if (messages_count_mail == 0) {
                db.deleteMessages(email_address, folder_name);
            } else {
                Message[] messages_tmp = imap_folder.getMessages();

                checkRemoved(email_address, folder_name);

                messages_count_db   = db.getCountMessages(email_address, folder_name);
                messages_count_mail = imap_folder.getMessageCount();

//                System.out.println(messages_count_db + " / " + messages_count_mail);

                long last_uid_db   = db.getLastAddUID(emailAccount.getEmailAddress(), folder_name);
                long last_uid_mail = messages_tmp.length > 0 ? imap_folder.getUID(messages_tmp[messages_tmp.length - 1]) : 0;

                if (last_uid_db > 0 && checkRandomMessages(last_uid_db)) {
                    if (
                            last_uid_db         == last_uid_mail     &&
                            messages_count_mail == messages_count_db
                    ) {

                        return true;
                    } else if (last_uid_db < last_uid_mail) {
                        messages = imap_folder.getMessagesByUID(last_uid_db + 1, last_uid_mail);
                        fetchMessages(messages);
                        return false;
                    } else {
                        System.out.println(last_uid_db + "/" + last_uid_mail + "   " + messages_count_db + "/" + messages_count_mail + "   " + folder_name);
                        checkRemoved(email_address, folder_name);
                        return false;
                    }
                } else {
//                    db.deleteMessages(email_address, folder_name);
                    messages = imap_folder.getMessages();
                    fetchMessages(messages);
                    return false;
                }
            }
        }

        myFolder.setStatus("checkOldMails end ");

        return false;
    }

    private boolean fetchMessages(Message[] messages) {
        return fetchMessages(messages, false);
    }

    private boolean fetchMessages(Message[] messages, boolean only_uid) {

        try {
            if (messages.length == 0) {
                return true;
            }

//            reopenFolder("fetch");

            FetchProfile fp = new FetchProfile();

            if (!only_uid) {
                fp.add(FetchProfile.Item.ENVELOPE); // From, To, Cc, Bcc, ReplyTo, Subject and Date
                fp.add(FetchProfile.Item.CONTENT_INFO); // ContentType, ContentDisposition, ContentDescription, Size and LineCount
//                    fp.add(FetchProfile.Item.SIZE); // Ограничение по объему предварительно загруженных писем
//                    fp.add(FetchProfile.Item.FLAGS); //
                fp.add("Message-ID");
                fp.add("X-Tdfid");
            }

            fp.add(UIDFolder.FetchProfileItem.UID);

            final int count_all      = messages.length;
            final int count_messages = 100;
            final int count_step     = count_all / count_messages + 1;

            int start;
            int end;

            for (int i = 0; i < count_step; i++) {

                start = count_messages * i;
                end   = start + count_messages;

                if (end > count_all - 1) {
                    end = count_all;
                }

                System.out.println(count_all + " - " + start + " / " + end);

                Message[] messages_tmp = Arrays.copyOfRange(messages, start, end);

                myFolder.setStatus("Fetch start " + messages_tmp.length + " / " + count_all + " / " + imap_folder.getMessageCount() + " (" + start + " - " + end + ")");

                System.out.println(messages_tmp.length);

                imap_folder.fetch(messages_tmp, fp);

                myFolder.setStatus("load in DB start " + messages_tmp.length + " / " + count_all + " / " + imap_folder.getMessageCount() + " (" + start + " - " + end + ")");

                if (!only_uid) {
                    for (Message message : messages_tmp) {
                        try {

                            db.addEmail(new Email(user_id, email_address, message, folder_name, imap_folder));
                        } catch (Exception e) {
                            myFolder.setException(e);
                        }
                    }
                }

                myFolder.setStatus("load in DB end");
            }

        } catch (Exception e) {
            myFolder.setException(e);
        }

        return true;
    }

    private boolean reopenFolder(String reson) {

//        System.err.println(reson);

        try {
            myFolder.setThread_problem(1);
            long start = System.currentTimeMillis();

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
        } finally {
            myFolder.setThread_problem(0);
            return true;
        }
    }

    private void checkRemoved(String email, String folder_name) {
        try {
            checkFolder(imap_folder);
            
            // Если нет UID, то пометить как deleted
            int query_buffer = 5000;

            reopenFolder("checkRemoved");

            int messagesCount = imap_folder.getMessageCount();

            long query_count = (messagesCount / query_buffer) + 1;

            for (int j = 0; j < query_count; j++) {

                long start = (j * query_buffer + 1);
                long end   = ((j + 1) * query_buffer + 1);

                if (end > messagesCount) {
                    end = messagesCount;
                }

                if (start > end) {
                    start = end;
                }

                long finalStart = start;
                long finalEnd   = end;

                if (
                        finalStart == 0 ||
                        finalEnd   == 0
                ) {
                    return;
                }

//                String out = (String) imap_folder.doCommand(imapProtocol -> {
////                    Response[] responses = imapProtocol.command("FETCH " + imap_message.getMessageNumber() + " (FLAGS UID)", null); // TODO
//                    Response[] responses = imapProtocol.command("UID FETCH 1775 (FLAGS)", null);
//                    return responses[0].toString();
//                });
//
//                System.out.println(out);
//
//                System.exit(0);

                ArrayList<Long> arr_uids = (ArrayList<Long>) imap_folder.doCommand(imapProtocol -> {

                    Response[] responses = imapProtocol.command("UID SEARCH " + finalStart + ":" + finalEnd, null);
                    String[]   out_str   = responses[0].toString().split(" ");

                    ArrayList<Long> arr_uids_tmp = new ArrayList<>();

                    if (out_str.length > 2) {
                        for (int n = 2; n < out_str.length; n++) {
                            try {
                                arr_uids_tmp.add(Long.parseLong(out_str[n]));
                            } catch (NumberFormatException e) {
                                System.err.println(responses[0]);
                                break;
                            }
                        }
                    }

                    return arr_uids_tmp;
                });

                if (
                        arr_uids.size() == 0 &&
                        imap_folder.getMessageCount() != 0
                ) {
                    throw new Exception("Problem with get UID"); // TODO повторный запрос UID
                }

//                if (finalStart == 1 &&  arr_uids.size() > 0 && (arr_uids.get(0) - 1) > 0) {
                if (finalStart == 1) {
                    long uid_end = arr_uids.size() > 0 ? arr_uids.get(0) - 1 : -1;

                    db.setRemoved(email, folder_name, -1, uid_end, null); // пометить до
                }

                if (arr_uids.size() > 0) {
                    long uid_start = arr_uids.get(0);
                    long uid_end   = arr_uids.get(arr_uids.size() - 1);

                    db.setRemoved(email, folder_name, uid_start, uid_end, arr_uids); //set removed
                    if (reopenFolder("fetchMessages")) { // TODO

                        long[] uids_tmp = db.getMissingUIDs(
                                email_address,
                                folder_name,
                                uid_start,
                                uid_end,
                                arr_uids
                        );


                        reopenFolder("fetchMessages");
                        fetchMessages(
                            imap_folder.getMessagesByUID(
                                uids_tmp
                            )
                        ); // add missing messages
                    }
                }

                if (query_count == j + 1) {
                    long uid_start = arr_uids.size() > 0 ? arr_uids.get(arr_uids.size() - 1) + 1 : - 1; // TODO Проверить на повторяемость действий у пустых папок

                    db.setRemoved(email, folder_name, uid_start, -1, null); // пометить после
                }
            }

            this.myFolder.setMessages_count(imap_folder.getMessageCount());
            this.myFolder.setMessages_db_count(db.getCountMessages(email_address, folder_name));
        } catch (Exception e) {
            myFolder.setException(e);
        }
    }

    private void checkFlags(String email, String folder_name) {
        try {
            db.setFlags(email, imap_folder.getFullName()); // Обнулить флаги

            HashMap<String, String> flags = new HashMap<>();

            flags.put("KEYWORD $HasAttachment", null);
            flags.put("KEYWORD $Forwarded", null);
            flags.put("KEYWORD $label1", null);
            flags.put("KEYWORD $label2", null);
            flags.put("KEYWORD $label3", null);
            flags.put("KEYWORD $label4", null);
            flags.put("KEYWORD $label5", null);
            flags.put("FLAGGED",  null);
            flags.put("ANSWERED", null);
            flags.put("DELETED",  null);
            flags.put("DRAFT",    null);
            flags.put("UNSEEN",   null);

            for (HashMap.Entry<String, String> flag : flags.entrySet()) {

                flag.setValue((String) imap_folder.doCommand(imapProtocol -> {
                    StringBuilder str_uids = new StringBuilder();
                    Response[] responses   = imapProtocol.command("UID SEARCH " + flag.getKey(), null);
                    String[] arr_out_str   = responses[0].toString().split(" ");

                    if (arr_out_str.length > 2 && !(Integer.parseInt(arr_out_str[2]) > 0)) {
                        System.err.println(Arrays.toString(responses));
                    }

                    if (arr_out_str.length > 2) {
                        str_uids = new StringBuilder(arr_out_str[2]);

                        for (int n = 2; n < arr_out_str.length; n++) {
                            str_uids.append(",").append(arr_out_str[n]);
                        }
                    }

                    return str_uids.toString();
                }));
            }

            if (!flags.get("ANSWERED").equals("")) {
                db.setFlags(email_address, folder_name, "answered", 1, flags.get("ANSWERED"));
            }
            if (!flags.get("DELETED").equals("")) {
                db.setFlags(email_address, folder_name, "deleted", 1, flags.get("DELETED"));
            }
            if (!flags.get("FLAGGED").equals("")) {
                db.setFlags(email_address, folder_name, "flagged", 1, flags.get("FLAGGED"));
            }
            if (!flags.get("DRAFT").equals("")) {
                db.setFlags(email_address, folder_name, "draft", 1, flags.get("DRAFT"));
            }
            if (!flags.get("UNSEEN").equals("")) {
                db.setFlags(email_address, folder_name, "seen", 0, flags.get("UNSEEN"));
            }
            if (!flags.get("KEYWORD $Forwarded").equals("")) {
                db.setFlags(email_address, folder_name, "forwarded", 1, flags.get("KEYWORD $Forwarded"));
            }
            if (!flags.get("KEYWORD $label1").equals("")) {
                db.setFlags(email_address, folder_name, "label_1", 1, flags.get("KEYWORD $label1"));
            }
            if (!flags.get("KEYWORD $label2").equals("")) {
                db.setFlags(email_address, folder_name, "label_2", 1, flags.get("KEYWORD $label2"));
            }
            if (!flags.get("KEYWORD $label3").equals("")) {
                db.setFlags(email_address, folder_name, "label_3", 1, flags.get("KEYWORD $label3"));
            }
            if (!flags.get("KEYWORD $label4").equals("")) {
                db.setFlags(email_address, folder_name, "label_4", 1, flags.get("KEYWORD $label4"));
            }
            if (!flags.get("KEYWORD $label5").equals("")) {
                db.setFlags(email_address, folder_name, "label_5", 1, flags.get("KEYWORD $label5"));
            }
            if (!flags.get("KEYWORD $HasAttachment").equals("")) {
                db.setFlags(email_address, folder_name, "has_attachment", 1, flags.get("KEYWORD $HasAttachment"));
            }

        } catch (Exception e) {
            myFolder.setException(e);
        } finally {
//            System.err.println(folder_name + " checkFlags end");
        }

    }

    private boolean checkRandomMessages(long last_uid_db) {
        int check_count = 0;
        int sqrt  = 0;

        try {
            checkFolder(imap_folder);

            Message last_message = imap_folder.getMessageByUID(last_uid_db);
            if (last_message == null) {
//                return Boolean.parseBoolean(null);
                return false;
            }
            int last_id = last_message.getMessageNumber();

            sqrt = (int) Math.ceil(Math.sqrt(last_id) / 2); // Корень квадратный /2

            int i = 1;

            while (i <= sqrt) {
                Message message = imap_folder.getMessage(1 + (int) (Math.random() * sqrt));
                long uid = imap_folder.getUID(message);
                MyMessage myMessage = db.getMyMessage(emailAccount.getEmailAddress(), imap_folder.getFullName(), uid);

                if (myMessage == null) {
                    continue;
                } else {
                    if (myMessage.compare((IMAPMessage) message, imap_folder, true)) {
                        check_count++;
                    }
                    i++;
                }
            }

        } catch (Exception e) {
            myFolder.setException(e);
        } finally {
//            myFolder.setStatus("end_add_message_folder");
        }

        return (sqrt == check_count);
    }

    private void checkFolder(IMAPFolder imapFolder) throws MessagingException {
        if (!imapFolder.isOpen()) {
            imapFolder.open(Folder.READ_ONLY);
        }
    }

    private void addFolderListenersConnection(IMAPFolder imap_folder) {
        imap_folder.addConnectionListener(new ConnectionListener() {
            @Override
            public void opened(ConnectionEvent connectionEvent) { // TODO проверка изменения сообщений при переподключении/подключении
//                long last_uid_db   = db.getLastAddUID(emailAccount.getEmailAddress(), folder_name);
//                long last_uid_mail = messages_tmp.length > 0 ? imap_folder.getUID(messages_tmp[messages_tmp.length - 1]) : 0;
//
//                messages = imap_folder.getMessagesByUID(last_uid_db + 1, last_uid_mail);
//                fetchMessages(messages);

//                System.err.println("opened !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                checkRemoved(email_address, folder_name);
                myFolder.setStatus("listening");
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                myFolder.setStatus("disconnected");
                reopenFolder("disconnected");
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {
                myFolder.setStatus("closed");
                reopenFolder("closed");
            }
        });
    }

    private void addFolderListenersMessages(IMAPFolder imap_folder) {

        imap_folder.addMessageChangedListener(messageChangedEvent -> {

//            System.out.println("messageChangedEvent");

            try {
                myFolder.updateTime_last_event();

                IMAPMessage imap_message = (IMAPMessage) messageChangedEvent.getMessage();

                db.addEmail(new Email(user_id, email_address, imap_message, folder_name, imap_folder));

            } catch (Exception e) {
                myFolder.setException(e);
            }
        });

        imap_folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                try {
                    myFolder.updateTime_last_event();

                    for (Message message : messageCountEvent.getMessages()) {
                        db.addEmail(new Email(user_id, email_address, message, folder_name, imap_folder));
                    }

                    myFolder.setMessages_count(imap_folder.getMessageCount());
                    myFolder.setMessages_db_count(db.getCountMessages(email_address, folder_name));
                } catch (Exception e) {
                    myFolder.setException(e);
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) { // TODO проверить работу, есть расхождения при обновлении количества удаленных сооблений
                myFolder.updateTime_last_event();

                System.out.println("messagesRemoved!!!");

                try {
                    Message[] messages_tmp = messageCountEvent.getMessages();

                    System.err.println("messagesRemoved count = " + messages_tmp.length);

                    for (Message message_tmp : messages_tmp) {
                        db.setDeleteFlag(email_address, folder_name, imap_folder.getUID(message_tmp));
                    }

                    myFolder.setMessages_db_count(db.getCountMessages(email_address, folder_name));
                    myFolder.setMessages_count(imap_folder.getMessageCount());
                } catch (Exception e) {
                    myFolder.setException(e);
                }

            }
        });
    }

}
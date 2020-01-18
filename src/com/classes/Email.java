package com.classes;

import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

public class Email {

    private static String[] folders_arr = new String[]{"Отправленные", "Sent", "Черновик", "Черновики", "Draft", "Drafts"};

    private int    id = 0;
    private String email_account;
    private String direction = "error";
    private int    user_id;
    private int    client_id = 0;
    private long   uid;
    private String message_id;
    private int    msgno = 0;
    private String from;
    private String to;

    private String cc;
    private String bcc;

    private String in_replay_to;
    private String references = "null";
    private Timestamp date;
    private long    size;
    private String subject;
    private String folder;

    private int    recent         = 0;
    private int    flagged        = 0;
    private int    answred        = 0;
    private int    deleted        = 0;
    private int    seen           = 0;
    private int    draft          = 0;
    private int    user           = 0;
    private int    forwarded      = 0;
    private int    label1         = 0;
    private int    label2         = 0;
    private int    label3         = 0;
    private int    label4         = 0;
    private int    label5         = 0;
    private int    has_attachment = 0;

    private Timestamp update;

    private String tdf_id;

    public Email(int user_id, String email_account, Message imap_message, String folder_name, IMAPFolder imapFolder) {
        try {
            if (!imapFolder.isOpen()) {
                imapFolder.open(Folder.READ_ONLY);
            }

            // TODO проверка на открытую папку

            this.email_account = email_account;

            // TODO Gmail and mail название папки исходящие

//            this.direction = (folder_name.equals("Исходящие") ? "out" : "in");

            String cc = InternetAddress.toString(imap_message.getRecipients(Message.RecipientType.CC));
            this.cc   = cc;

            String bcc = InternetAddress.toString(imap_message.getRecipients(Message.RecipientType.BCC));
            this.bcc   = (bcc == null || bcc.equals("") ? "null" : bcc);

            String from = InternetAddress.toString(imap_message.getFrom());
            this.from = (from == null || from.equals("") ? "null" : from);

            assert from != null;
//            this.direction = from.contains(email_account) ? "out" : "in"; // TODO проверить

//            if(Arrays.stream(new String[]{"Отправленые", "Sent"}).anyMatch(s -> s.equals(folder_name))) {
            this.direction = Arrays.asList(folders_arr).contains(folder_name) ? "out" : "in";

            String to = InternetAddress.toString(imap_message.getRecipients(Message.RecipientType.TO));
            this.to = (to == null || to.equals("") ? "null" : to);

            this.user_id = user_id;

            try {
                String message_id = imap_message.getHeader("Message-ID")[0];
                this.message_id = (message_id == null || message_id.equals("") ? "null" : message_id);
            } catch (NullPointerException e) {
                this.message_id = null;
            }

            try {
                String tdf_id = imap_message.getHeader("X-Tdfid")[0];
                this.tdf_id = (tdf_id == null || tdf_id.equals("") ? "null" : tdf_id);
            } catch (NullPointerException e) {
                this.tdf_id = null;
            }

            String in_replay_to =  InternetAddress.toString(imap_message.getReplyTo());
            this.in_replay_to = (in_replay_to == null || in_replay_to.equals("") ? "null" : in_replay_to);

            Timestamp date = new Timestamp(imap_message.getReceivedDate().getTime());
            this.date = (date == null ? new Timestamp(0) : new Timestamp(imap_message.getReceivedDate().getTime()));

            int size  = imap_message.getSize();
            this.size = size;

            String subject = removeBadChars(imap_message.getSubject());
            this.subject = (subject == null || subject.equals("") ? "null" : subject);

            this.folder = folder_name;
            this.update = new Timestamp(new Date().getTime());


            if (!imapFolder.isOpen()) {
                try {
                    imapFolder.open(IMAPFolder.READ_ONLY);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            long uid = imapFolder.getUID(imap_message);

//            IMAPFolder imap_folder = (IMAPFolder) imap_message.getFolder();

            if (!imapFolder.isOpen()) {
                try {
                    imapFolder.open(IMAPFolder.READ_ONLY);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            if (uid > 0) {
                this.uid = uid;
            } else {
//                this.uid = imap_folder.getUID(imap_message);
                this.uid = imapFolder.getUID(imap_message);
            }

            String out = (String) imapFolder.doCommand(imapProtocol -> {
//                    Response[] responses = imapProtocol.command("FETCH " + imap_message.getMessageNumber() + " (FLAGS UID)", null); // TODO
                Response[] responses = imapProtocol.command("FETCH " + imap_message.getMessageNumber() + " (FLAGS)", null);

                return responses[0].toString();
            });

            if (out.contains("\\Deleted"))     { this.deleted = 1; }
            if (out.contains("\\Answered"))    { this.answred = 1; }
            if (out.contains("\\Draft"))       { this.draft   = 1; }
            if (out.contains("\\Flagged"))     { this.flagged = 1; }
//            if (out.contains("\\Recent"))      { this.recent  = 1; }
            if (out.contains("\\Seen"))        { this.seen    = 1; }
//            if (out.contains("\\User"))        { this.user    = 1; }
            if (out.contains("$Forvard"))      { this.forwarded = 1; }
            if (out.contains("$label1"))       { this.label1  = 1; }
            if (out.contains("$label2"))       { this.label2  = 1; }
            if (out.contains("$label3"))       { this.label3  = 1; }
            if (out.contains("$label4"))       { this.label4  = 1; }
            if (out.contains("$label5"))       { this.label5  = 1; }
            if (out.contains("$HasAttachment")) { this.has_attachment = 1; }

//            Date sent = message.getSentDate();         // когда отправлено
//            Date received = message.getReceivedDate(); // когда получено

//        } catch (MessagingException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Email {"                                  + " \n" +
                "     id             = " + id             + ",\n" +
                "     direction      = " + direction      + ",\n" +
                "     user_id        = " + user_id        + ",\n" +
                "     client_id      = " + client_id      + ",\n" +
                "     uid            = " + uid            + ",\n" +
                "     message_id     = " + message_id     + ",\n" +
                "     msgno          = " + msgno          + ",\n" +
                "     from           = " + from           + ",\n" +
                "     to             = " + to             + ",\n" +
                "     in_replay_to   = " + in_replay_to   + ",\n" +
                "     references     = " + references     + ",\n" +
                "     date           = " + date           + ",\n" +
                "     size           = " + size           + ",\n" +
                "     subject        = " + subject        + ",\n" +
                "     folder         = " + folder         + ",\n" +
                "     recent         = " + recent         + ",\n" +
                "     flagged        = " + flagged        + ",\n" +
                "     answred        = " + answred        + ",\n" +
                "     deleted        = " + deleted        + ",\n" +
                "     seen           = " + seen           + ",\n" +
                "     draft          = " + draft          + ",\n" +
                "     user           = " + user           + ",\n" +
                "     forwarded      = " + forwarded      + ",\n" +
                "     label1         = " + label1         + ",\n" +
                "     label2         = " + label2         + ",\n" +
                "     label3         = " + label3         + ",\n" +
                "     label4         = " + label4         + ",\n" +
                "     label5         = " + label5         + ",\n" +
                "     has_attachment = " + has_attachment + ",\n" +
                "     update         = " + update         + " \n" +
                "     email_account  = " + email_account  + " \n" +
                "}\n";
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public String getDirection() {
        return direction;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public long getUid() {
        return uid;
    }

    public String getMessage_id() {
        return message_id;
    }

    public int getMsgno() {
        return msgno;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getIn_replay_to() {
        return in_replay_to;
    }

    public String getReferences() {
        return references;
    }

    public Timestamp getDate() {
        return date;
    }

    public long getSize() {
        return size;
    }

    public String getSubject() {
        return subject;
    }

    public String getFolder() {
        return folder;
    }

    public int getRecent() {
        return recent;
    }

    public int getFlagged() {
        return flagged;
    }

    public int getAnswred() {
        return answred;
    }

    public int getDeleted() {
        return deleted;
    }

    public int getSeen() {
        return seen;
    }

    public int getDraft() {
        return draft;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public void setMsgno(int msgno) {
        this.msgno = msgno;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public void setIn_replay_to(String in_replay_to) {
        this.in_replay_to = in_replay_to;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setRecent(int recent) {
        this.recent = recent;
    }

    public void setFlagged(int flagged) {
        this.flagged = flagged;
    }

    public void setAnswred(int answred) {
        this.answred = answred;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public void setDraft(int draft) {
        this.draft = draft;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getLabel1() {
        return this.label1;
    }

    public void setLabel1(int label1) {
        this.label1 = label1;
    }

    public int getLabel2() {
        return label2;
    }

    public void setLabel2(int label2) {
        this.label2 = label2;
    }

    public int getLabel3() {
        return label3;
    }

    public void setLabel3(int label3) {
        this.label3 = label3;
    }

    public int getLabel4() {
        return label4;
    }

    public void setLabel4(int label4) {
        this.label4 = label4;
    }

    public int getLabel5() {
        return label5;
    }

    public void setLabel5(int label5) {
        this.label5 = label5;
    }

    public int getHas_attachment() {
        return has_attachment;
    }

    public void setHas_attachment(int has_attachment) {
        this.has_attachment = has_attachment;
    }

    public void setUpdate(Timestamp update) {
        this.update = update;
    }

    public Timestamp getUpdate() {
        return update;
    }

    public int getForwarded() {
        return forwarded;
    }

    public void setForwarded(int forwarded) {
        this.forwarded = forwarded;
    }

    public String getEmail_account() {
        return email_account;
    }

    public void setEmail_account(String email_account) {
        this.email_account = email_account;
    }

    public String getTdf_id() {
        return tdf_id;
    }

    public void setTdf_id(String tdf_id) {
        this.tdf_id = tdf_id;
    }

    public static String removeBadChars(String s) {
        if (s == null) return null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0 ; i < s.length() ; i++) {
            if (Character.isHighSurrogate(s.charAt(i))) { continue; }
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

}

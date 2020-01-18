package com.classes;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Objects;

public class MyMessage {
    private String    email_acount;
    private String    direction;
    private int       user_id;
    private int       client_id;
    private long      uid;
    private String    message_id;
    private int       msgno;
    private String    from;
    private String    to;
    private String    in_reply_to;
    private String    references;
    private Timestamp date;
    private long      size;
    private String    subject;
    private String    folder;

    private int       recent;
    private int       flagged;
    private int       answered;
    private int       deleted;
    private int       seen;
    private int       draft;
    private int       forwarded;

    private int       label1;
    private int       label2;
    private int       label3;
    private int       label4;
    private int       label5;
    private int       has_attachment;

    private Timestamp udate;

    public MyMessage(
            String direction,
            int user_id,
            int client_id,
            long uid,
            String message_id,
            String from,
            String to,
            String in_reply_to,
            String references,
            Timestamp date,
            long size,
            String subject,
            String folder,
            int flagged,
            int answered,
            int deleted,
            int seen,
            int draft,
            int forwarded,
            int label1,
            int label2,
            int label3,
            int label4,
            int label5,
            int has_attachment,
            Timestamp udate,
            String email_account
    ) {
        this.direction      = direction;
        this.user_id        = user_id;
        this.client_id      = client_id;
        this.uid            = uid;
        this.message_id     = message_id;
        this.from           = from;
        this.to             = to;
        this.in_reply_to    = in_reply_to;
        this.references     = references;
        this.date           =  date;
        this.size           = size;
        this.subject        = subject;
        this.folder         = folder;
        this.flagged        = flagged;
        this.answered       = answered;
        this.deleted        = deleted;
        this.seen           = seen;
        this.draft          = draft;
        this.forwarded      = forwarded;
        this.label1         = label1;
        this.label2         = label2;
        this.label3         = label3;
        this.label4         = label4;
        this.label5         = label5;
        this.has_attachment = has_attachment;
        this.udate          = udate;
        this.email_acount   = email_account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyMessage myMessage = (MyMessage) o;
        return size     == myMessage.size     &&
               recent   == myMessage.recent   &&
               flagged  == myMessage.flagged  &&
               answered == myMessage.answered &&
               deleted  == myMessage.deleted  &&
               seen     == myMessage.seen     &&
               draft    == myMessage.draft    &&
               Objects.equals(message_id, myMessage.message_id)   &&
               Objects.equals(from, myMessage.from)               &&
               Objects.equals(to, myMessage.to)                   &&
               Objects.equals(in_reply_to, myMessage.in_reply_to) &&
               Objects.equals(references, myMessage.references)   &&
               Objects.equals(date, myMessage.date)               &&
               Objects.equals(subject, myMessage.subject)         &&
               Objects.equals(folder, myMessage.folder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message_id, from, to, in_reply_to, references, date,
                size, subject, folder, recent, flagged, answered, deleted, seen, draft);
    }

    @Override
    public String toString() {
        return "MyMessage{"     +
                "direction='"   + direction   + '\'' +
                "user_id="      + user_id     +
                "client_id="    + client_id   +
                "uid="          + uid         +
                "message_id='"  + message_id  + '\'' +
                "msgno="        + msgno       +
                "from='"        + from        + '\'' +
                "to='"          + to          + '\'' +
                "in_reply_to='" + in_reply_to + '\'' +
                "references='"  + references  + '\'' +
                "date="         + date        +
                "size="         + size        +
                "subject='"     + subject     + '\'' +
                "folder='"      + folder      + '\'' +
                "recent="       + recent      +
                "flagged="      + flagged     +
                "answered="     + answered    +
                "deleted="      + deleted     +
                "seen="         + seen        +
                "draft="        + draft       +
                "forwarded="    + forwarded   +
                "label1="       + label1      +
                "label2="       + label2      +
                "label3="       + label3      +
                "label4="       + label4      +
                "label5="       + label5      +
                "label1="       + label1      +
                "udate="        + udate       +
                '}';
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public int getMsgno() {
        return msgno;
    }

    public void setMsgno(int msgno) {
        this.msgno = msgno;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getIn_reply_to() {
        return in_reply_to;
    }

    public void setIn_reply_to(String in_reply_to) {
        this.in_reply_to = in_reply_to;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getRecent() {
        return recent;
    }

    public void setRecent(int recent) {
        this.recent = recent;
    }

    public int getFlagged() {
        return flagged;
    }

    public void setFlagged(int flagged) {
        this.flagged = flagged;
    }

    public int getAnswered() {
        return answered;
    }

    public void setAnswered(int answered) {
        this.answered = answered;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int getDraft() {
        return draft;
    }

    public void setDraft(int draft) {
        this.draft = draft;
    }

    public int getUser() {
        return forwarded;
    }

    public void setForwarded(int user) {
        this.forwarded = forwarded;
    }

    public Timestamp getUdate() {
        return udate;
    }

    public void setUdate(Timestamp udate) {
        this.udate = udate;
    }

    public int getLabel1() {
        return label1;
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

    public boolean compareString(String str1, String str2, boolean is_print) {

        if (str1 != null) {
            str1 = new String(str1.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            str1 = str1.replace("\n", "").trim();
        }
        if (str2 != null) {
            str2 = new String(str2.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            str2 = str2.replace("\n", "").trim();
        }

        if (
                (str1 == null || str1.equals("") || str1.equals("null")) &&
                (str2 == null || str2.equals("") || str2.equals("null"))
        ) {
            return true; // TODO null
        }

        if ((str1 == null || str2 == null) || (str1.equals("null") || str2.equals("null"))) {
            return ((str1 == null || str1.equals("null")) && (str2 == null || str2.equals("null")));
        }

        if (str1.length() != str2.length()) {
            if (is_print) {
                System.out.println(str1 + "\u001B[91m" + "!!!!!!!!!!!! length= " + str1.length() + "\u001B[0m");
                System.out.println(str2 + "\u001B[91m" + "!!!!!!!!!!!! length= " + str2.length() + "\u001B[0m");
            }
            return false;
        } else {
            char[] c1 = str1.toCharArray();
            char[] c2 = str2.toCharArray();

            int error_count = 0;

            for (int i = 0, j = 0; i < c1.length; i++, j++) {
                if (is_print) {
                    System.out.println((int) c1[i] + "|" + (int) c2[i]);
                }
                if ((((int) c1[i])) != (((int) c2[j]))) {
                        error_count++;
                }
            }

            return error_count == 0;
        }
    }

    public boolean compareInteger(Integer i1, Integer i2) {
        return compareString(i1.toString(), i2.toString(), false);
    }

    public boolean compareLong(Long l1, Long l2) {
        return compareString(l1.toString(), l2.toString(), false);
    }

    public boolean compare(IMAPMessage imap_message, IMAPFolder imap_folder, boolean is_show_debag) {

        try {
            if (!imap_folder.isOpen()) {
                imap_folder.open(IMAPFolder.READ_ONLY);
            }

            long   mail_UID         = imap_folder.getUID(imap_message);
            String mail_MID         = imap_message.getMessageID();
//            String mail_from        = InternetAddress.toString(imap_message.getFrom());
//            String mail_to          = InternetAddress.toString(imap_message.getRecipients(Message.RecipientType.TO));
//            String mail_reply_to    = InternetAddress.toString(imap_message.getReplyTo());
//            long   mail_size        = imap_message.getSize();
//            String mail_subject     = imap_message.getSubject();
            String mail_folder_name = imap_message.getFolder().getFullName();

            if (is_show_debag) {
                if (!compareLong(this.getUid(), mail_UID)) {
                    compareString(Long.toString(this.getUid()), Long.toString(mail_UID), true);
                    System.out.println("UID( " + this.getUid() + " !=" + mail_UID + " )");
                }

                if (!compareString(this.message_id, mail_MID, false)) {
                    compareString(this.message_id, mail_MID,true);
                    System.out.println("Message_id( != )");
                    System.out.println(this.message_id);
                    System.out.println(mail_MID + ")");

                    if (
                            this.message_id != null &&
                            mail_MID != null        &&
                            this.message_id.length() == mail_MID.length()
                    ) {
                        char[] c1 = this.message_id.toCharArray();
                        char[] c2 = mail_MID.toCharArray();

                        for (int i=0; i < this.message_id.length(); i++) {
                            if (String.valueOf(c1[i]).equals(String.valueOf(c2[i]))) {
                                System.out.println(String.valueOf(c1[i]) + "===" + String.valueOf(c2[i]));
                            }
                        }
                    }
                }

//                if (!compareString(this.from, mail_from, false)) {
//                    compareString(this.from, mail_from, true);
//                    System.out.println("From( != )");
//                    System.out.println(this.from);
//                    System.out.println(mail_from);
//                }
//
//                if (!compareString(this.to, mail_to, false)) {
//                    compareString(this.to, mail_to, true);
//                    System.out.println("To !=");
//                    System.out.println(this.to);
//                    System.out.println(mail_to);
//                }
//
//                if (!compareString(this.in_reply_to, mail_reply_to, false)) {
//                    compareString(this.in_reply_to, mail_reply_to, true);
//                    System.out.println("In_reply_to( != )");
//                    System.out.println(this.in_reply_to);
//                    System.out.println(mail_reply_to);
//                }

//                if (!compareLong(this.size, mail_size)) {
//                    System.out.println("getSize(" + this.size + "!=" + mail_size + ")");
//                }

//                if (!compareString(this.subject, mail_subject, false)) {
//                    compareString(this.subject, mail_subject, true);
//                    System.out.println("subject  != ");
//                    System.out.println(this.subject);
//                    System.out.println(mail_subject);
//                }

                if (!compareString(this.folder, mail_folder_name, false)) {
                    compareString(this.folder, mail_folder_name, true);
                    System.out.println("Folder name( != )");
                    System.out.println(this.folder);
                    System.out.println(mail_folder_name);
                }
            }

            if (
                compareLong(this.getUid(),      mail_UID)      &&
                compareString(this.message_id,  mail_MID, false)      &&
//                compareString(this.from,        mail_from, false)     &&
//                compareString(this.to,          mail_to, false)       &&
//                compareString(this.in_reply_to, mail_reply_to, false) &&
//                compareLong(this.size,          mail_size)     &&
//                compareString(this.subject,     mail_subject, false)  &&
                compareString(this.folder,      mail_folder_name, false)

            ) {
                return true;
            } else {
                System.out.println("UID( " + this.getUid() + " !=" + mail_UID + " )");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}

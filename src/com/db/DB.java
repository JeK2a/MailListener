package com.db;

import com.Main;
import com.classes.Email;
import com.classes.MyMessage;
import com.classes.User;
import com.service.SettingsMail;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO добавить во всех запросах:
// TODO result stmt -> close if != null && isOpen
// TODO добавить задержку и ограничение по количеству в рекурсивные запросы
// TODO folder_name.replaceAll("\"", "\\\""); // TODO может понадобиться, в папка может мспользоваться "

public class DB implements AutoCloseable {

//    private static final String[] params = {
//            "useSSL=false",
//            "serverTimezone=UTC"
//
//    };

    private static final String[] params_arr = {
        "useSSL=false",
        "useUnicode=true",
        "characterEncoding=utf-8"
    };

    private static Connection con;
    private static int count_errors  = 0;
    public  static int count_queries = 0;
    public  static boolean result    = false;

    private String USER;
    private String PASSWORD;
    private String HOST;
    private String PORT;
    private String SCHEMA;
    private String URL;

    public DB() {
        new SettingsMail();

        USER     = SettingsMail.getUser();
        PASSWORD = SettingsMail.getPassword();
        HOST     = SettingsMail.getHost();
        PORT     = SettingsMail.getPort();
        SCHEMA   = SettingsMail.getSchema();
        URL      = "jdbc:mysql://" + HOST + ":" + PORT + "/" + SCHEMA;

//        params[0] = "useSSL="            + SettingsMail.getUsessl();
//        params[1] = "useUnicode="        + SettingsMail.getUseunicode();
//        params[2] = "characterEncoding=" + SettingsMail.getCharacterencoding();

        connectToDB();
    }

    public boolean connectToDB() {

        boolean result = false;

        try {
            if (con != null && !con.isClosed()) {
                return result = true;
            }

            Class.forName("com.mysql.jdbc.Driver"); // MySQL 5
//            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8

            String params = arrayToString(params_arr, "&");

            System.out.println(URL + "?" + params + USER + PASSWORD);

            con = DriverManager.getConnection(URL + "?" + params, USER, PASSWORD); // JDBC подключение к MySQL

            if (con  == null || con.isClosed()) {           // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
            } else {
                result = true;
            }
        } catch(Exception e) {
            System.err.println("Не удалось подключиться к DB");
            e.printStackTrace();

            Main.sleep(30000);
            return connectToDB();
        } finally {
            DB.result = result;
            return result;
        }
    }

    public static void incCount_queries() {
        DB.count_queries++;
    }

    public boolean addEmail(Email email) {

        if (
            email             == null ||
            email.getFolder() == null
        ) { // TODO проверить, в каких ситуациях может возникать и как влияет return
            new NullPointerException();
            return false;
        }

        String query = "" +
            "INSERT INTO `a_api_emails`( " +
            "    `direction`, "      + // 1
            "    `user_id`, "        + // 2
            "    `uid`, "            + // 3
            "    `message_id`, "     + // 4
            "    `from`, "           + // 5
            "    `from_decode`, "    + // 5
            "    `to`, "             + // 6
            "    `to_decode`, "      + // 6
            "    `cc`, "             + // 7
            "    `bcc`, "            + // 8
            "    `in_reply_to`, "    + // 9
            "    `references`, "     + //10
            "    `message_date`, "   + //11
            "    `size`, "           + //12
            "    `subject`, "        + //13
            "    `folder`, "         + //14
            "    `flagged`, "        + //15
            "    `answered`, "       + //16
            "    `deleted`, "        + //17
            "    `removed`, "        + //18
            "    `seen`, "           + //19
            "    `draft`, "          + //20
            "    `forwarded`, "      + //21
            "    `label_1`, "        + //22
            "    `label_2`, "        + //23
            "    `label_3`, "        + //24
            "    `label_4`, "        + //25
            "    `label_5`, "        + //26
            "    `has_attachment`, " + //27
            "    `time`, "           + //28
            "    `email_account`, "  + //29
            "    `tdf_id` "          + //30
            ") "                     +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
            "    ON DUPLICATE KEY UPDATE " +
            "        `direction`      = VALUES(`direction`), "      +
            "        `user_id`        = VALUES(`user_id`), "        +
            "        `message_id`     = VALUES(`message_id`), "     +
            "        `from`           = VALUES(`from`), "           +
            "        `from_decode`    = VALUES(`from_decode`), "    +
            "        `to`             = VALUES(`to`), "             +
            "        `to_decode`      = VALUES(`to_decode`), "      +
            "        `cc`             = VALUES(`cc`), "             +
            "        `bcc`            = VALUES(`bcc`), "            +
            "        `in_reply_to`    = VALUES(`in_reply_to`), "    +
            "        `references`     = VALUES(`references`), "     +
            "        `message_date`   = VALUES(`message_date`), "   +
            "        `size`           = VALUES(`size`), "           +
            "        `subject`        = VALUES(`subject`), "        +
            "        `flagged`        = VALUES(`flagged`), "        +
            "        `answered`       = VALUES(`answered`), "       +
            "        `deleted`        = VALUES(`deleted`), "        +
            "        `removed`        = VALUES(`removed`), "        +
            "        `seen`           = VALUES(`seen`), "           +
            "        `draft`          = VALUES(`draft`), "          +
            "        `forwarded`      = VALUES(`forwarded`), "      +
            "        `label_1`        = VALUES(`label_1`), "        +
            "        `label_2`        = VALUES(`label_2`), "        +
            "        `label_3`        = VALUES(`label_3`), "        +
            "        `label_4`        = VALUES(`label_4`), "        +
            "        `label_5`        = VALUES(`label_5`), "        +
            "        `has_attachment` = VALUES(`has_attachment`), " +
            "        `time`           = VALUES(`time`), "           +
            "        `tdf_id`         = VALUES(`tdf_id`),"          +
            "        `new`            = 0, "                        +
            "        `time_updated`   = NOW() "                     +
            ";";

        try {
            incCount_queries();
            PreparedStatement prepare_statement_tmp = con.prepareStatement(query);

            prepare_statement_tmp.setString(1, email.getDirection());
            prepare_statement_tmp.setInt(2, email.getUser_id());
            prepare_statement_tmp.setLong(3, email.getUid());
            prepare_statement_tmp.setString(4, email.getMessage_id());
            prepare_statement_tmp.setString(5, email.getFrom());
            prepare_statement_tmp.setString(6, email.getFrom_decode());
            prepare_statement_tmp.setString(7, email.getTo());
            prepare_statement_tmp.setString(8, email.getTo_decode());
            prepare_statement_tmp.setString(9, email.getCc());
            prepare_statement_tmp.setString(10, email.getBcc());
            prepare_statement_tmp.setString(11, email.getIn_replay_to());
            prepare_statement_tmp.setString(12, email.getReferences());
            prepare_statement_tmp.setTimestamp(13, email.getDate());
            prepare_statement_tmp.setLong(14, email.getSize());
            prepare_statement_tmp.setString(15, email.getSubject());
            prepare_statement_tmp.setString(16, email.getFolder());
            prepare_statement_tmp.setInt(17, email.getFlagged());
            prepare_statement_tmp.setInt(18, email.getAnswred());
            prepare_statement_tmp.setInt(19, email.getDeleted());
            prepare_statement_tmp.setInt(20, 0); // TODO !!!!!
            prepare_statement_tmp.setInt(21, email.getSeen());
            prepare_statement_tmp.setInt(22, email.getDraft());
            prepare_statement_tmp.setInt(23, email.getForwarded());
            prepare_statement_tmp.setInt(24, email.getLabel1());
            prepare_statement_tmp.setInt(25, email.getLabel2());
            prepare_statement_tmp.setInt(26, email.getLabel3());
            prepare_statement_tmp.setInt(27, email.getLabel4());
            prepare_statement_tmp.setInt(28, email.getLabel5());
            prepare_statement_tmp.setInt(29, email.getHas_attachment());
            prepare_statement_tmp.setTimestamp(30, email.getUpdate());
            prepare_statement_tmp.setString(31, email.getEmail_account());
            prepare_statement_tmp.setString(32, email.getTdf_id());

            prepare_statement_tmp.executeUpdate();
        } catch (
            com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
            com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {
            e.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            if (++count_errors > 10) {
                return false;
            } else {
                Main.sleep();
                if (connectToDB()) {
                    return reAddEmail(email);
                }
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
            System.err.println(query);
            System.err.println(email);
        } catch (Exception e) {
            System.err.println("addEmail error"); // TODO tmp
            e.printStackTrace();
            System.err.println(query);
            System.err.println(email);

            return reAddEmail(email);
        }

        return true;
    }

    private boolean reAddEmail(Email email) {
        if (count_errors++ < 5 && connectToDB()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return addEmail(email);
        } else {
            count_errors = 0;
            return false;
        }
    }

    public ArrayList<User> getUsers() {

        String query = "" +
            "SELECT " +
            "    `users`.`id`, "            +
            "    `users`.`user_id`, "       +
            "    `users`.`email`, "         +
            "    `users`.`password`, "      +
            "    `users`.`is_monitoring`, " +
            "    `users`.`is_default`, "    +
            "    `settings`.`host`, "       +
            "    `settings`.`port`, "       +
            "    `users`.`login`, "         +
            "    `users`.`name_from`, "     +
            "    `settings`.`charset`, "    +
            "    `settings`.`secure`, "     +
            "    `users`.`success` "        +
            "FROM `" + SettingsMail.getTable_users() + "` AS `users` " +
            "INNER JOIN `a_my_emails_settings` AS `settings` " +
            "   ON " +
            "       `users`.`email_provider` = `settings`.`provider` AND " +
            "       `settings`.`type` = 'imap'  " +
            "WHERE " +
            "   `users`.`id` = 304 " +
//                "   `id` = 538 " +
//            "   `users`.`email` = \"sl@tdfort.ru\" " +
//            "   `users`.`email` = \"me@tdfort.ru\" " +
//            "   `users`.`is_monitoring` = 1 " +
            "ORDER  BY `users`.`email`;"
//                +
//                "`success` = 1"
        ; // TODO success

        System.out.println(query);

        ArrayList<User> users = new ArrayList<>();

        try {
            Statement stmt = con.createStatement();
            incCount_queries();
            ResultSet rs_tmp = stmt.executeQuery(query);

            while (rs_tmp.next()) {
                users.add(
                    new User(
                        rs_tmp.getInt(1),
                        rs_tmp.getInt(2),
                        rs_tmp.getString(3),
                        rs_tmp.getString(4),
                        rs_tmp.getBoolean(5),
                        rs_tmp.getBoolean(6),
                        rs_tmp.getString(7),
                        rs_tmp.getInt(8),
                        rs_tmp.getString(9),
                        rs_tmp.getString(10),
                        rs_tmp.getString(11),
                        rs_tmp.getString(12),
                        rs_tmp.getInt(13)
                    )
                );
            }
        } catch (
                com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
                com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {
            e.printStackTrace();
            if (++count_errors > 10) {
                return null;
            } else {
                Main.sleep();
                if (connectToDB()) {
                    return getUsers();
                }
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
            System.err.println(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public long getCountMessages(String email_account, String folder_name) {

        String query =
            "SELECT COUNT(`uid`) " +
            "FROM `a_api_emails` " +
            "WHERE " +
            "    `email_account` = \"" + email_account + "\" AND " +
            "    `folder`        = \"" + folder_name.replace("\"", "\\\"")   + "\" AND " +
            "    `removed`       = 0;" ;

        long count_messages = 0;

        try {
            if (con.isClosed()) {
                connectToDB();
            }

            incCount_queries();
            Statement stmt = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            if (rs_tmp.next()) {
                count_messages = rs_tmp.getLong(1);
            }

            rs_tmp.close();
            stmt.close();
        } catch (
            com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
            com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {
            e.printStackTrace();
            if (++count_errors > 10) {
                return 0;
            } else {
                Main.sleep();
                if (connectToDB()) {
                    return getCountMessages(email_account, folder_name);
                }
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
            System.err.println(query);
        } catch (Exception e) {
            e.printStackTrace();
            return getCountMessages(email_account, folder_name);
        }

        return count_messages;
    }

    public int updateSuccess(String email_address, int success) {

        String query =
            "UPDATE `a_my_users_emails` " +
            "SET `success` = '" + success + "' " +
            "WHERE `email` = '" + email_address + "';";

        int result = 0;

        try {
            if (con.isClosed()) {
                connectToDB();
            }

            incCount_queries();
            Statement stmt = con.createStatement();
            result = stmt.executeUpdate(query);
        } catch (
                com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
                com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {
            e.printStackTrace();
            if (++count_errors > 10) {
                return 0;
            } else {
                Main.sleep();
                if (connectToDB()) {
                    return updateSuccess(email_address, success);
                }
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
            System.err.println(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean updateQuery(String query) {
        Statement stmt = null;

        try {
            if (con.isClosed()) {
                connectToDB();
            }

            incCount_queries();

            stmt = con.createStatement();
            stmt.executeUpdate(query); // TODO
            stmt.close();
        } catch (
//            com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
//            com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException |
            com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException e
        ) {
//            e.printStackTrace();
//            if (++count_errors > 10) {
//                return false;
//            } else {
//                Main.sleep();
//                if (connectToDB()) {
//                    return updateQuery(query);
//                }
//            }
//        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
//            e.printStackTrace();
//            System.err.println(query);
//        } catch (SQLException e) {
//            if (connectToDB()) {
//                updateQuery(query);
//            }
//            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println(query);
        } finally {
            try {
                if (stmt != null && !stmt.isClosed()) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public void changeFolderName(Email email, String new_folder_name) { // TODO изменить у сообщений имя папки (проверить)

        String query =
            "UPDATE `a_my_emails` " +
            "SET " +
            "    `folder` = '" + new_folder_name   + "', " +
            "    `time`   = '" + email.getUpdate() + "'  " +
            "WHERE" +
            "    `folder`     = '" + email.getFolder()     + "' AND " +
            "    `message_id` = '" + email.getMessage_id() + "';";

        updateQuery(query);

    }

    @Override
    public void close() {
        try {
            if (con != null && !con.isClosed()) { con.close(); }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String arrayToString(String[] arr, String symbol) {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            str.append(arr[i]);

            if (i != (arr.length - 1)) {
                str.append(symbol);
            }
        }

        return str.toString();
    }

    public boolean setDeleteFlag(String email_address, String folder_name, long uid) { // TODO изменение флага сообщенией на удаленное (проверить)

        String query =
            "UPDATE `a_api_emails` " +
            "SET " +
            "    `deleted` = 1,    " +
            "    `removed` = 1,    " +
            "    `time`    = NOW() " +
            "WHERE " +
            "    `email_account` = '" + email_address + "' AND " +
            "    `folder`        = '" + folder_name   + "' AND " +
            "    `uid`           = '" + uid           + "';";

        return updateQuery(query);
    }

    public boolean setFlags(String email, String folder_name) { // Обнуление флагов у всех писем // TODO возможно выполнять на всем аккаунте, а не на одной папке

        if (email.isEmpty() || folder_name.isEmpty()) {
            new NullPointerException();
        }

        String query =
            "UPDATE `a_api_emails` " +
            "SET " +
            "    `flagged`        = 0, " +
            "    `answered`       = 0, " +
            "    `deleted`        = 0, " +
            "    `seen`           = 1, " +
            "    `draft`          = 0, " +
            "    `forwarded`      = 0, " +
            "    `label_1`        = 0, " +
            "    `label_2`        = 0, " +
            "    `label_3`        = 0, " +
            "    `label_4`        = 0, " +
            "    `label_5`        = 0, " +
            "    `has_attachment` = 0  " +
            "WHERE " +
            "   `email_account` = '" + email       + "' AND " +
            "   `folder`        = '" + folder_name + "';";

//        if (email.equals("")) { // TODO проверить работоспособность и внести все в один запрос
//            query += String.format(" AND `email_account` = '%d' ", email.toString());
//        }
//        if (folder_name != null) {
//            query += String.format(" AND `folder` = '%s' ", folder_name);
//        }

        return updateQuery(query);
    }

    public int setFlags(String email_account, String folder_name, String flag_name, int flag_value, String uids) { // Проставление флагов у определенных писем
        String query =
            "UPDATE `a_api_emails` " +
            "SET `" + flag_name + "` = " + flag_value + " " +
            "WHERE  " +
            "   `email_account` = '" + email_account + "' AND " +
            "   `folder`        = '" + folder_name   + "' AND " +
            "   `uid` IN ("+uids+");";

        int result = 0;

        try {
            Statement stmt = con.createStatement();
            result = stmt.executeUpdate(query);
        } catch (
            com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
            com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {
            e.printStackTrace();
            if (++count_errors > 10) {

            } else {
                Main.sleep();
                if (connectToDB()) {
                    return setFlags(email_account, folder_name, flag_name, flag_value, uids);
                }
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
            System.err.println(query);
        } catch (Exception e) {
            e.printStackTrace();
            return setFlags(email_account, folder_name, flag_name, flag_value, uids);
        }

        return result;
    }

    public boolean setRemoved(String email, String folder_name, long uid_start, long uid_end, ArrayList<Long> uids) { // TODO можно ли объединить с setDelete
        String query =
                "UPDATE `a_api_emails` " +
                "SET " +
                "    `removed` = 1, " +
                "    `time` = NOW() " +
                "WHERE  " +
                "   `folder`        = '" + folder_name + "' AND " +
                "   `email_account` = '" + email       + "' ";

        if (uid_start > -1) {
            query += " AND `uid` >= " + uid_start + " ";
        }

        if (uid_end > -1) {
            query += " AND `uid` <= " + uid_end + " ";
        }

        if (uids != null && uids.size() > 0) {
            StringBuilder str_uids = new StringBuilder(String.valueOf(uids.get(0)));

            for (int n = 1; n < uids.size(); n++) {
                str_uids.append(",").append(uids.get(n));
            }

            query += " AND `uid` NOT IN (" + str_uids + ") ";
        }

        return updateQuery(query);
    }

    public long[] getMissingUIDs(String email_address, String folder_name, long uid_start, long uid_end, ArrayList<Long> uids) {

        long[] missing_uids = new long[0];

        String query = "" +
            "SELECT `uid` " +
            "FROM `a_api_emails` " +
            "WHERE  " +
            "   `folder`        = '" + folder_name   + "' AND " +
            "   `email_account` = '" + email_address + "' AND " +
            "   `uid`          >= "  + uid_start     + "  AND " +
            "   `uid`          <= "  + uid_end       + "  AND " +
            "   `removed`       = 0 ";

        if (uids != null && uids.size() > 0) {
            StringBuilder str_uids = new StringBuilder(String.valueOf(uids.get(0)));

            for (int n = 1; n < uids.size(); n++) {
                str_uids.append(",").append(uids.get(n));
            }

            query += " AND `uid` IN (" + str_uids + ") ";
        }

        try {
            Statement stmt   = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            assert uids != null;
            ArrayList<Long> uids_tmp = (ArrayList<Long>) uids.clone();

            while (rs_tmp.next()) {
                uids_tmp.remove(rs_tmp.getLong(1));
            }

            missing_uids = new long[uids_tmp.size()];

            for (int i = 0; i < uids_tmp.size(); i++) {
                missing_uids[i] = uids_tmp.get(i);
            }

            rs_tmp.close();
            stmt.close();
        } catch (
                com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
                com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {
            e.printStackTrace();
            if (++count_errors > 10) {

            } else {
                Main.sleep();
                if (connectToDB()) {
                    return getMissingUIDs(email_address, folder_name, uid_start, uid_end, uids);
                }
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
            System.err.println(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return missing_uids;
    }

    public boolean deleteMessage(int user_id, String folder_name, long uid) { // TODO почему не используется?
        String query =
            "DELETE `a_api_emails` " +
            "SET `deleted` = 1, " +
            "    `time` = '" + new Timestamp(new Date().getTime()) + "' " +
            "WHERE  " +
            "    `user_id` = '" + user_id     + "' AND " +
            "    `folder`  = '" + folder_name + "' AND " +
            "    `uid`     = '" + uid + "'";

        return updateQuery(query);
    }

//    public boolean updateAccountError(int id,  String error_text) {
//        return updateQuery(id, "error", error_text);
//    }

    public boolean updateQuery(int id, String key, String value) { // TODO почему не используется?
        String query =
                "UPDATE `a_my_users_emails` " +
                "SET `" + key + "` = '" + value + "' " +
                "WHERE `id` = " + id + ";";

        return updateQuery(query);
    }

    public long getLastAddUID(String account_email, String folder_name) {
        long last_add_uid = 0;

        String query =
            "SELECT `uid` " +
            "FROM `a_api_emails` " +
            "WHERE " +
            "   `email_account` = '" + account_email +"' AND " +
            "   `folder`        = '" + folder_name + "'  AND " +
            "   `removed`       = 0 " +
            "ORDER BY `uid` DESC " +
            "LIMIT 1";

        try {
            Statement stmt = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            if (rs_tmp.next()) {
                last_add_uid = rs_tmp.getLong(1);
            }

            rs_tmp.close();
            stmt.close();
        } catch (
                com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
                        com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {
            e.printStackTrace();
            if (++count_errors > 10) {

            } else {
                Main.sleep();
                if (connectToDB()) {
                    return getLastAddUID(account_email, folder_name);
                }
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
            System.err.println(query);
        } catch (Exception e) {
            e.printStackTrace();
            Main.sleep(1000);
            return getLastAddUID(account_email, folder_name);
        }

        return last_add_uid;
    }

    public boolean deleteMessages(String email_address, String folder_name) {
        String query =
            "DELETE FROM `a_api_emails` " +
            "WHERE " +
            "    `email_account` = '" + email_address + "' AND " +
            "    `folder`        = '" + folder_name + "';";

        return updateQuery(query);
    }

    public MyMessage getMyMessage(String email_address, String folder_name, long uid) {
        String query = "" +
            "SELECT " +
            "    `direction`, "      + //1
            "    `user_id`, "        + //2
            "    `uid`, "            + //4
            "    `message_id`, "     + //5
            "    `from`, "           + //6
            "    `to`, "             + //7
            "    `in_reply_to`, "    + //8
            "    `references`, "     + //9
            "    `message_date`, "   + //10
            "    `size`, "           + //11
            "    `subject`, "        + //12
            "    `folder`, "         + //13
            "    `flagged`, "        + //14
            "    `answered`, "       + //15
            "    `deleted`, "        + //16
            "    `seen`, "           + //17
            "    `draft`, "          + //18
            "    `forwarded`, "      + //19
            "    `label_1`, "        + //20
            "    `label_2`, "        + //21
            "    `label_3`, "        + //22
            "    `label_4`, "        + //23
            "    `label_5`, "        + //24
            "    `has_attachment`, " + //25
            "    `time`, "           + //26
            "    `email_account` "   + //27
            "FROM `a_api_emails` "   +
            "WHERE " +
            "    `email_account` = '" + email_address + "' AND " +
            "    `uid`           = '" + uid + "'           AND " +
            "    `folder`        = '" + folder_name + "';";

        try {
            Statement stmt   = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            if (rs_tmp.next()) {
                return new MyMessage(
                    rs_tmp.getString("direction"),
                    rs_tmp.getInt("user_id"),
                    rs_tmp.getLong("uid"),
                    rs_tmp.getString("message_id"),
                    rs_tmp.getString("from"),
                    rs_tmp.getString("to"),
                    rs_tmp.getString("in_reply_to"),
                    rs_tmp.getString("references"),
                    rs_tmp.getTimestamp("message_date"),
                    rs_tmp.getLong("size"),
                    rs_tmp.getString("subject"),
                    rs_tmp.getString("folder"),
                    rs_tmp.getInt("flagged"),
                    rs_tmp.getInt("answered"),
                    rs_tmp.getInt("deleted"),
                    rs_tmp.getInt("seen"),
                    rs_tmp.getInt("draft"),
                    rs_tmp.getInt("forwarded"),
                    rs_tmp.getInt("label_1"),
                    rs_tmp.getInt("label_2"),
                    rs_tmp.getInt("label_3"),
                    rs_tmp.getInt("label_4"),
                    rs_tmp.getInt("label_5"),
                    rs_tmp.getInt("has_attachment"),
                    rs_tmp.getTimestamp("time"),
                    rs_tmp.getString("email_account")
                );
            }

            rs_tmp.close();
            stmt.close();

        } catch (
                com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
                com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {
            e.printStackTrace();
            if (++count_errors > 10) {

            } else {
                Main.sleep();
                if (connectToDB()) {
                    return getMyMessage(email_address, folder_name, uid);
                }
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
            System.err.println(query);
        } catch (Exception e) {
            return getMyMessage(email_address, folder_name, uid);
        }

        return null;
    }

//    public ConcurrentHashMap<Integer, String> getDecode(String dir) {
//        String query =
//            "SELECT " +
//            "   `emails`.`id`, " +
//            "   `emails`.`" + dir + "` " +
//            "FROM `a_api_emails` AS `emails` " +
//            "WHERE " +
//            "   `emails`.`" + dir + "` LIKE '?=%' escape \'?\' AND " +
//            "   `emails`.`" + dir + "_decode` IS NULL " +
//            "ORDER BY `id` " +
//            "LIMIT 5000 OFFSET 0 " +
//            ";";
//
//        ConcurrentHashMap<Integer, String> emails_cut = new ConcurrentHashMap<>();
//
//        try {
//            Statement stmt = con.createStatement();
//            incCount_queries();
//            ResultSet rs_tmp = stmt.executeQuery(query);
//
//            while (rs_tmp.next()) {
//                emails_cut.put(
//                    rs_tmp.getInt(1),
//                    rs_tmp.getString(2)
//
//                );
//            }
//        } catch (
//                com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
//                com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
//        ) {
//            e.printStackTrace();
//            if (++count_errors > 10) {
//                return null;
//            } else {
//                Main.sleep();
//                if (connectToDB()) {
//                    return getDecode(dir);
//                }
//            }
//        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
//            e.printStackTrace();
//            System.err.println(query);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return emails_cut;
//    }
}
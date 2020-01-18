package com;

import com.classes.User;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyProperties extends Properties {

    private String IMAP_server;
    private String IMAP_auth_email;
    private String IMAP_auth_password;

    private ExecutorService es = Executors.newCachedThreadPool();

    public MyProperties(User user) {
        this.IMAP_server        = user.getHost();
        this.IMAP_auth_email    = user.getEmail();
        this.IMAP_auth_password = user.getPassword();

//        put("mail.debug"    , SettingsMail.getMail_debug());
        put("mail.debug"    , true);
        put("mail.imap.port", user.getPort());

        put("mail.imap.usesocketchannels", "true");

        // создать или выбрать соответствующую очередь событий
//        put("mail.event.scope", "session"); //
        put("mail.event.scope", "application");
//        put("mail.event.scope", "store");

        put("mail.event.executor", es);

        if (
            user.getSecure() != null &&
            (
                user.getSecure().equals("ssl") ||
                user.getSecure().equals("tls") ||
                user.getSecure().equals("SSL") ||
                user.getSecure().equals("TLS")
            )
        ) {
            put("mail.store.protocol" , "imaps");
            put("mail.imap.ssl.enable", "true");
        } else {
            put("mail.store.protocol" , "imap");
        }
//        put("mail.imap.statuscachetimeout", "500");


////        put("mail.debug"          , SettingsMail.getMail_debug());
//        put("mail.debug"          , true);
////        put("mail.imap.connectionpool.debug", SettingsMail.getMail_debug()); // Flag to toggle debugging of the connection pool.
//        put("mail.store.protocol" , "imaps");
//        put("mail.imap.port"      , user.getPort());
////        put("mail.smtp.userset"   , "false");
////        put("mail.imap.userset"   , "false");
////        put("mail.imap.statuscachetimeout", "5000");
////        put("mail.imap.connectionpoolsize", "20");
////        put("mail.imap.separatestoreconnection", "true"); // использовать выделенное хранилище
////        put("mail.imap.connectionpooltimeout", "100000"); // connection pool
//
//        if (user.getSecure().equals("ssl") || user.getSecure().equals("tls") ||
//            user.getSecure().equals("SSL") || user.getSecure().equals("TLS"))
//        {
//            put("mail.imap.ssl.enable", "true");
//        }
    }

    public ExecutorService getEs() {
        return es;
    }

    public String getIMAPServer() {
        return IMAP_server;
    }

    public void setIMAPServer(String IMAP_server) {
        this.IMAP_server = IMAP_server;
    }

    public String getIMAPAuthEmail() {
        return IMAP_auth_email;
    }

    public void setIMAPAuthEmail(String IMAP_auth_email) {
        this.IMAP_auth_email = IMAP_auth_email;
    }

    public String getIMAPAuthPassword() {
        return IMAP_auth_password;
    }

    public void setIMAPAuthPassword(String IMAP_auth_password) {
        this.IMAP_auth_password = IMAP_auth_password;
    }
}

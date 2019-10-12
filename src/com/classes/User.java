package com.classes;

public class User {
    private int     id;
    private int     user_id;
    private String  email;
    private String  password;
    private boolean is_monitoring;
    private boolean is_default;
    private String  host;
    private int     port;
    private String  login;
    private String  name_from;
    private String  charset;
    private String  secure;
    private int     success;

    public User(
               int     id,
               int     user_id,
               String  email,
               String  password,
               boolean is_monitoring,
               boolean is_default,
               String  host,
               int     port,
               String  login,
               String  name_from,
               String  charset,
               String  secure,
               int     success
    ) {
        this.id            = id           ;
        this.user_id       = user_id      ;
        this.email         = email        ;
        this.password      = password     ;
        this.is_monitoring = is_monitoring;
        this.is_default    = is_default   ;
        this.host          = host         ;
        this.port          = port         ;
        this.login         = login        ;
        this.name_from     = name_from    ;
        this.charset       = charset      ;
        this.secure        = secure       ;
        this.success       = success      ;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \""            + id            + "\"," +
                "\"user_id\": \""       + user_id       + "\"," +
                "\"email\": \""         + email         + "\"," +
                "\"is_monitoring\": \"" + is_monitoring + "\"," +
                "\"is_default\": \""    + is_default    + "\"," +
                "\"host\": \""          + host          + "\"," +
                "\"port\": \""          + port          + "\"," +
                "\"login\": \""         + login         + "\"," +
                "\"name_from\": \""     + name_from     + "\"," +
                "\"charset\": \""       + charset       + "\"," +
                "\"secure\": \""        + secure        + "\"}";
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isIs_monitoring() {
        return is_monitoring;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getLogin() {
        return login;
    }

    public String getName_from() {
        return name_from;
    }

    public String getCharset() {
        return charset;
    }

    public String getSecure() {
        return secure;
    }
}
package com.service;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

// Получение настроек из XML файла
public class SettingsMail {

    private static String user;
    private static String password;
    private static String host;
    private static String port;
    private static String schema;
    private static String usessl;
    private static String useunicode;
    private static String characterencoding;

    private static String table_users;
    private static String table_emails;

    private static String  mail_debug;
    private static boolean session_debug;

    private static boolean wait_folder;
    private static boolean wait_user;

    public SettingsMail() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File("config_my.xml");
            doc = builder.parse(file);
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            System.err.println(ex);
        }

        assert doc != null;
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {

            Node child = children.item(i);

            if (child instanceof Element) {
                Element childElement = (Element) child;
                Text textNode = (Text) childElement.getFirstChild();
                String text = textNode.getData().trim();

                switch (childElement.getTagName()) {
                    case "user"              : user              = text; break;
                    case "password"          : password          = text; break;
                    case "host"              : host              = text; break;
                    case "port"              : port              = text; break;
                    case "schema"            : schema            = text; break;
                    case "usessl"            : usessl            = text; break;
                    case "useunicode"        : useunicode        = text; break;
                    case "characterencoding" : characterencoding = text; break;

                    case "table_users"       : table_users       = text; break;
                    case "table_emails"      : table_emails      = text; break;

                    case "mail_debug"        : mail_debug        = text; break;
                    case "session_debug"     : session_debug     = Boolean.parseBoolean(text); break;

                    case "wait_folder"       : wait_folder       = Boolean.parseBoolean(text); break;
                    case "wait_user"         : wait_user         = Boolean.parseBoolean(text); break;
                }
            }
        }
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static String getHost() {
        return host;
    }

    public static String getPort() {
        return port;
    }

    public static String getSchema() {
        return schema;
    }

    public static String getUsessl() {
        return usessl;
    }

    public static String getUseunicode() {
        return useunicode;
    }

    public static String getCharacterencoding() {
        return characterencoding;
    }

    public static String getMail_debug() {
        return mail_debug;
    }

    public static boolean getSession_debug() {
        return session_debug;
    }

    public static String getTable_users() {
        return table_users;
    }

    public static boolean getWaitFolder() {
        return wait_folder;
    }

    public static boolean getWaitUser() {
        return wait_user;
    }

    public static String getTable_emails() {
        return table_emails;
    }
}

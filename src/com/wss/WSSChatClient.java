package com.wss;

import com.Main;
import com.classes.EmailAccount;
import com.db.DB;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.threads.Mailing;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLContext;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSSChatClient {
    private static WebSocket webSocket = null;
    private static WebSocketFactory webSocketFactory;
    private static WebSocketAdapter webSocketAdapter;

    public static boolean result = false;

    public WSSChatClient() {
        restart();
    }

    private void restart() {
        try {
            webSocketFactory = new WebSocketFactory();

            webSocketAdapter = new WebSocketAdapter(){
                @Override
                public void onTextMessage(WebSocket ws, String message) {
                try {
                    if (!message.contains("{")) {
                        System.err.println("|||" + message + "|||");
                        return;
                    }

                    JSONObject jsonArray = (JSONObject) getArrayFromJSON(message);

                    String command = String.valueOf(jsonArray.get("message"));

                    switch (command) {
                        case "restart":
                            System.out.println("===================================");
                            System.out.println(Main.mailing_tread.isAlive());
                            Main.is_restart = true;
                            System.out.println("===================================");
                            break;
                        case "stop":
                            System.out.println("========================STOP========================");
                            System.exit(0);
                            break;
                        case "close":
                            webSocket.sendClose();
                            break;
                        case "info":
                            // TODO
                            break;
                        case "status":
                            System.out.println("start accounts = " + Mailing.emailAccounts.size());

                            ConcurrentHashMap<String, EmailAccount> tmpEmailAccounts = new ConcurrentHashMap<>(Mailing.emailAccounts); // (ConcurrentHashMap<Integer, EmailAccount>) Mailing.emailAccounts; // TODO создать дубль

                            String json = getJsonFromMap(tmpEmailAccounts);

                            json = "{\"accounts\": " + json + ", \"count_queries\": " + DB.count_queries + "}"; // TODO обернуть в SQL

                            sendText("json", json);

                            System.gc();

                            break;
                        default:
                            System.out.println("Error command - " + command);
                    }
                } catch (Exception e) {
                    System.err.println("|||" + message + "|||");
                    e.printStackTrace();
                }
                }
            };

            // Create a custom SSL context.
            SSLContext context = NaiveSSLContext.getInstance("TLS");

            // Set the custom SSL context.
            webSocketFactory.setSSLContext(context);
            webSocketFactory.setVerifyHostname(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connectToWSS() {
        boolean result = false;
        try {
            if (webSocket != null && webSocket.isOpen()) {
                webSocket.sendClose();
                webSocket.disconnect();
            }

            restart();

            webSocket = webSocketFactory.createSocket("wss://my.tdfort.ru:10001/?ws_group=worker:javamail");
            webSocket.addListener(webSocketAdapter);
            webSocket.connect();
            result = true;
        } catch (com.neovisionaries.ws.client.WebSocketException e) {
            System.out.println("Не удалось переподключиться к WSS сокету");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            WSSChatClient.result = result;
            return result;
        }
    }

    public static void sendText(String subject, String text) {

//        if (!subject.equals("json")) return; // TODO выпилить все, кроме json

        if (webSocket != null && webSocket.isOpen()) {
            text = forJSON(text);
            webSocket.sendText("{\"subject\":\"" + subject + "\", \"message\":\"" + text + "\"}");
        } else {
            System.err.println("error send");
        }
    }

    public static String forJSON(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int len = input.length();

        final StringBuilder result = new StringBuilder(len + len / 4); // сделаем небольшой запас, чтобы не выделять память потом
        final StringCharacterIterator iterator = new StringCharacterIterator(input);
        char ch = iterator.current();

        while (ch != CharacterIterator.DONE) {
            switch (ch) {
                case '\n': result.append("<br>"); break;
                case '\r': result.append("\\r");  break;
                case '\'': result.append("\\\'"); break;
//                case '"': result.append("\\\""); break;
                case '\"': result.append("\\\""); break;
                case '\t': result.append(" ");    break;
                default: result.append(ch);       break;
            }
            ch = iterator.next();
        }
        return result.toString();
    }

    private static Object getArrayFromJSON(String jsonStr) {
        try {
            return new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            return false;
        }
    }

    private String getJsonFromMap(ConcurrentHashMap<String, EmailAccount> map) {
        StringBuffer tmpStr = new StringBuffer("{ ");

        for (Map.Entry<String, EmailAccount> e: map.entrySet()){
            tmpStr.append("\"").append(e.getKey()).append("\": ").append(e.getValue()).append(",");
        }

        return tmpStr.substring(0, tmpStr.length() - 1) + "}";
    }

}
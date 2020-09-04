package com.threads;

import com.classes.Email;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MessageChangedThread implements Runnable {

    private Email email;
    private static int index = 0;
    private static int count = 0;

    public MessageChangedThread(Email email) {
        this.email = email;
    }

    @Override
    public void run() {
        count++;
        System.err.println("Count MessageChangedThread ++ " + count);

        String flegs_json = "{" +
                "\"Deleted\":"  + email.getDeleted() + "," +
                "\"Answered\":" + email.getAnswred() + "," +
                "\"Draft\":"    + email.getDraft()   + "," +
                "\"Flagged\":"  + email.getFlagged() + "," +
                "\"Seen\":"     + email.getSeen()    +
                "}";
        String domin     = "https://my.tdfort.ru/launchers/orders_from_emails.php";
        String params    = "page=ordersapi&action=flagstate&email=" + email.getEmail_account()+ "&folder=" + email.getFolder() + "&uid="+ email.getUid()+ "&flag=" + flegs_json;
        String urlString = domin + "?" + params;

        try {
            URL url = new URL(urlString);
//            URLConnection urlConnection = url.openConnection();
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();


            urlConnection.connect();

            int code = urlConnection.getResponseCode();
            System.out.println("code: " + code);

//            Map <String, List<String>> map = urlConnection.getHeaderFields();

//            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//                String key         = entry.getKey();
//                List<String> value = entry.getValue();
//                System.err.println(key + " => " + Arrays.toString(value.toArray()));
//            }

//            System.out.println(urlConnection.getHeaderField(null));

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()
                    )
            );
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        count--;
        System.err.println("Count MessageChangedThread -- " + count);
    }

    public static int getIndex() {
        return ++index;
    }

    public static int getCount() {
        return count;
    }

}

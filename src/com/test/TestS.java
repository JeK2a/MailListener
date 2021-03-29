package com.test;

import com.service.SettingsMail;

import java.sql.*;

import static com.sun.deploy.util.ArrayUtil.arrayToString;

public class TestS {
    public static void main(String[] args) {
        String USER     = "";
        String PASSWORD = "";
        String HOST     = "93.189.150.244";
        String PORT     = "9306";
        String SCHEMA   = "";
        String URL      = "jdbc:mysql://" + HOST + ":" + PORT;

        String[] params_arr = {
                "useSSL=false",
                "useUnicode=true",
                "characterEncoding=utf-8"
        };

        Connection con = null;

        try {
            Class.forName("com.mysql.jdbc.Driver"); // MySQL 5
//            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8

            String params = arrayToString(params_arr, "&");
            System.out.println(URL + USER + PASSWORD  + "?" + params);
            con = DriverManager.getConnection(URL, USER, PASSWORD); // JDBC подключение к MySQL

            System.out.println("Test");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static String arrayToString(String[] arr, String symbol) {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            str.append(arr[i]);

            if (i != (arr.length - 1)) {
                str.append(symbol);
            }
        }

        return str.toString();
    }

}

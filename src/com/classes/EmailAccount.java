package com.classes;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EmailAccount implements Serializable {
    private volatile User user;
    private volatile ConcurrentHashMap<String, MyFolder> myFoldersMap = new ConcurrentHashMap<>();
    private volatile String status = "new";
    private volatile int event_count = 0;
    private volatile String emailAddress;
    private volatile String exception_text = "";

    private Thread thread;

    private volatile long time_status_change = new Date().getTime() / 1000;
    private volatile long time_last_event    = new Date().getTime() / 1000;

    private int count_restart_success = 0;
    private int count_restart_noop    = 0;
    private int count_restart_fail    = 0;

    private long time_reconnect = -1;
    private int thread_problem = 0;

    public EmailAccount(User user) {
        this.user         = user;
        this.emailAddress = user.getEmail();
    }

    public EmailAccount(User user, ConcurrentHashMap<String, MyFolder> myFoldersMap) {
        this.user         = user;
        this.myFoldersMap = myFoldersMap;
    }

    public EmailAccount(User user, ConcurrentHashMap<String, MyFolder> myFoldersMap, String status) {
        this.user         = user;
        this.emailAddress = user.getEmail();
        this.myFoldersMap = myFoldersMap;
        this.status       = status;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public int getCount_restart_success() {
        return count_restart_success;
    }

    public void setCount_restart_success(int count_restart_success) {
        this.count_restart_success = count_restart_success;
    }

    public int getCount_restart_noop() {
        return count_restart_noop;
    }

    public void setCount_restart_noop(int count_restart_noop) {
        this.count_restart_noop = count_restart_noop;
    }

    public int getCount_restart_fail() {
        return count_restart_fail;
    }

    public void setCount_restart_fail(int count_restart_fail) {
        this.count_restart_fail = count_restart_fail;
    }

    public long getTime_reconnect() {
        return time_reconnect;
    }

    public void setTime_reconnect(long time_reconnect) {
        this.time_reconnect = time_reconnect;
    }

    public int getThread_problem() {
        return thread_problem;
    }

    public void setThread_problem(int thread_problem) {
        this.thread_problem = thread_problem;
    }

    public void incrementCount_restart_success() {
        this.count_restart_success++;
    }

    public void incrementCount_restart_fail() {
        this.count_restart_fail++;
    }

    public void incrementCount_restart_noop() {
        this.count_restart_noop++;
    }

    public String getException_text() {
        return exception_text;
    }

    public void setException(String exception_text) {
//        this.setStatus("error");
        this.exception_text += "<br>===========================================<br>" + exception_text;

        int exception_text_length = this.exception_text.length();

        if (exception_text_length > 1500) {
            this.exception_text.substring(exception_text_length - 1400, exception_text_length);
        }
    }

    public void setException(Exception exception) {

        if (exception instanceof javax.mail.AuthenticationFailedException) {
            this.setStatus("AuthenticationFailed");
        } else {
            System.err.println(exception.getMessage());
            exception.printStackTrace();
            this.setStatus("error");
        }

        StringBuilder exception_text = new StringBuilder(exception.toString() + "<br>" + exception.getMessage() + "<br>");

        for (StackTraceElement element : exception.getStackTrace()) {
            exception_text.append("<br>").append(element.toString());
        }

        setException(exception_text.toString());
//        setException((WSSChatClient.forException(exception_text.toString())));
    }

    public void updateTime_status_change() {
        this.time_status_change = new Date().getTime() / 1000;
    }

    public void updateTime_last_event() {
        this.time_last_event = new Date().getTime() / 1000;
    }

    public long getTime_status_change() {
        return time_status_change;
    }

    public void setTime_status_change(long time_status_change) {
        this.time_status_change = time_status_change;
    }

    public long getTime_last_event() {
        return time_last_event;
    }

    public void setTime_last_event(long time_last_event) {
        this.time_last_event = time_last_event;
    }

    public ConcurrentHashMap<String, MyFolder> getFoldersMap() {
        return myFoldersMap;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ConcurrentHashMap<String, MyFolder> getMyFoldersMap() {
        return myFoldersMap;
    }

    public void setMyFoldersMap(ConcurrentHashMap<String, MyFolder> myFoldersMap) {
        this.myFoldersMap = myFoldersMap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.updateTime_status_change();
        this.updateTime_last_event();
        this.status = status;
    }

    public void addMyFolder(MyFolder myFolder) {
        this.myFoldersMap.put(myFolder.getFolder_name(), myFolder);
    }

    public int getEvent_count() {
        return event_count;
    }

    public void setEvent_count(int event_count) {
        this.event_count = event_count;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void eventCounterIncriminate() {
        this.event_count++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAccount emailAccount = (EmailAccount) o;
        return Objects.equals(user, emailAccount.user) &&
               Objects.equals(myFoldersMap, emailAccount.myFoldersMap) &&
               Objects.equals(status, emailAccount.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, myFoldersMap, status);
    }

    @Override
    public String toString() {
        return "{\"user\": "                   + user                         +
                ", \"status\": \""             + status                       +
                "\", \"error\": \""            + Base64.getEncoder().encodeToString(exception_text.getBytes()) +
//                "\", \"error\": \""            + exception_text.replace('"', '\'') +
                "\", \"myFoldersMap\": "       + getJsonFromMap(myFoldersMap) +
                ", \"count_restart_success\": "   + count_restart_success     +
                ", \"count_restart_fail\": "   + count_restart_fail           +
                ", \"count_restart_noop\": "   + count_restart_noop           +
                ", \"time_reconnect\": "        + time_reconnect +
                ", \"thread_problem\": "       + thread_problem               +
                ", \"time_status_change\": "   + time_status_change           +
                ", \"time_last_event\": "      + time_last_event              +
                "}";
    }

    private String getJsonFromMap(ConcurrentHashMap<String, MyFolder> map) {
        StringBuffer tmpStr = new StringBuffer("{ ");

        for(Map.Entry<String, MyFolder> e: map.entrySet()){
            tmpStr.append("\"").append(e.getKey()).append("\": ").append(e.getValue()).append(",");
        }

        return tmpStr.substring(0, tmpStr.length() - 1) + "}";
    }

}

package com;

import com.db.DB;
import com.threads.Mailing;
import com.threads.Suicide;

import java.util.HashMap;

public class Main {

    private static int db_i            = 0;
    public static Thread mailing_tread = null;
    public static DB db                = new DB();
    public static boolean is_restart   = true;

    public static void main(String[] args) {
        try {
            if (!db.connectToDB()) {
                System.exit(0);
            }

            Thread suicide_thread = new Thread(new Suicide());
            suicide_thread.setName("Suicide " + Suicide.getIndex());
            suicide_thread.setPriority(Thread.MAX_PRIORITY);
            suicide_thread.start();

            mailing_tread = new Thread(new Mailing());
            mailing_tread.setName("Mailing " + Mailing.getIndex());
            mailing_tread.start();

            while (true) {
                if (!suicide_thread.isAlive()) {
                    System.err.println("Suicide died");
                }

                if (
                        db_i++ < 5 &&
                        (
                            DB.result ||
                            db.connectToDB()
                        )
                ) {
                    db_i = 0;
                }

                Thread.sleep(10000);

                if (!suicide_thread.isAlive()) {
                    System.exit(0);
                }

                ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
                ThreadGroup parent;
                while ((parent = rootGroup.getParent()) != null) {
                    rootGroup = parent;
                }

//                listThreads(rootGroup, "");
//                System.out.println("=================================================================================");
//                countThreads(rootGroup, ""); // TODO on

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("<===================================================================================THE END + error===================================================================================>");
            System.exit(0);
        } finally {
            System.out.println("<===================================================================================THE END===================================================================================>");
        }
    }

    private void showThreads() {

//        for (Thread t : ThreadUtils.getAllThreads()) {
//            System.out.println(t.getName() + ", " + t.isDaemon());
//        }
    }


    // List all threads and recursively list all subgroup
    public static void countThreads(ThreadGroup group, String indent) { // TODO раскоментировать

        int nt = group.activeCount();
        Thread[] threads = new Thread[nt * 2 + 10];
        nt = group.enumerate(threads, false);

        // List every thread in the group

        HashMap<String, Integer> hashMap = new HashMap<>();

        for (int i = 0; i < nt; i++) {
            Thread thread = threads[i];

            String threadName = thread.getName();

            String regex =
                (
                    (
                        (
                           threadName.indexOf("-") < threadName.indexOf(" ") &&
                           threadName.contains("-")
                        ) ||
                        !threadName.contains(" ")
                    ) ?
                    "-" :
                    " "
                );

            String[] arr_srt = threadName.split(regex);

            if (arr_srt.length > 1) {
                threadName = arr_srt[0];
            }

            hashMap.compute(threadName, (key, value) -> (value == null) ? 1 : value + 1);
        }

        hashMap.forEach((key, value) -> System.out.println("    Threads[" + key + " count " + value + "]"));

        // Recursively list all subgroups
        int ng = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[ng * 2 + 10];
        ng = group.enumerate(groups, false);

        for (int i = 0; i < ng; i++) {
            countThreads(groups[i], indent + "  ");
        }
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep() {
        sleep(500);
    }

}
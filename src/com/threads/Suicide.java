package com.threads;

import com.wss.WSSChatClient;

public class Suicide implements Runnable {

    public static long timer = 0;
    private static int index = 0;

    public static int getIndex() {
        return ++index;
    }

    @Override
    public void run() {

//        long time_limit = 60 * 60 * 24; // TODO убрать ограничение по времени

        WSSChatClient wssChatClient = new WSSChatClient();
        wssChatClient.connectToWSS();

        while (true) {
            System.out.print(" timer " + timer++ + " (" + Thread.activeCount() + ") ");

//            System.out.print(" (" + Thread.activeCount() + "," + ManagementFactory.getThreadMXBean().getThreadCount() + "," + ManagementFactory.getRuntimeMXBean().getName() + "," + ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount() + ") ");
//            System.out.println();
//            System.out.println(ManagementFactory.getClassLoadingMXBean().getLoadedClassCount() + "/" + ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount() + "/"+ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount());
//            System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
//            System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024);

//            if (timer++ > time_limit || Thread.activeCount() > 4000) {
            if (Thread.activeCount() > 4000) {
                System.out.println("=================================================Suicide=================================================");
                System.exit(0);
            }

            if (timer % 15 == 0) {
                wssChatClient.connectToWSS();
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

package com;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TestGroup {
    public static void main(String[] args) {

        int[] uids_tmp = {
                2
        };

        int  limit      = 70;
        int  uids_count = uids_tmp.length;
        long uid_start  = uids_tmp[0];
        long uid_end    = uids_tmp[0];
        ArrayList<Long> uids_list = new ArrayList<>();

        System.out.println(Arrays.toString(uids_tmp));

        for (int i = 0; uids_count > i; i++) {
            if (
                uids_count == i + 1 ||
                ((uids_tmp[i] + 1) != uids_tmp[i + 1])
            ) {
                if (uid_start == uid_end) {
                    uids_list.add(uid_start);

                    if (uids_list.size() == limit) {
                        System.out.println(uids_list.size());
                        System.out.println(Arrays.toString(uids_list.toArray()));
                        uids_list.clear();
                    }
                } else {
//                    System.out.println(uid_start + " - " + uid_end);
                }

                if (uids_count > i + 1) {
                    uid_start = uids_tmp[i + 1];
                }
            }

            if (uids_count > i + 1) {
                uid_end = uids_tmp[i + 1];
            }
        }

        System.out.println(uids_list.size());
        System.out.println(Arrays.toString(uids_list.toArray()));
        System.out.println(uids_list.toArray().getClass());

    }
}

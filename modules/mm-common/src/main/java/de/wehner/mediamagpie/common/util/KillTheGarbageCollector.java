package de.wehner.mediamagpie.common.util;

import java.util.ArrayList;
import java.util.List;

// TODO rwe: just a testclass
public class KillTheGarbageCollector {
    private List<String> outerStore = new ArrayList<String>(1000000);
    private List<String> oldStore = new ArrayList<String>(1000000);
    private static int nThread = 3;

    public static void main(String[] args) {

        for (int i = 0; i < nThread; ++i) {
            new Thread() {
                public void run() {
                    new KillTheGarbageCollector().go();
                }
            }.start();
        }
    }

    private void go() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000; ++i) {
            if (i % 100 == 0) {
                doOld();
                time = System.currentTimeMillis(); // reset the clock
            }
            for (int j = 0; j < 1000000 / nThread; ++j) {
                outerStore.add(((Integer) j).toString());
            }
            outerStore.clear();
            long t = System.currentTimeMillis();
            System.out.println("" + (t - time));
            time = t;
        }
    }

    private void doOld() {
        oldStore.clear();
        for (int j = 0; j < 10000000 / nThread; ++j) {
            oldStore.add(((Integer) j).toString());
        }
    }

}

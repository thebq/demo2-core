package vn.vnpay;

import java.util.Timer;
import java.util.TimerTask;

public class CronJob {
    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask task = new MyTask();
        timer.schedule(task, 0,18000);
    }
}

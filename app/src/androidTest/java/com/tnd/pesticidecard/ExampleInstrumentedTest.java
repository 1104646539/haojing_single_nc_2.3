package com.tnd.multifuction1;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.tnd.multifuction", appContext.getPackageName());
    }

    @Test
    public void threadTest() {

        final CountDownLatch latch= new CountDownLatch(50);//使用java并发库concurrent
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    test1();
                    latch.countDown();
                }
            });
            list.add(t);
        }

        for (int i = 0; i < list.size(); i++) {
            list.get(i).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void test1() {

        System.out.println(Thread.currentThread().getId() + "线程开始");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getId() + "线程结束");
    }

    CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void CountDownLatchTest() {


        Thread t1 = new Thread(){
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("我是第1个线程...");
            }
        };
        Thread t2 = new Thread(){
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("我是第2个线程...");
            }
        };
        Thread t3 = new Thread(){
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("我是第3个线程...");
            }
        };

        t1.start();
        SystemClock.sleep(3000);
        latch.countDown();

        latch = new CountDownLatch(1);
        t2.start();
        SystemClock.sleep(3000);
        latch.countDown();

        latch = new CountDownLatch(1);
        t3.start();
        SystemClock.sleep(3000);
        //latch.countDown();
    }
}

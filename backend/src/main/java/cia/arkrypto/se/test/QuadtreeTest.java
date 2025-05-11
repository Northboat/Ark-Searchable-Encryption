package cia.arkrypto.se.test;

import cia.arkrypto.se.crypto.EncryptedQuadtree;
import cia.arkrypto.se.ds.Location;
import cia.arkrypto.se.ds.QuadTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuadtreeTest {
    static int n = 1;
    static long s1, e1, s2, e2, s3, e3;
    public static void main(String[] args) {

        EncryptedQuadtree tree = new EncryptedQuadtree();

        genPoint();
        QuadTree root = tree.build(O);


        bfs(root);
        System.out.println((e1-s1+e2-s2+e3-s3)/n + " ms");
    }

    public static void threadTest(EncryptedQuadtree tree){
        Object lock1 = new Object();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                s1 = System.currentTimeMillis();
                for(int i = 0; i < n; i++){
                    tree.build(O);
                }
                e1 = System.currentTimeMillis();
                synchronized (lock1) {//获取对象锁
                    lock1.notify();//子线程唤醒
                }
            }
        });

        thread1.start();

        try {
            synchronized (lock1) {//这里也是一样
                lock1.wait();//主线程等待
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void printTree(QuadTree t){
        if(t != null) {
            System.out.print(t.getP() + "\tX: ");
            System.out.print(Arrays.toString(t.getX()) + "\ts_x: ");
            System.out.print(t.getS_x() + "\tt_x: ");
            System.out.print(t.getT_x());
            System.out.println();
        }
    }

    public static void bfs(QuadTree root){
        List<QuadTree> list = new ArrayList<>();
        if(root != null){
            list.add(root);
        }

        while(!list.isEmpty()){
            QuadTree cur = list.get(0);
            printTree(cur);
            for(int i = 0; i < 4; i++){
                if(cur.getSubtree()[i] != null){
                    list.add(cur.getSubtree()[i]);
                }
            }
            list.remove(0);
        }
    }


    static Location[] O;
    public static void genPoint() {
        double centerX = 0; // 圆心的横坐标
        double centerY = 0; // 圆心的纵坐标
        double radius = 900; // 圆半径
        int numPoints = 100; // 生成的点数

        Random random = new Random();
        O = new Location[numPoints];
        double[] x = new double[numPoints];
        double[] y = new double[numPoints];

        // 生成圆内的点
        for (int i = numPoints - 1; i >= 0; i--) {
            x[i] = centerX + random.nextDouble() * radius;
            y[i] = centerY + random.nextDouble() * radius;
            O[i] = new Location((int)x[i], (int)y[i]);
        }
        // 输出点坐标
        for (int i = 0; i < numPoints; i++) {
            System.out.println("["+x[i]+","+y[i]+"]");
        }

        for(Location o: O){
            System.out.println(o);
        }
    }
}

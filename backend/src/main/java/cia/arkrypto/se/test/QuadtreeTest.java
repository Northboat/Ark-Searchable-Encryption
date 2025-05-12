package cia.arkrypto.se.test;

import cia.arkrypto.se.crypto.EncryptedQuadtree;
import cia.arkrypto.se.ds.Ciphertext;
import cia.arkrypto.se.ds.Point;
import cia.arkrypto.se.ds.QuadtreeNode;
import cia.arkrypto.se.util.HashUtil;
import cia.arkrypto.se.util.IPFEUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.security.core.parameters.P;

import java.util.*;

public class QuadtreeTest {

    public static Field G1, Zr;
    // 这里的 n 是字符串被映射的数组长度，这个和前缀是很相关的，决定了前缀的长度上限
    private static final int l;

    static{
        Pairing bp = PairingFactory.getPairing("a.properties");
        G1 = bp.getG1();
        Zr = bp.getZr();
        l = 26;
    }

    // 搜索匹配不上，很难受
    public static void main(String[] args) {

        EncryptedQuadtree tree = new EncryptedQuadtree(G1, Zr, l);

//        tree.build(10);
//        System.out.println(tree.getTreeStruct(1));
//        System.out.println(tree.getTreeStruct(1));

        tree.test();

        IPFETest(tree);
    }


    public static void IPFETest(EncryptedQuadtree tree){
        Point p = new Point(1, 1);
        String str = "101010";
        QuadtreeNode t = tree.creatNode(str, p);

        String q = "101010";
        Ciphertext c = tree.getCiphertext(p);

        System.out.println(t);
        System.out.println(c);

        Element res = IPFEUtil.decrypt(tree.getG1(), t, c);
        System.out.println(res);
    }


    public static long[] threadTest(Point[] O, EncryptedQuadtree ... trees){
        int n = trees.length;
        Object[] locks = new Object[n];
        Thread[] threads = new Thread[n];
        long[] s = new long[n], e = new long[n];


        for(int i = 0; i < n; i++){
            locks[i] = new Object();
            int finalI = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    s[finalI] = System.currentTimeMillis();
                    for (EncryptedQuadtree tree : trees) {
                        tree.build(O);
                    }
                    e[finalI] = System.currentTimeMillis();
                    synchronized (locks[finalI]) {//获取对象锁
                        locks[finalI].notify();//子线程唤醒
                    }
                }
            });
        }

        for(Thread thread: threads){
            thread.start();
        }

        try {
            for(Object lock: locks){
                synchronized (lock) {//这里也是一样
                    lock.wait();//主线程等待
                }
            }
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        long[] timespan = new long[n];
        for(int i = 0; i < n; i++){
            timespan[i] = e[i]-s[i];
        }
        return timespan;
    }
}

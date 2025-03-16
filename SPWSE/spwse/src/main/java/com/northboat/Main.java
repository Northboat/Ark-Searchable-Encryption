package com.northboat;

import com.northboat.se.CipherSystem;
import com.northboat.se.impl.SPWSEOne;
import com.northboat.se.impl.SPWSETwo;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Main {

    private static final Pairing bp = PairingFactory.getPairing("a160.properties");
    // 加密单词长度，为 2n
    private static final int n;
    // 主公钥
    public static Field G1, G2, GT, Zr;
    public static Element g1, g2;

    // 初始化生成元
    static{
        System.out.println("系统参数初始化\n=====================");
        G1 = bp.getG1();
        G2 = bp.getG2();
        GT = bp.getGT();
        Zr = bp.getZr();
        n = 20;
        g1 = G1.newRandomElement().getImmutable();
        g2 = G2.newRandomElement().getImmutable();
    }


    public static void main(String[] args) {

        int m = 1; // 循环次数
        String w = "wouldwouldwouldwould"; // 关键词
        String t = "wouldwo**dwouldwould"; // 搜索字

        CipherSystem spwseOne = new SPWSEOne(G1, GT, Zr, bp, n, g1, g2);
        CipherSystem spwseTwo = new SPWSETwo(G1, GT, Zr, bp, n, g1);

        test(spwseOne, w, t, m);
        test(spwseTwo, w, t, m);
    }


    public static void test(CipherSystem cipherSystem, String w, String t, int m){
        cipherSystem.setup();
        cipherSystem.keygen();

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++){
            cipherSystem.enc(w);
        }
        long e1 = System.currentTimeMillis();



        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++){
            cipherSystem.trap(t);
        }
        long e2 = System.currentTimeMillis();


        boolean flag = false;
        long s3 = System.currentTimeMillis();
        for(int i = 0; i < m; i++){
            flag = cipherSystem.search();
        }
        long e3 = System.currentTimeMillis();

        System.out.println("算法 " + cipherSystem.getClass() + " 对 " + w + " 和 " + t + " 的测试\n==========================");
        System.out.println("验证结果: " + flag);
        System.out.println("加密 " + w + " 时长: " + (double)(e1 - s1)/m + "ms");

        System.out.println("计算 " + t + " 陷门时长: " + (double)(e2 - s2)/m + "ms");
        System.out.println("匹配时长: " + (double)(e3 - s3)/m + "ms\n==========================\n\n");
    }

}

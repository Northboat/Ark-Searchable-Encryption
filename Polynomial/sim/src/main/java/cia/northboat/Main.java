package cia.northboat;

import cia.northboat.sim.Gu2CKS;
import cia.northboat.sim.TBEKS;
import cia.northboat.sim.TMS;
import cia.northboat.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final Pairing bp = PairingFactory.getPairing("a.properties");
    public static Field G1, G2, GT, Zr;
    public static Element g1, g2, h, y;

    public static long[][] time;
    public static void init() {
        G1 = bp.getG1();
        G2 = bp.getG2();
        GT = bp.getGT();
        Zr = bp.getZr();
        g1 = G1.newRandomElement().getImmutable();
        g2 = G2.newRandomElement().getImmutable();
        h = G1.newRandomElement().getImmutable();
        y = Zr.newRandomElement().getImmutable();
        time = new long[4][4];
    }

    static String[] words = {"word", "chinese", "english", "security", "science", "information", "northboat"};
    public static List<String> getWords(int i){
        return new ArrayList<>(Arrays.asList(words).subList(0, i));
    }

    public static void main(String[] args) {
        init();
        test(1);
    }

    public static void test(int m){
        List<String> words = getWords(4);
        testOp("word", 26, m);
        testTMS(m, words);
        testTBEKS(m, words);
        testGu2CKS(m, words);
        printTime(m);
    }

    public static void testOp(String str, int n, int m){
        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            bp.pairing(g1, g2).getImmutable();
        long e1 = System.currentTimeMillis();

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            g1.powZn(y).getImmutable();
        long e2 = System.currentTimeMillis();

        long s3 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            HashUtil.hashStr2ZrArr(Zr, str, n);
        long e3 = System.currentTimeMillis();

        time[0][0] += e1-s1;
        time[0][1] += e2-s2;
        time[0][2] += e3-s3;
    }

    public static void printTime(int n){
        System.out.println("\n========== 耗时(ms) ==========");
        for(long[] t: time){
            for(long i: t){
                System.out.print(i/n + "\t\t");
            }
            System.out.println();
        }
    }

    public static void testTMS(int m, List<String> words){
        TMS.init(G1, Zr, g1, bp);

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            TMS.keyGen(4);
        long e1 = System.currentTimeMillis();

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            TMS.enc(words);
        long e2 = System.currentTimeMillis();

        long s3 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            TMS.trap(words);
        long e3 = System.currentTimeMillis();

        long s4 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            System.out.println(TMS.search(words));
        long e4 = System.currentTimeMillis();

        time[1][0] += e1-s1;
        time[1][1] += e2-s2;
        time[1][2] += e3-s3;
        time[1][3] += e4-s4;
    }


    public static void testTBEKS(int m, List<String> words){
        TBEKS.init(G1, Zr, bp, g1, h);

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            TBEKS.keyGen(3);
        long e1 = System.currentTimeMillis();

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            TBEKS.enc(words);
        long e2 = System.currentTimeMillis();

        long s3 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            TBEKS.trap(words);
        long e3 = System.currentTimeMillis();

        long s4 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            System.out.println(TBEKS.search());
        long e4 = System.currentTimeMillis();

        time[2][0] += e1-s1;
        time[2][1] += e2-s2;
        time[2][2] += e3-s3;
        time[2][3] += e4-s4;
    }


    public static void testGu2CKS(int m, List<String> words){
        Gu2CKS.init(G1, Zr, bp, g1);

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            Gu2CKS.keyGen(3);
        long e1 = System.currentTimeMillis();

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            Gu2CKS.enc(words);
        long e2 = System.currentTimeMillis();

        long s3 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            Gu2CKS.trap(words);
        long e3 = System.currentTimeMillis();

        long s4 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            System.out.println(Gu2CKS.search());
        long e4 = System.currentTimeMillis();

        time[3][0] += e1-s1;
        time[3][1] += e2-s2;
        time[3][2] += e3-s3;
        time[3][3] += e4-s4;
    }

}
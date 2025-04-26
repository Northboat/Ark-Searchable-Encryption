package cia.northboat;

import cia.northboat.se.CipherSystem;
import cia.northboat.se.impl.*;
import cia.northboat.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final Pairing bp;
    // 加密单词长度，为 2n
    private static final int n = 8;
    // 主公钥
    public static Field G1, G2, GT, Zr;

    public static List<List<Long>> times;

    static{
        bp = PairingFactory.getPairing("a.properties");
        G1 = bp.getG1();
        G2 = bp.getG2();
        GT = bp.getGT();
        Zr = bp.getZr();
        times = new ArrayList<>();
    }

    public static void printTime(){
        System.out.println("======== Time Cost ========");
        for(List<Long> t: times){
            for(long i: t){
                System.out.print(i + "\t\t\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int m = 1, k = 3, q = 1024;
        String w = "hello world";

        CipherSystem pauks = new PAUKS(G1, GT, Zr, bp, n);
//        CipherSystem saPauks = new SAPAUKS(G1, GT, Zr, bp, n);
//        CipherSystem dIBaeks = new DIBAEKS(G1, GT, Zr, bp, n);
//        CipherSystem pMatch = new PMatch(G1, GT, Zr, bp, n);
//        CipherSystem crIma = new CRIMA(G1, GT, Zr, bp, n);
//        CipherSystem tu2Cks = new Tu2CKS(G1, GT, Zr, bp, n ,k);
//        CipherSystem tuCr = new TuCR(G1, GT, Zr, bp, n);
//        CipherSystem duMse = new DuMSE(G1, GT, Zr, bp, n, q);
        CipherSystem paeks = new PAEKS(G1, GT, Zr, bp, n);

        test(pauks, w, null, m);
//        test(saPauks, w, null, m);
//        test(dIBaeks, w, null, m);
//        test(pMatch, w, null, m);
//        test(crIma, w, null, m);
//        test(tu2Cks, w, null, m);
//        test(tuCr, w, null, m);
//        test(duMse, w, null, m);
        test(paeks, w, null, m);

        printTime();
    }


    public static void test(CipherSystem cipherSystem, String word, List<String> words, int m){
        System.out.println(cipherSystem.getClass() + " test:");

        cipherSystem.setup();
        cipherSystem.keygen();

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++) {
            try {
                cipherSystem.enc(word);
            } catch (UnsupportedOperationException e) {
                cipherSystem.enc(words);
            }
        }
        long e1 = System.currentTimeMillis();
        long t1 = e1-s1;

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++){
            try{
                cipherSystem.trap(word);
            }catch (UnsupportedOperationException e){
                cipherSystem.trap(words);
            }
        }
        long e2 = System.currentTimeMillis();
        long t2 = e2-s2;

        long s3 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            System.out.println(cipherSystem.search());
        long e3 = System.currentTimeMillis();
        long t3 = e3-s3;

        if(cipherSystem.getUpdatable()){
            cipherSystem.updateKey();
            long s4 = System.currentTimeMillis();
            for(int i = 0; i < m; i++)
                cipherSystem.updateEnc();
            long e4 = System.currentTimeMillis();
            t1 += e4-s4;


            long s5 = System.currentTimeMillis();
            for(int i = 0; i < m; i++)
                cipherSystem.constTrap(word);
            long e5 = System.currentTimeMillis();
            t2 += e5-s5;

            long s6 = System.currentTimeMillis();
            for(int i = 0; i < m; i++)
                System.out.println(cipherSystem.updateSearch());
            long e6 = System.currentTimeMillis();
            t3 += e6-s6;
        }

        times.add(Arrays.asList(t1/m, t2/m, t3/m));

        System.out.println(cipherSystem.getClass() + " test finished!\n");
    }

}
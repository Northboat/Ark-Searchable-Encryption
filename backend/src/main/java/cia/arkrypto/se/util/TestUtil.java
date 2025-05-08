package cia.arkrypto.se.util;

import cia.arkrypto.se.crypto.CipherSystem;
import cia.arkrypto.se.crypto.impl.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestUtil {
    public static void singleThreadOneWordTest(Field G1, Field G2, Field GT, Field Zr, Pairing bp, int n){
        int m = 1, k = 3, q = 1024;
        String w = "hello world";

        CipherSystem pauks = new PAUKS(G1, GT, Zr, bp, n);
        CipherSystem saPauks = new SAPAUKS(G1, GT, Zr, bp, n);
        CipherSystem dIBaeks = new DIBAEKS(G1, GT, Zr, bp, n);
        CipherSystem pMatch = new PMatch(G1, GT, Zr, bp, n);
        CipherSystem crIma = new CRIMA(G1, GT, Zr, bp, n);
        CipherSystem tu2Cks = new Tu2CKS(G1, GT, Zr, bp, n ,k);
        CipherSystem tuCr = new TuCR(G1, GT, Zr, bp, n);
        CipherSystem duMse = new DuMSE(G1, GT, Zr, bp, n, q);
        CipherSystem paeks = new PAEKS(G1, GT, Zr, bp, n);
        CipherSystem spwseOne = new SPWSEOne(G1, GT, Zr, bp, n, G2.newRandomElement().getImmutable());
        CipherSystem spwseTwo = new SPWSETwo(G1, GT, Zr, bp, n);
        CipherSystem peks = new PEKS(G1, GT, Zr, bp, n);
        CipherSystem dpreks = new DPREKS(G1, GT, Zr, bp, n);
        CipherSystem preks = new PREKS(G1, GT, Zr, bp, n);
        CipherSystem fipeck = new FIPECK(G1, GT, Zr, bp, n);


        test(pauks, w, null, m);
        test(saPauks, w, null, m);

        test(crIma, w, null, m);
        test(dIBaeks, w, null, m);
        test(dpreks, w, null, m);
        test(duMse, w, null, m);

        test(fipeck, w, null, m);

        test(pMatch, w, null, m);

        test(tu2Cks, w, null, m);
        test(tuCr, w, null, m);

        test(paeks, w, null, m);
        test(spwseOne, w, null, m);
        test(spwseTwo, w, null, m);
        test(peks, w, null, m);

        test(preks, w, null, m);

        printTime();
    }


    public static void singleThreadMultiWordsTest(Field G1, Field G2, Field GT, Field Zr, Pairing bp, int n) {
        int m = 1, k = 4;
        String file = "word/2.txt";
        List<String> words = FileUtil.readFileToList(file);

        CipherSystem tms = new TMS(G1, GT, Zr, bp, n, k);
        CipherSystem tbeks = new TBEKS(G1, GT, Zr, bp, n, k);
        CipherSystem gu2cks = new Gu2CKS(G1, GT, Zr, bp, n, k);

        test(tms, "", words, m);
        test(tbeks, "", words, m);
        test(gu2cks, "", words, m);

        printTime();
    }


    public static void multiThreadMultiWordsTest(Field G1, Field G2, Field GT, Field Zr, Pairing bp, int n) {
        int round = 1, sender = 1, receiver = 1;

        String file = "word/2.txt";
        List<String> words = FileUtil.readFileToList(file);

        CipherSystem ap = new AP(G1, GT, Zr, bp, n, G2);
        CipherSystem scf = new SCF(G1, GT, Zr, bp, n);
        CipherSystem pecks = new PECKS(G1, GT, Zr, bp, n);

        List<CipherSystem> cipherSystems = new ArrayList<>();
        cipherSystems.add(scf);
        cipherSystems.add(ap);
        cipherSystems.add(pecks);

        executorServiceTest(cipherSystems, words, sender, receiver, round);

        System.out.println("The Time Cost Log in ./time.log");
    }


    public static List<List<Long>> times = new ArrayList<>();

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
                cipherSystem.reEnc();
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

    public static void executorServiceTest(List<CipherSystem> cipherSystems, List<String> words,
                                           int sender, int receiver, int round){
        int n = cipherSystems.size();

        System.out.println("Thread Pool Start, " + n + " Threads in Total");

        // 需要测试的算法数量
        List<List<Long>> times = new ArrayList<>(n);
        for (CipherSystem system : cipherSystems) {
            System.out.println(system.getClass() + "Test");
            times.add(new ArrayList<>());
        }
        ExecutorService executor = Executors.newFixedThreadPool(n);
        List<Future<List<Long>>> futures = new ArrayList<>();
        // 提交任务
        for(CipherSystem cipherSystem: cipherSystems){
            futures.add(executor.submit(() -> cipherSystem.test(words, sender, receiver, round)));
        }

        // 获取结果
        try {
            // 这一步是阻塞的，用 set 保证各算法先后次序是我所希望的
            for(int i = 0; i < n; i++){
                Future<List<Long>> future = futures.get(i);
                times.set(i, future.get());
            }
            // 记录结果
            logTime(times);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池
            executor.shutdown();
            System.out.println("Thread Pool Shutdown");
        }
    }

    public static void printTime(){
        System.out.println("=== Time Cost ===");
        for(List<Long> t: times){
            for(long i: t){
                System.out.print(i + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void logTime(List<List<Long>> times){
        FileUtil.writeCostToLog("============= Time Cost ============\n");
        for(List<Long> t: times){
            for(int i = 0; i < t.size(); i++){
                if(i != 0){
                    FileUtil.writeCostToLog("\t" + t.get(i));
                    continue;
                }
                FileUtil.writeCostToLog(t.get(i) + "");
            }
            FileUtil.writeCostToLog("\n");
        }
        FileUtil.writeCostToLog("\n\n");
    }


    public static void testMem(Field G1, Field G2, Field GT, Field Zr){
        Element g1 = G1.newRandomElement();
        Element g2 = G2.newRandomElement();
        Element gt = GT.newRandomElement();
        Element zr = Zr.newRandomElement();

        int a = 100, b = 100, n = 2, m = 2, lambda = 32;
        int g1Length = g1.toBytes().length, g2Length = g2.toBytes().length, gtLength = gt.toBytes().length, zrLength = zr.toBytes().length;

        int AP1, AP2, SCF, PECKS;
        for(int i = 0; i < 7; i++){

            // 密钥传输 1
//            AP1 = a*(G1+G2+ZR);
//            SCF = a*(G1+ZR);
//            PECKS = a*(3*G1+ZR);
            // 密钥传输 2
//            AP1 = b*(G2+ZR);
//            SCF = b*(G1+ZR);
//            PECKS = b*(3*G1+ZR);


            // 密文传输
//            AP1 = a*(GT+(n+3)*G2+G1);
//            AP2 = (a*b-a)*(3*GT+(n+3)*G2+2*G1);
//            SCF = (a*b*n)*(9*lambda);
//            PECKS = a*((n+3)*G1);

            // 陷门传输
            AP1 = b*((n+3)*g1Length+zrLength);
            AP2 = (a*b-b)*((n+3)*g1Length+zrLength);
            SCF = (a*b*m)*g1Length;
            PECKS = b*((n+4)*g1Length);



            System.out.println(AP1);
            System.out.println(AP2);
            System.out.println(SCF);
            System.out.println(PECKS);
            System.out.println();

//            a += 50;
            b += 50;
//            m += 100;
//            n += 100;
        }
    }

}

package cia.arkrypto.se.util;

import cia.arkrypto.se.crypto.CipherSystem;
import cia.arkrypto.se.crypto.impl.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestUtil {

    public static void singleThreadTest(Field G1, Field G2, Field GT, Field Zr, Pairing bp, int n){
        int m = 1, k = 3, q = 1024;
        String w = "hello world";
        String file = "test_data/word/2.txt";
        List<String> words = FileUtil.readFileToList(file);

        CipherSystem pauks = new PAUKS(G1, GT, Zr, bp, n);
        CipherSystem sapauks = new SAPAUKS(G1, GT, Zr, bp, n);
        CipherSystem dibaeks = new DIBAEKS(G1, GT, Zr, bp, n);
        CipherSystem pmatch = new PMatch(G1, GT, Zr, bp, n);
        CipherSystem crima = new CRIMA(G1, GT, Zr, bp, n);
        CipherSystem tu2cks = new Tu2CKS(G1, GT, Zr, bp, n ,k);
        CipherSystem tucr = new TuCR(G1, GT, Zr, bp, n);
        CipherSystem dumse = new DuMSE(G1, GT, Zr, bp, n, q);
        CipherSystem paeks = new PAEKS(G1, GT, Zr, bp, n);
        CipherSystem spwse1 = new SPWSE1(G1, GT, Zr, bp, n, G2);
        CipherSystem spwse2 = new SPWSE2(G1, GT, Zr, bp, n);
        CipherSystem peks = new PEKS(G1, GT, Zr, bp, n);
        CipherSystem dpreks = new DPREKS(G1, GT, Zr, bp, n);
        CipherSystem preks = new PREKS(G1, GT, Zr, bp, n);
        CipherSystem fipeck = new FIPECK(G1, GT, Zr, bp, n);




        test(pauks, w, null, m);
        test(sapauks, w, null, m);

        test(crima, w, null, m);
        test(dibaeks, w, null, m);
        test(dpreks, w, null, m);
        test(dumse, w, null, m);

        test(fipeck, w, null, m);

        test(pmatch, w, null, m);

        test(tu2cks, w, null, m);
        test(tucr, w, null, m);

        test(paeks, w, null, m);
        test(spwse1, w, null, m);
        test(spwse2, w, null, m);
        test(peks, w, null, m);

        test(preks, w, null, m);


        int l = 4;
        CipherSystem tms = new TMS(G1, GT, Zr, bp, n, l);
        CipherSystem tbeks = new TBEKS(G1, GT, Zr, bp, n, l);
        CipherSystem gu2cks = new Gu2CKS(G1, GT, Zr, bp, n, l);
        test(tms, "", words, m);
        test(tbeks, "", words, m);
        test(gu2cks, "", words, m);

        printTime();
    }



    public static void multiThreadTest(Field G1, Field G2, Field GT, Field Zr, Pairing bp, int n) {
        int round = 1, sender = 1, receiver = 1;

        String file = "test_data/word/2.txt";
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

        Map<String, Object> res = cipherSystem.test(word, words, m);

        long encCost = (long) res.get("EncCost");
        long trapCost = (long) res.get("TrapCost");
        long searchCost = (long) res.get("SearchCost");

        if(cipherSystem.getUpdatable()){
            encCost += (long) res.get("ReEncCost");
            trapCost += (long) res.get("ConstTrapCost");
            searchCost += (long) res.get("UpdateSearchCost");
        }
        times.add(Arrays.asList(encCost, trapCost, searchCost));

        System.out.println(cipherSystem.getClass() + " test finished!\n");
    }


    // 简单的多线程测试，数据量太大了，应该能快点
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

package cia.northboat;

import cia.northboat.se.CipherSystem;
import cia.northboat.se.impl.AP;
import cia.northboat.se.impl.FIPECK;
import cia.northboat.se.impl.PECKS;
import cia.northboat.se.impl.SCF;
import cia.northboat.util.FileUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static Field G1, G2, GT, Zr;
    private static final Pairing bp;
    // 加密单词长度，为 2n
    private static final int n;

    static{
        bp = PairingFactory.getPairing("a.properties");
        G1 = bp.getG1();
        G2 = bp.getG2();
        GT = bp.getGT();
        Zr = bp.getZr();
        n = 12;
    }

    public static void main(String[] args) {

        int round = 1, sender = 100, receiver = 100;

        String file = "200.txt";
        List<String> words = FileUtil.readFileToList(file);

        CipherSystem scf = new SCF(G1, GT, Zr, bp, n);
        CipherSystem ap = new AP(G1, GT, Zr, bp, n, G2);
        CipherSystem pecks = new PECKS(G1, GT, Zr, bp, n);

        List<CipherSystem> cipherSystems = new ArrayList<>();
        cipherSystems.add(scf);
        cipherSystems.add(ap);
        cipherSystems.add(pecks);

        executorServiceTest(cipherSystems, words, sender, receiver, round);
        testMem();

    }


    public static void testMem(){
        Element g1 = G1.newRandomElement();
        Element g2 = G2.newRandomElement();
        Element gt = GT.newRandomElement();
        Element zr = Zr.newRandomElement();

        int a = 100, b = 100, n = 2, m = 2, lambda = 32;
        int G1 = g1.toBytes().length, G2 = g2.toBytes().length, GT = gt.toBytes().length, ZR = zr.toBytes().length;

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
            AP1 = b*((n+3)*G1+ZR);
            AP2 = (a*b-b)*((n+3)*G1+ZR);
            SCF = (a*b*m)*G1;
            PECKS = b*((n+4)*G1);



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



    public static void executorServiceTest(List<CipherSystem> cipherSystems, List<String> words,
                            int sender, int receiver, int round){
        int n = cipherSystems.size();

        // 需要测试的算法数量
        List<List<Long>> times = new ArrayList<>(n);
        for(int i = 0; i < n; i++){
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
        }
    }

    public static void logTime(List<List<Long>> times){
        FileUtil.writeCostToLog("================= Time Cost =================\n");
        for(List<Long> t: times){
            for(int i = 0; i < t.size(); i++){
                if(i != 0){
                    FileUtil.writeCostToLog("\t\t\t" + t.get(i));
                    continue;
                }
                FileUtil.writeCostToLog(t.get(i) + "");
            }
            FileUtil.writeCostToLog("\n");
        }
        FileUtil.writeCostToLog("\n\n");
    }

}

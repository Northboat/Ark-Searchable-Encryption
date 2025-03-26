package cia.northboat;

import cia.northboat.se.CipherSystem;
import cia.northboat.se.impl.AP;
import cia.northboat.se.impl.FIPECK;
import cia.northboat.se.impl.PECKS;
import cia.northboat.se.impl.SCF;
import cia.northboat.util.FileUtil;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

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

        String file = "2.txt";
        List<String> words = FileUtil.readFileToList(file);

        CipherSystem scf = new SCF(G1, GT, Zr, bp, n);
        CipherSystem ap = new AP(G1, GT, Zr, bp, n, G2);
        CipherSystem pecks = new PECKS(G1, GT, Zr, bp, n);

        List<CipherSystem> cipherSystems = new ArrayList<>();
        cipherSystems.add(scf);
        cipherSystems.add(ap);
        cipherSystems.add(pecks);


        for(int i = 1; i <= 7; i++){
//            executorServiceTest(cipherSystems, words, sender+i*50, receiver, round);
            executorServiceTest(cipherSystems, words, sender, receiver+i*50, round);
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
            for(long i: t){
                FileUtil.writeCostToLog(i + "\t\t\t");
            }
            FileUtil.writeCostToLog("\n");
        }
        FileUtil.writeCostToLog("\n\n");
    }

}

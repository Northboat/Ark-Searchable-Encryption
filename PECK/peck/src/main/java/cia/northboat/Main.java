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
    // 主公钥

    public static List<List<Long>> times;

    static{
        bp = PairingFactory.getPairing("a.properties");
        G1 = bp.getG1();
        G2 = bp.getG2();
        GT = bp.getGT();
        Zr = bp.getZr();
        n = 12;

        // 需要测试的算法数量
        int k = 3;
        times = new ArrayList<>();
        for(int i = 0; i < k; i++){
            times.add(new ArrayList<>());
        }
    }

    public static void printTime(){
        System.out.println("================= Time Cost =================");
        for(List<Long> t: times){
            for(long i: t){
                System.out.print(i + "\t\t\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        int round = 1, sender = 10, receiver = 10;

        String file = "300.txt";
        List<String> words = FileUtil.readFileToList(file);

//        CipherSystem fipeck = new FIPECK(G1, GT, Zr, bp, n);
        CipherSystem scf = new SCF(G1, GT, Zr, bp, n);
        CipherSystem ap = new AP(G1, GT, Zr, bp, n, G2);
        CipherSystem pecks = new PECKS(G1, GT, Zr, bp, n);

        List<CipherSystem> cipherSystems = new ArrayList<>();
        cipherSystems.add(scf);
        cipherSystems.add(ap);
        cipherSystems.add(pecks);

        executorServiceTest(cipherSystems, words, sender, receiver, round);
    }


    public static void executorServiceTest(List<CipherSystem> cipherSystems, List<String> words,
                            int sender, int receiver, int round){

        ExecutorService executor = Executors.newFixedThreadPool(cipherSystems.size());
        List<Future<List<Long>>> futures = new ArrayList<>();
        // 提交任务
        for(CipherSystem cipherSystem: cipherSystems){
            futures.add(executor.submit(() -> cipherSystem.test(words, sender, receiver, round)));
        }

        // 获取结果
        try {
            // 这一步是阻塞的，不用 add 而用 set 是因为有可能先后次序不是我所希望的
            for(int i = 0; i < futures.size(); i++){
                Future<List<Long>> future = futures.get(i);
                times.set(i, future.get());
            }
            // 打印结果
            printTime();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池
            executor.shutdown();
        }
    }

}

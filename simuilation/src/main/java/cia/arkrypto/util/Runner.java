package cia.arkrypto.util;

import cia.arkrypto.se.CipherSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Runner {

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
            System.out.println("Thread Pool Shutdown\n");
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


}

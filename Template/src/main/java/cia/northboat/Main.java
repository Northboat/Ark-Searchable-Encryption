package cia.northboat;

import cia.northboat.se.CipherSystem;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static Field G1, G2, GT, Zr;
    private static Pairing bp;
    // 加密单词长度，为 2n
    private static final int n = 12;
    // 主公钥

    public static Element g1, h1, g2, gt;

    public static List<List<Long>> times;

    static{
        bp = PairingFactory.getPairing("a.properties");
        G1 = bp.getG1();
        G2 = bp.getG2();
        GT = bp.getGT();
        Zr = bp.getZr();
        g1 = G1.newRandomElement().getImmutable();
        h1 = G1.newRandomElement().getImmutable();
        g2 = G2.newRandomElement().getImmutable();
        gt = GT.newRandomElement().getImmutable();
        times = new ArrayList<>();
    }

    public static void main(String[] args) {
        System.out.println("Creat Your Cipher Implement by Extending CipherSystem in Directory se!");
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

    public static void test(CipherSystem cipherSystem, String word, List<String> words, int round){
        System.out.println(cipherSystem.getClass() + " test:");

        cipherSystem.setup();
        cipherSystem.keygen();

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < round; i++) {
            try {
                cipherSystem.enc(word);
            } catch (UnsupportedOperationException e) {
                // e.printStackTrace();
                cipherSystem.enc(words);
            }
        }
        long e1 = System.currentTimeMillis();

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < round; i++){
            try{
                cipherSystem.trap(word);
            }catch (UnsupportedOperationException e){
                // e.printStackTrace();
                cipherSystem.trap(words);
            }
        }
        long e2 = System.currentTimeMillis();

        long s3 = System.currentTimeMillis();
        for(int i = 0; i < round; i++)
            System.out.println(cipherSystem.search());
        long e3 = System.currentTimeMillis();

        times.add(Arrays.asList((e1-s1)/round, (e2-s2)/round, (e3-s3)/round));
        System.out.println("test finished!\n");
    }
}
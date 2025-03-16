package cia.northboat;

import cia.northboat.se.CipherSystem;
import cia.northboat.se.impl.Gu2CKS;
import cia.northboat.se.impl.TBEKS;
import cia.northboat.se.impl.TMS;
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
    private static int n;
    public static Element g, h;

    public static List<List<Long>> times;

    static  {
        G1 = bp.getG1();
        G2 = bp.getG2();
        GT = bp.getGT();
        Zr = bp.getZr();
        n = 26;
        g = G1.newRandomElement().getImmutable();
        h = G1.newRandomElement().getImmutable();
        times = new ArrayList<>();
    }

    static String[] words = {"word", "chinese", "english", "security", "science", "information", "northboat"};
    public static List<String> getWords(int length){
        return new ArrayList<>(Arrays.asList(words).subList(0, length));
    }

    public static void main(String[] args) {
        int m = 1, k = 4, length = 4;
        List<String> words = getWords(length);

        CipherSystem tms = new TMS(G1, GT, Zr, bp, n, g, k);
        CipherSystem tbeks = new TBEKS(G1, GT, Zr, bp, n, g, h, k);
        CipherSystem gu2cks = new Gu2CKS(G1, GT, Zr, bp, n, g, k);

        testOp(m);
        test(tms, "", words, m);
        test(tbeks, "", words, m);
        test(gu2cks, "", words, m);

        printTime();
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

    public static void test(CipherSystem cipherSystem, String word, List<String> words, int m){
        System.out.println(cipherSystem.getClass() + " test:");

        cipherSystem.setup();
        cipherSystem.keygen();

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++) {
            try {
                cipherSystem.enc(word);
            } catch (UnsupportedOperationException e) {
                // e.printStackTrace();
                cipherSystem.enc(words);
            }
        }
        long e1 = System.currentTimeMillis();

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++){
            try{
                cipherSystem.trap(word);
            }catch (UnsupportedOperationException e){
                // e.printStackTrace();
                cipherSystem.trap(words);
            }
        }
        long e2 = System.currentTimeMillis();

        long s3 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            System.out.println(cipherSystem.search());
        long e3 = System.currentTimeMillis();

        times.add(Arrays.asList((e1-s1)/m, (e2-s2)/m, (e3-s3)/m));
        System.out.println("test finished!\n");
    }

    public static void testOp(int m){
        Element u = G1.newRandomElement().getImmutable();
        Element v = G2.newRandomElement().getImmutable();
        Element x = Zr.newRandomElement().getImmutable();

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            bp.pairing(u, v).getImmutable();
        long e1 = System.currentTimeMillis();

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            u.powZn(x).getImmutable();
        long e2 = System.currentTimeMillis();

        long s3 = System.currentTimeMillis();
        for(int i = 0; i < m; i++)
            HashUtil.hashStr2ZrArr(Zr, "test hash time cost", n);
        long e3 = System.currentTimeMillis();

        times.add(Arrays.asList((e1-s1)/m, (e2-s2)/m, (e3-s3)/m));
    }

}
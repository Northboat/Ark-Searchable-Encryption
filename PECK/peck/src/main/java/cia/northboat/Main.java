package cia.northboat;

import cia.northboat.se.CipherSystem;
import cia.northboat.se.impl.FIPECK;
import cia.northboat.se.impl.PECKS;
import cia.northboat.se.impl.SCF;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static Field G1, G2, GT, Zr;
    private static final Pairing bp = PairingFactory.getPairing("a.properties");
    // 加密单词长度，为 2n
    private static final int n = 12;
    // 主公钥

    public static Element g1, h1, g2, gt;

    public static List<List<Long>> times;

    static{
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
        String word = "Hello Maven!";
        int round = 1;
        List<String> strs = Arrays.asList("cyber", "information", "security");

        CipherSystem fipeck = new FIPECK(G1, GT, Zr, bp, n, g1);
        CipherSystem scf = new SCF(G1, GT, Zr, bp, n);
        CipherSystem pecks = new PECKS(G1, GT, Zr, bp, n, g1);

        test(fipeck, word, strs, round);
        test(scf, word, strs, round);
        test(pecks, word, strs, round);

        printTime();
    }


    public static void test(CipherSystem cipherSystem, String str, List<String> strs, int m){
        System.out.println(cipherSystem.getClass() + " test:");

        cipherSystem.setup();
        cipherSystem.keygen();

        long s1 = System.currentTimeMillis();
        for(int i = 0; i < m; i++) {
            try {
                cipherSystem.enc(str);
            } catch (UnsupportedOperationException e) {
                // e.printStackTrace();
                cipherSystem.enc(strs);
            }
        }
        long e1 = System.currentTimeMillis();

        long s2 = System.currentTimeMillis();
        for(int i = 0; i < m; i++){
            try{
                cipherSystem.trap(str);
            }catch (UnsupportedOperationException e){
                // e.printStackTrace();
                cipherSystem.trap(strs);
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
}

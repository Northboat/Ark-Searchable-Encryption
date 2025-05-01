package cia.arkrypto.util;

import cia.arkrypto.se.CipherSystem;
import cia.arkrypto.se.impl.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {
    public static void singleThreadOneWordTest(Field G1, Field G2, Field GT, Field Zr, Pairing bp, int n){
        int m = 10, k = 3, q = 1024;
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


//        Runner.test(pauks, w, null, m);
//        Runner.test(saPauks, w, null, m);
//        Runner.test(dIBaeks, w, null, m);
//        Runner.test(pMatch, w, null, m);
//        Runner.test(crIma, w, null, m);
//        Runner.test(tu2Cks, w, null, m);
//        Runner.test(tuCr, w, null, m);
//        Runner.test(duMse, w, null, m);
//        Runner.test(paeks, w, null, m);
//        Runner.test(spwseOne, w, null, m);
//        Runner.test(spwseTwo, w, null, m);
        Runner.test(peks, w, null, m);

        Runner.printTime();
    }


    public static void singleThreadMultiWordsTest(Field G1, Field G2, Field GT, Field Zr, Pairing bp, int n) {
        int m = 1, k = 4;
        String file = "2.txt";
        List<String> words = FileUtil.readFileToList(file);

        CipherSystem tms = new TMS(G1, GT, Zr, bp, n, k);
        CipherSystem tbeks = new TBEKS(G1, GT, Zr, bp, n, k);
        CipherSystem gu2cks = new Gu2CKS(G1, GT, Zr, bp, n, k);

        Runner.test(tms, "", words, m);
        Runner.test(tbeks, "", words, m);
        Runner.test(gu2cks, "", words, m);

        Runner.printTime();
    }


    public static void multiThreadMultiWordsTest(Field G1, Field G2, Field GT, Field Zr, Pairing bp, int n) {
        int round = 1, sender = 1, receiver = 1;

        String file = "2.txt";
        List<String> words = FileUtil.readFileToList(file);

        CipherSystem scf = new SCF(G1, GT, Zr, bp, n);
        CipherSystem ap = new AP(G1, GT, Zr, bp, n, G2);
        CipherSystem pecks = new PECKS(G1, GT, Zr, bp, n);

        List<CipherSystem> cipherSystems = new ArrayList<>();
        cipherSystems.add(scf);
        cipherSystems.add(ap);
        cipherSystems.add(pecks);

        Runner.executorServiceTest(cipherSystems, words, sender, receiver, round);

        System.out.println("The Time Cost Log in ./time.log");
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

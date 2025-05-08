package cia.arkrypto;

import cia.arkrypto.util.TestUtil;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

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
        TestUtil.singleThreadOneWordTest(G1, G2, GT, Zr, bp, n);
//        TestUtil.singleThreadMultiWordsTest(G1, G2, GT, Zr, bp, n);
//        TestUtil.multiThreadMultiWordsTest(G1, G2, GT, Zr, bp, n);
    }

}

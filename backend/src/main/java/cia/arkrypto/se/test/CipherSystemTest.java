package cia.arkrypto.se.test;

import cia.arkrypto.se.crypto.RangedSEArchetype;
import cia.arkrypto.se.util.TestUtil;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class CipherSystemTest {
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
//        TestUtil.singleThreadTest(G1, G2, GT, Zr, bp, n);
//        TestUtil.multiThreadTest(G1, G2, GT, Zr, bp, n);

        RangedSEArchetype rangedSEArchetype = new RangedSEArchetype(G1, GT, Zr, bp);
        rangedSEArchetype.test();
    }
}

package cia.northboat.sim;

import cia.northboat.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.List;

public class TMS {

    private static Pairing bp;
    private static Field G, Zr;
    private static Element g;
    public static Element H(List<String> words){
        Element h = g.duplicate();
        for(String str: words){
            Element[] w = HashUtil.hashStr2ZrArr(Zr, str, 26);
            h = HashUtil.hashZrArr2G(h, w);
        }
        return h.getImmutable();
    }

    public static Element H0(String word){
        Element[] w = HashUtil.hashStr2ZrArr(Zr, word, 26);
        return HashUtil.hashZrArr2Zr(Zr, g, w);
    }

    public static Element ONE, TWO, THREE, FOUR, FIVE, SIX;
    public static Element f(Element x){
        Element part1 = THREE.mul(x.mul(x)).getImmutable();
        Element part2 = TWO.mul(x).getImmutable();
        Element part3 = FIVE;
//        System.out.println(part1 + "\t" + part2 + "\t" + part3);
        return part1.add(part2).add(part3).getImmutable();
    }


    public static void init(Field G, Field Zr, Element g, Pairing bp){
        TMS.G = G;
        TMS.Zr = Zr;
        TMS.g = g;
        TMS.bp = bp;

        ONE = Zr.newOneElement().getImmutable();
        TWO = Zr.newElement(2).getImmutable();
        THREE = Zr.newElement(3).getImmutable();
        FOUR = Zr.newElement(4).getImmutable();
        FIVE = Zr.newElement(5).getImmutable();
        SIX = Zr.newElement(6).getImmutable();
    }


    static Element[] sk, pk;
    public static void keyGen(int n){
        sk = new Element[n]; pk = new Element[n];
        for(int i = 0; i < n; i++){
            Element x = Zr.newElement(i+1).getImmutable();
//            Element x = Zr.newRandomElement().getImmutable();
//            System.out.println(x + "\t" + f(x));
            sk[i] = f(x);
            pk[i] = g.powZn(sk[i]).getImmutable();
        }

//        for(Element s: sk){ System.out.println(s); }
    }



    static Element Q, C1, C2, pk5, K5;
    public static void enc(List<String> words){
        Element part1 = pk[0].powZn(FOUR).getImmutable();
        Element part2 = pk[1].powZn(SIX.negate()).getImmutable();
        Element part3 = pk[2].powZn(FOUR).getImmutable();
        Element part4 = pk[3].powZn(ONE.negate()).getImmutable();

        Q = part1.add(part2).add(part3).add(part4).getImmutable();

        Element s = Zr.newRandomElement().getImmutable();
        C1 = g.powZn(s).getImmutable();


        C2 = bp.pairing(H(words), Q).powZn(s).getImmutable();

        pk5 = g.powZn(Zr.newElement(90)).getImmutable();

        K5 = bp.pairing(H(words), pk5.powZn(s)).getImmutable();
    }



    static Element[] T;
    public static void trap(List<String> words){
        T = new Element[sk.length];

        Element H = H(words).getImmutable();
//        System.out.println(H);
        for(int i = 0; i < T.length; i++){
//            System.out.println(sk[i]);
            T[i] = H.powZn(sk[i]).getImmutable();
        }

    }


    static Element K1, K2, K3, K;
    public static boolean search(List<String> words){
        K1 = bp.pairing(T[0], C1);
        K2 = bp.pairing(T[1], C1);
        K3 = bp.pairing(T[2], C1);

        Element H = H(words);
        Element D = bp.pairing(C1, H).getImmutable();
        Element part1 = D.powZn(sk[0].mul(Zr.newElement(15).div(FOUR))).getImmutable();
        Element part2 = D.powZn(sk[1].mul(FIVE.negate())).getImmutable();
        Element part3 = D.powZn(sk[2].mul(FIVE.div(TWO))).getImmutable();
        Element part4 = D.powZn(Zr.newElement(90).mul(FOUR.invert().negate())).getImmutable();

        K = part1.mul(part2).mul(part3).mul(part4);
        System.out.println("TMS K: " + K);
        System.out.println("TMS C2: " + C2);
        return K.equals(C2);
    }
}

package cia.northboat.sim;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.List;

public class Gu2CKS {
    private static Pairing bp;
    private static Field G, Zr;
    private static Element g, skc, pkc, g1;



    static Element ZERO, ONE, TWO, SEVEN;
    public static void init(Field G, Field Zr, Pairing bp, Element g){
        Gu2CKS.G = G;
        Gu2CKS.Zr = Zr;
        Gu2CKS.bp = bp;
        Gu2CKS.g = g;
        skc = Zr.newRandomElement().getImmutable();
        pkc = g.powZn(skc).getImmutable();

        Element x1 = Zr.newRandomElement().getImmutable();
        g1 = g.powZn(x1).getImmutable();

        ZERO = Zr.newZeroElement().getImmutable();
        ONE = Zr.newOneElement().getImmutable();
        TWO = Zr.newElement(2).getImmutable();
        SEVEN = Zr.newElement(7).getImmutable();
    }


    static Element[] sk, pk;
    public static void keyGen(int n){
        sk = new Element[n]; pk = new Element[n];
        for(int i = 0; i < n ; i++){
            sk[i] = Zr.newRandomElement().getImmutable();
            pk[i] = g.powZn(sk[i]).getImmutable();
        }
    }


    static Element[] eta, B;
    static Element C1;
    public static void enc(List<String> words){
        // 模拟传入的关键词
        eta = new Element[3]; B = new Element[3];
        eta[0] = Zr.newElement(207).getImmutable();
        eta[1] = Zr.newElement(-30).getImmutable();
        eta[2] = Zr.newOneElement().getImmutable();

        Element r1 = Zr.newRandomElement().getImmutable();


        for(int i = 0; i < 3; i++){
            B[i] = g.powZn(r1.mul(eta[i]).mul(SEVEN.invert())).getImmutable();
        }
        // 207-30x(10)+(10)^2 = 7
        Element part1 = bp.pairing(g1, pk[0]).powZn(r1).getImmutable();
        Element part2 = bp.pairing(g1, pk[1]).powZn(r1).getImmutable();
        Element part3 = bp.pairing(g1, pk[2]).powZn(r1).getImmutable();
        C1 = part1.mul(part2).mul(part3).getImmutable();
//        C1 = part1.getImmutable();
    }



    static Element T10, T11, T12, T13, T20, T21, T22, T23, T30, T31, T32, T33;
    public static void trap(List<String> words){
        // 模拟关键词的哈希值
        Element w1 = Zr.newElement(10).getImmutable();
        Element w2 = Zr.newElement(20).getImmutable();
        Element a = Zr.newRandomElement().getImmutable();
        Element b = Zr.newRandomElement().getImmutable();
        Element c = Zr.newRandomElement().getImmutable();
        Element d = null;

        T10 = pk[0].powZn(a).getImmutable();
        d = pkc.powZn(sk[0].mul(a));
        T11 = g1.powZn(sk[0].mul(TWO.invert()).mul(w1.powZn(ZERO).add(w2.powZn(ZERO)))).mul(d).getImmutable();
//        System.out.println(TWO.invert().mul(TWO));
//        System.out.println(w1.powZn(TWO).add(w2.powZn(TWO)));
        T12 = g1.powZn(sk[0].mul(TWO.invert()).mul(w1.powZn(ONE).add(w2.powZn(ONE)))).mul(d).getImmutable();
        T13 = g1.powZn(sk[0].mul(TWO.invert()).mul(w1.powZn(TWO).add(w2.powZn(TWO)))).mul(d).getImmutable();

        d = pkc.powZn(sk[1].mul(b));
        T20 = pk[1].powZn(b).getImmutable();
        T21 = g1.powZn(sk[1].mul(TWO.invert()).mul(w1.powZn(ZERO).add(w2.powZn(ZERO)))).mul(d).getImmutable();
        T22 = g1.powZn(sk[1].mul(TWO.invert()).mul(w1.powZn(ONE).add(w2.powZn(ONE)))).mul(d).getImmutable();
        T23 = g1.powZn(sk[1].mul(TWO.invert()).mul(w1.powZn(TWO).add(w2.powZn(TWO)))).mul(d).getImmutable();

        d = pkc.powZn(sk[2].mul(c));
        T30 = pk[2].powZn(c).getImmutable();
        T31 = g1.powZn(sk[2].mul(TWO.invert()).mul(w1.powZn(ZERO).add(w2.powZn(ZERO)))).mul(d).getImmutable();
        T32 = g1.powZn(sk[2].mul(TWO.invert()).mul(w1.powZn(ONE).add(w2.powZn(ONE)))).mul(d).getImmutable();
        T33 = g1.powZn(sk[2].mul(TWO.invert()).mul(w1.powZn(TWO).add(w2.powZn(TWO)))).mul(d).getImmutable();
    }


    static Element[] T;
    public static boolean search(){
        T = new Element[3];

        T[0] = T11.div(T10.powZn(skc)).mul(T21.div(T20.powZn(skc))).mul(T31.div(T30.powZn(skc))).getImmutable();
        T[1] = T12.div(T10.powZn(skc)).mul(T22.div(T20.powZn(skc))).mul(T32.div(T30.powZn(skc))).getImmutable();
        T[2] = T13.div(T10.powZn(skc)).mul(T23.div(T20.powZn(skc))).mul(T33.div(T30.powZn(skc))).getImmutable();

        Element part1 = bp.pairing(B[0], T[0]).getImmutable();
        Element part2 = bp.pairing(B[1], T[1]).getImmutable();
        Element part3 = bp.pairing(B[2], T[2]).getImmutable();

        Element K = part1.mul(part2).mul(part3).getImmutable();

        System.out.println("Gu2CKS K: " + K);
        System.out.println("Gu2CKS C1: " + C1);

        return K.equals(C1);
    }

}

package cia.northboat.sim;

import cia.northboat.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.List;

public class TBEKS {

    private static Pairing bp;
    private static Field G, Zr;
    private static Element g;
    // MSK
    private static Element r, d, s, v, alpha;
    // MPK
    public static Element h, u, w1, w2;


    public static void init(Field G, Field Zr, Pairing bp, Element g, Element h){
        TBEKS.G = G;
        TBEKS.Zr = Zr;
        TBEKS.bp = bp;
        TBEKS.g = g;

        TBEKS.h = h;

        TBEKS.r = Zr.newElement(5).getImmutable();
        TBEKS.d = Zr.newElement(4).getImmutable();
        TBEKS.v = Zr.newElement(6).getImmutable();
        TBEKS.s = Zr.newRandomElement().getImmutable();
        TBEKS.u = s.div(d).getImmutable();
        TBEKS.w1 = g.powZn(r.div(d)).getImmutable();
        TBEKS.w2 = g.powZn(r.mul(u)).getImmutable();

        TBEKS.alpha = Zr.newRandomElement().getImmutable();

        ONE = Zr.newOneElement().getImmutable();
        TWO = Zr.newElement(2).getImmutable();
        THREE = Zr.newElement(3).getImmutable();
        FOUR = Zr.newElement(4).getImmutable();
        FIVE = Zr.newElement(5).getImmutable();

    }

    public static Element ONE, TWO, THREE, FOUR, FIVE;
    public static Element f1(Element x){
        return FIVE.add(TWO.mul(x)).add(THREE.mul(x.mul(x))).getImmutable();
    }
    public static Element f2(Element x){
        return FOUR.add(x).add(TWO.mul(x.mul(x))).getImmutable();
    }

    private static Element[] R, D;
    public static Element[] pk;
    // n 是参与计算用户数量
    public static void keyGen(int n){
        R = new Element[n]; D = new Element[n];
        for(int i = 0; i < n; i++){
            Element x = Zr.newElement(i+1).getImmutable();
//            Element a = Zr.newRandomElement().getImmutable();
//            Element x = Zr.newRandomElement().getImmutable();
            R[i] = f1(x);
            D[i] = f2(x);
        }
//        for(int i = 0; i < n; i++){ System.out.println(R[i] + "\t" + D[i]); }
    }


    public static Element H(String word){
        Element[] w = HashUtil.hashStr2ZrArr(Zr, word, 26);
        return HashUtil.hashZrArr2Zr(Zr, g, w);
    }

    // l: 关键词数量
    public static Element I0, I1;
    public static Element[] I;
    public static void enc(List<String> words){
        int l = words.size();
        I0 = h.powZn(alpha.negate()).getImmutable();
        I1 = w1.powZn(alpha).getImmutable();

        I = new Element[l];
        for(int i = 0; i < l; i++){
            I[i] = w2.powZn(alpha.mul(H(words.get(i)))).getImmutable();
        }
    }

    public static Element A, B, T1, T2;
    public static Element[] lambda, mu;
    public static void trap(List<String> words){
        Element beta = Zr.newRandomElement().getImmutable();
        Element sum = Zr.newZeroElement();
        for(String w: words){
            sum.add(H(w));
        }
        sum.getImmutable();

        A = g.powZn(beta).getImmutable();
        B = h.powZn(u.mul(sum).add(beta)).getImmutable();

        // 固定为 3 个用户
        lambda = new Element[3]; mu = new Element[3];
        Element a = null;
        T1 = G.newOneElement();
        T2 = G.newOneElement();
        for(int i = 0; i < 3; i++){
            switch (i) {
                case 0 -> a = THREE;
                case 1 -> a = THREE.negate();
                case 2 -> a = ONE;
            }
            lambda[i] = A.powZn(R[i].mul(a)).getImmutable();
            mu[i] = B.powZn(D[i].mul(a)).getImmutable();
            T1.mul(lambda[i]);
            T2.mul(mu[i]);
        }
        T1.getImmutable();
//        System.out.println(T1);
//        System.out.println(A.powZn(FIVE));
        T2.getImmutable();
//        System.out.println(T2);
//        System.out.println(B.powZn(FOUR));
    }


    public static boolean search(){
        Element part1 = bp.pairing(I0, T1).getImmutable();
        Element part2 = bp.pairing(I1, T2).getImmutable();

        Element product = G.newOneElement();
        for(Element i: I){
            product.mul(i);
        }
        product = product.getImmutable();
//        System.out.println(product);
        Element part3 = bp.pairing(product, h);

        Element left = part1.mul(part2).getImmutable();
        Element right = part3.getImmutable();

        System.out.println("TBEKS Left: " + left);
        System.out.println("TBEKS Right: " + right);

        return left.equals(right);
    }
}

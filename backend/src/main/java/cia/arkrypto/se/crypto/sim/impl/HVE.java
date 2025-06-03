package cia.arkrypto.se.crypto.sim.impl;

import cia.arkrypto.se.crypto.sim.CipherSystem;
import cia.arkrypto.se.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.Map;

// Hidden Vector Encryption
public class HVE extends CipherSystem {


    private final Element M;
    private final int l;
    public HVE(Field G1, Field GT, Field Zr, Pairing BP, int n, int l) {
        super(G1, GT, Zr, BP, n);
        this.l = l;
        M = HashUtil.hashStr2GT(this.getZr(), randomGT(), "test", this.getN());
    }


    // 优化的 Gray 编码器
    public Map<String, String> grayOptimizer(Map<String, Double> cells){

        return null;
    }


    Element g, a;
    @Override
    public void setup() {
        g = randomG();
        a = randomZ();
    }


    Element[] u, h, w, U, H, W;
    @Override
    public void keygen() {
        u = new Element[l];
        h = new Element[l];
        w = new Element[l];
        U = new Element[l];
        H = new Element[l];
        W = new Element[l];
        for(int i = 0; i < l; i++){
            Element t = randomG();
            u[i] = t; U[i] = t;
            t = randomG();
            h[i] = t; H[i] = t;
            t = randomG();
            w[i] = t; W[i] = t;
        }
    }


    Element C0, C1;
    Element[] C2, C3;
    @Override
    public void enc(String w) {
        Element s = randomZ();
        C0 = M.mul(pairing(g, g).powZn(a.mul(s))).getImmutable();
        C1 = g.powZn(s).getImmutable();

        C2 = new Element[l];
        C3 = new Element[l];
        for(int i = 0; i < l; i++){
            Element I = w.charAt(i) == '1' ? getI("1") : getI("0");
            C2[i] = U[i].powZn(I).mul(H[i]).powZn(s).getImmutable();
//            C2[i] = U[i].powZn(I).mul(H[i]).getImmutable();
            C3[i] = W[i].powZn(s).getImmutable();
//            C3[i] = W[i].getImmutable();
        }



        Element test = this.getG().newOneElement();
        for(int i = 0; i < l; i++){
            test.mul(C2[i]).mul(C3[i]);
        }
        System.out.println("test: " + test);
        System.out.println("M:" + M);
        System.out.println("e(g,g)^{as}: " + pairing(g, g).powZn(a.mul(s)));
        System.out.println("C0: " + C0);
        System.out.println("C0/e(g,g)^{as}: " + C0.div(pairing(g, g).powZn(a.mul(s))) + "\n");
    }


    String T;
    Element K0;
    Element[] K1, K2;
    @Override
    public void trap(String q) {
        T = q;
        K1 = new Element[l];
        K2 = new Element[l];

        Element c = this.getG().newOneElement();
        for(int i = 0; i < l; i++){
            if(q.charAt(i) != '*'){
                Element T = q.charAt(i) == '1' ? getI("1") : getI("0");
                Element r1 = randomZ(), r2 = randomZ();
                K1[i] = g.powZn(r1).getImmutable();
                K2[i] = g.powZn(r2).getImmutable();
                c.mul(u[i].powZn(T).mul(h[i]).powZn(r1).mul(w[i]).powZn(r2));
//                c.mul(u[i].powZn(T).mul(h[i]).mul(w[i]));
            }
        }
        c.getImmutable();
        K0 = g.powZn(a).mul(c).getImmutable();

        System.out.println("Product: " + c);
        System.out.println("e(C1, K0): " + pairing(C1, K0));

    }

    @Override
    public boolean search() {
        Element part1 = pairing(C1, K0);

//        System.out.println("cal: " + part1.div(C0));

        Element part2 = this.getGT().newOneElement();
        for(int i = 0; i < l; i++){
            if(T.charAt(i) != '*'){
                Element part3 = pairing(C2[i], K1[i]);
                Element part4 = pairing(C3[i], K2[i]);

                part2.mul(part3.mul(part4));
            }
        }
        part2.getImmutable();

        System.out.println("Pairing Product: " + part2);
//        System.out.println("part2: " + part2);
        System.out.println("e(C1, K0)/Pairing Product: "+ part1.div(part2));

        Element M1 = C0.div(part1.div(part2)).getImmutable();

        System.out.println("HVE left: " + M);
        System.out.println("HVE right: " + M1);
        return M.isEqual(M1);
    }


}

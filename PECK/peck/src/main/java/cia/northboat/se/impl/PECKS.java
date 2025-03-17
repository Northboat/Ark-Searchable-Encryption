package cia.northboat.se.impl;

import cia.northboat.se.CipherSystem;
import cia.northboat.util.HashUtil;
import cia.northboat.util.PolynomialUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;
import java.util.List;


public class PECKS extends CipherSystem {

    public PECKS(Field G, Field GT, Field Zr, Pairing bp, int n){
        super(G, GT, Zr, bp, n);
    }

    public Element H(String str){
        Element[] w = HashUtil.hashStr2ZrArr(this.getZr(), str, this.getN());
        return HashUtil.hashZrArr2Zr(this.getZr(), w);
    }


    private Element g, g1, g2, EK, sk_cs, sk_ts, pk_cs, pk_ts, x_t, b1, a1, b2;
    @Override
    public void setup(){
        g = this.getG().newRandomElement().getImmutable();
        a1 = this.getZr().newRandomElement().getImmutable();
        b1 = this.getZr().newRandomElement().getImmutable();
        b2 = this.getZr().newRandomElement().getImmutable();
        x_t = this.getZr().newRandomElement().getImmutable();
        sk_cs = this.getZr().newRandomElement().getImmutable();
        sk_ts = this.getZr().newRandomElement().getImmutable();

        g1 = g.powZn(b1).getImmutable();
        g2 = g.powZn(b2).getImmutable();
        EK = g.powZn(f(x_t).div(b1)).getImmutable();
        pk_cs = g.powZn(sk_cs).getImmutable();
        pk_ts = g.powZn(sk_ts).getImmutable();
    }

    public Element f(Element x){
        return b1.add(a1.mul(x)).getImmutable();
    }

    Element D_i, E_i, F_i, G_i, PK_i;
    @Override
    public void keygen(){
        Element x_ti = this.getZr().newRandomElement().getImmutable();
        Element y_i = this.getZr().newRandomElement().getImmutable();

        D_i = g2.powZn(f(x_ti).mul(x_t.negate().div(x_ti.sub(x_t)))).getImmutable();
        E_i = g2.powZn(b1.mul(x_ti.negate().div(x_t.sub(x_ti)))).getImmutable();
        G_i = y_i.getImmutable();
        PK_i = g.powZn(y_i).getImmutable();
    }

    Element[] C1;
    Element C2, C3;
    int l;
    @Override
    public void enc(List<String> W) {
        Element r = this.getZr().newRandomElement().getImmutable();
        C2 = EK.powZn(r).getImmutable();
        C3 = g.powZn(r).getImmutable();

        // 将系数模一个 p
//        BigInteger p = new BigInteger("4");

        l = W.size();

        List<Element> factors = new ArrayList<>(l);
        for(int i = 0; i < l; i++){
//            BigInteger v = H(W.get(i)).toBigInteger();
//            factors.add(this.getZr().newElement(v.mod(p)).getImmutable());
            factors.add(H(W.get(i)));
        }
        System.out.println("function params: " + factors);

        List<Element> pi = PolynomialUtil.getCoefficients(this.getZr(), factors);
        pi.set(0, pi.get(0).add(this.getZr().newOneElement()).getImmutable());
        System.out.println("polynomial coefficients: " + pi);

        C1 = new Element[l+1];
        for(int i = 0; i <= l; i++){
            C1[i] = g1.powZn(r.mul(pi.get(i))).getImmutable();
        }
    }


    Element[] T1;
    Element T2, T3, T4;
    @Override
    public void trap(List<String> Q) {
        Element s = this.getZr().newRandomElement().getImmutable(), pi = this.getZr().newRandomElement().getImmutable();
        T2 = E_i.powZn(s).getImmutable();
        T3 = D_i.powZn(s).getImmutable();
        T4 = g.powZn(pi).getImmutable();

        if(Q.size() > l){
            return;
        }

        T1 = new Element[l+1];
        Element m = this.getZr().newElement(Q.size()).getImmutable();

        for(int i = 0; i <= l; i++){
            Element sum = this.getZr().newZeroElement();
            Element fai = this.getZr().newElement(i).getImmutable();

            for (String str: Q) {
                sum.add(H(str).powZn(fai));
            }
            sum.getImmutable();

            T1[i] = g2.powZn(s.mul(m.invert()).mul(sum)).mul(pk_cs.powZn(pi)).getImmutable();
        }
    }

    @Override
    public boolean search() {
        Element left = this.getGT().newOneElement();
        for(int i = 0; i <= l; i++){
            Element cur = this.getBp().pairing(C1[i], T1[i].div(T4.powZn(sk_cs)));
            left.mul(cur);
        }
        left.getImmutable();

        Element right = this.getBp().pairing(C2, T2).mul(this.getBp().pairing(C3, T3)).getImmutable();

        System.out.println("left: " + left);
        System.out.println("right: " + right);

        return left.isEqual(right);
    }
}

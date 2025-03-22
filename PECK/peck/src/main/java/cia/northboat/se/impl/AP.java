package cia.northboat.se.impl;

import cia.northboat.se.CipherSystem;
import cia.northboat.util.HashUtil;
import cia.northboat.util.PolynomialUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;
import java.util.List;

public class AP extends CipherSystem {

    Field G2;
    public AP(Field G, Field GT, Field Zr, Pairing bp, int n, Field G2){
        super(G, GT, Zr, bp, n, true);
        this.G2 = G2;
    }

    public AP(Field G, Field GT, Field Zr, Pairing bp, int n, Field G2, boolean updatable){
        super(G, GT, Zr, bp, n, updatable);
        this.G2 = G2;
    }

    public Element H1(String str){
        Element[] w = h(str);
        return HashUtil.hashZrArr2Zr(this.getZr(), w);
    }

    public Element H2(Element gt){
        return HashUtil.hashGT2G(this.getG(), gt);
    }

    Element g1, g2;
    @Override
    public void setup() {
        g1 = randomG();
        g2 = G2.newRandomElement().getImmutable();
    }


    Element x, X, V, y, Y;
    @Override
    public void keygen() {
        x = randomZ();
        X = g1.powZn(x).getImmutable();
        V = G2.newRandomElement().getImmutable();
        y = randomZ();
        Y = g2.powZn(y).getImmutable();
    }


    int l;
    Element[] B;
    Element Ci1, Ci2, Ci3;
    @Override
    public void enc(List<String> W) {
        l = W.size();
        Element s = randomZ(), r = randomZ();
        Element t = pairing(X, V).powZn(s).getImmutable();
        Ci1 = g1.powZn(s).getImmutable();
        Ci2 = t.mul(pairing(X, Y).powZn(r)).getImmutable();
        Ci3 = g2.powZn(r).getImmutable();

        List<Element> factors = new ArrayList<>();
        Element pi = randomZ();
        for(String w: W){
            factors.add(H1(w));
        }
        factors.add(pi);
        List<Element> eta = PolynomialUtil.getCoefficients(this.getZr(), factors);
        eta.set(0, eta.get(0).add(this.getZr().newOneElement()).getImmutable());

        System.out.println("factors: " + factors);
        System.out.println("words size: " + l + "\nfactors size: " + factors.size() + "\neta size: " + eta.size());
        System.out.println("polynomial coefficients: " + eta);

        B = new Element[l+2];
        for(int i = 0; i <= l+1; i++){
            B[i] = Ci3.powZn(eta.get(i)).getImmutable();
        }
    }


    Element Ti1, Ti2;
    Element[] Ti;
    @Override
    public void trap(List<String> Q) {
        Ti1 = randomZ();
        Element xi = randomZ();
        Ti2 = g1.powZn(xi).getImmutable();

        if(Q.size() > l){
            return;
        }

        Element m = this.getZr().newElement(Q.size()).getImmutable();
        Ti = new Element[l+2];
        for(int i = 0; i <= l+1; i++){
            Element fai = this.getZr().newElement(i).getImmutable();
            Element sum = this.getZr().newZeroElement();
            for(String q: Q){
                sum.add(H1(q).powZn(fai));
            }
            sum.getImmutable();
            Ti[i] = g1.powZn(m.invert().mul(Ti1).mul(y).mul(sum)).mul(X.powZn(xi)).getImmutable();
        }
    }

    @Override
    public boolean search() {
        Element t = pairing(Ci1, V).powZn(x).getImmutable();

        Element right = Ci2.powZn(Ti1).getImmutable();

        Element part1 = t.powZn(Ti1).getImmutable();
        Element part2 = this.getGT().newOneElement();
        for(int i = 0; i <= l+1; i++){
            Element cur = pairing(Ti[i].div(Ti2.powZn(x)), B[i]).powZn(x).getImmutable();
            part2.mul(cur);
        }
        part2.getImmutable();
        System.out.println("product: " + part2);

        Element left = part1.mul(part2).getImmutable();

        System.out.println("left: " + left);
        System.out.println("right: " + right);
        return left.isEqual(right);
    }




    Element[] rk1, rk2, rk3;
    @Override
    public void updateKey(){
        Element[] s = new Element[l], K = new Element[l];
        rk1 = new Element[l];
        rk2 = new Element[l];
        rk3 = new Element[l];

        Element product = this.getGT().newOneElement();
        for(int i = 0; i < l; i++){
            s[i] = randomZ();
            K[i] = randomGT();
            product.mul(K[i]);
            rk1[i] = g1.powZn(s[i]).getImmutable();
            rk2[i] = H2(product);
            if(i == 0){
                rk3[i] = K[i].mul(pairing(X, V).powZn(s[i])).getImmutable();
                continue;
            }
            rk3[i] = K[i].mul(pairing(X, V).powZn(s[i].sub(s[i-1]))).getImmutable();
        }
    }


    Element Cj1, Cj2, Cj3;
    Element[] Cj4, Cj5, Cj6;
    @Override
    public void updateEnc(){
        Cj1 = Ci1.duplicate().getImmutable();
        Cj2 = Ci2.duplicate().getImmutable();
        Cj3 = Ci3.duplicate().getImmutable();

        Cj4 = rk1;
        Cj5 = new Element[l];
        Cj6 = new Element[l];
        for(int i = 0; i < l; i++){
            Cj5[i] = pairing(rk2[i], Cj3).getImmutable();
            if(i == 0){
                Cj6[i] = rk3[i].getImmutable();
                continue;
            }
            Cj6[i] = Cj6[i-1].mul(rk3[i]).getImmutable();
        }
    }


    Element Tj1, Tj2;
    Element[] Tj;
    @Override
    public void constTrap(List<String> Q){
        Tj1 = randomZ();
        Element xi = randomZ();
        Tj2 = g1.powZn(xi).getImmutable();

        Element m = this.getZr().newElement(Q.size()).getImmutable();
        Tj = new Element[l+2];
        for(int i = 0; i <= l+1; i++){
            Element fai = this.getZr().newElement(i).getImmutable();
            Element sum = this.getZr().newZeroElement();
            for(String q: Q){
                sum.add(H1(q).powZn(fai));
            }
            sum.getImmutable();
            Tj[i] = g1.powZn(m.invert().mul(Tj1).mul(y).mul(sum)).mul(X.powZn(xi)).getImmutable();
        }
    }


    @Override
    public boolean updateSearch(){
        Element t = pairing(Cj1, V).powZn(x).getImmutable();
        int j = l >> 1;
        Element K = Cj6[j].div(pairing(Cj4[j], V).powZn(x)).getImmutable();

        Element product = this.getGT().newOneElement();
        for(int i = 0; i <= l+1; i++){
            Element cur = pairing(Tj[i].div(Tj2.powZn(x)), B[i]).powZn(x).getImmutable();
            product.mul(cur);
        }
        product.getImmutable();
        System.out.println("product: " + product);

        Element left = t.mul(Cj5[j]).powZn(Tj1).mul(product).getImmutable();
        Element right = Cj2.mul(pairing(H2(K), Cj3)).powZn(Tj1).getImmutable();

        System.out.println("left: " + left);
        System.out.println("right: " + right);

        return left.isEqual(right);
    }
}

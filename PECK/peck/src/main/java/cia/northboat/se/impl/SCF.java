package cia.northboat.se.impl;

import cia.northboat.se.CipherSystem;
import cia.northboat.se.SearchableEncryption;
import cia.northboat.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class SCF extends CipherSystem {

    public SCF(Field G, Field GT, Field Zr, Pairing bp, int n){
        super(G, GT, Zr, bp, n);
    }

    public Element h2(String w){
        Element[] W = HashUtil.hashStr2ZrArr(this.getZr(), w, this.getN());
        return HashUtil.hashZrArr2Zr(this.getZr(), W);
    }

    public Element h3(Element gt){
        return HashUtil.hashGT2Zr(this.getZr(), gt);
    }

    public Element h4(Element gt){
        return h3(gt);
    }

    public Element h5(Element g){
        return HashUtil.hashG2Zr(this.getZr(), g);
    }

    private Element P, Q; // G群上元素
    @Override
    public void setup(){
        P = this.getG().newRandomElement().getImmutable();
        Q = this.getG().newRandomElement().getImmutable();
    }

    private Element SK_do, SK_dr;
    public Element PK_do, PK_dr;
    @Override
    public void keygen(){
        Element a = this.getZr().newRandomElement().getImmutable(), b = this.getZr().newRandomElement().getImmutable();
        SK_do = a; PK_do = P.mulZn(a).getImmutable();
        SK_dr = b; PK_dr = P.mulZn(b).getImmutable();
    }



    public Element t, eh, uh, CV, v;
    @Override
    public void enc(String w){
        t = this.getZr().newRandomElement().getImmutable();
        eh = h3(this.getBp().pairing(PK_dr.mulZn(SK_do), Q.mulZn(h2(w)).powZn(t))).getImmutable();
        uh = h4(this.getBp().pairing(PK_dr.mulZn(SK_do), Q.mulZn(h2(w)).powZn(t))).getImmutable();
        Element l = this.getZr().newRandomElement().getImmutable();
        CV = h5(PK_dr.mulZn(l)).getImmutable();
        v = h5(P.mulZn(l).mulZn(SK_dr)).getImmutable();
    }


    public Element T;
    @Override
    public void trap(String w){
        T = Q.mulZn(h2(w)).mulZn(SK_dr).mulZn(t).powZn(t).getImmutable();
    }


    @Override
    public boolean search(){
        Element pairing = this.getBp().pairing(PK_do, T);
        System.out.println("pairing: " + pairing);
        return h3(pairing).isEqual(h4(pairing));
    }
}

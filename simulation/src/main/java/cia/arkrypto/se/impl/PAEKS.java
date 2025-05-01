package cia.arkrypto.se.impl;

import cia.arkrypto.se.CipherSystem;
import cia.arkrypto.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class PAEKS extends CipherSystem {

    public PAEKS(Field G, Field GT, Field Zr, Pairing bp, int n){
        super(G, GT, Zr, bp, n);
    }

    public Element H(String str){
        Element[] w = h(str);
        return HashUtil.hashZrArr2G(g, w);
    }


    Element g;
    @Override
    public void setup(){
        g = randomG();
    }


    Element sk_s, pk_s, sk_r, pk_r;
    @Override
    public void keygen(){
        sk_s = randomZ();
        sk_r = randomZ();
        pk_s = g.powZn(sk_s);
        pk_r = g.powZn(sk_r);
    }


    Element C1, C2;
    @Override
    public void enc(String w){
        Element r = randomZ();
        C1 = H(w).powZn(sk_s).mul(g.powZn(r)).getImmutable();
        C2 = pk_r.powZn(r).getImmutable();
    }



    Element T;
    @Override
    public void trap(String w){
        T = pairing(H(w).powZn(sk_r), pk_s).getImmutable();
    }



    @Override
    public boolean search(){
        System.out.println("T: " + T);
        Element left = T.mul(pairing(C2, g)).getImmutable();
        Element right = pairing(C1, pk_r).getImmutable();
        System.out.println("PAEKS left: " + left);
        System.out.println("PAEKS right: " + right);
        return left.isEqual(right);
    }



}

package cia.arkrypto.se.crypto.impl;

import cia.arkrypto.se.crypto.CipherSystem;
import cia.arkrypto.se.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.List;
import java.util.Map;

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



    boolean flag;
    Element left, right;
    @Override
    public boolean search(){
        System.out.println("T: " + T);
        left = T.mul(pairing(C2, g)).getImmutable();
        right = pairing(C1, pk_r).getImmutable();
        System.out.println("PAEKS left: " + left);
        System.out.println("PAEKS right: " + right);
        flag = left.isEqual(right);
        return flag;
    }


    @Override
    public Map<String, Object> test(String word, List<String> words, int round) {
        Map<String, Object> data = super.test(word, words, round);
        data.put("flag", flag);
        data.put("left", left);
        data.put("right", right);
        return data;
    }
}

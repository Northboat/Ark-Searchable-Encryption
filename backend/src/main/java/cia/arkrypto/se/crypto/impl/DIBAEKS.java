package cia.arkrypto.se.crypto.impl;

import cia.arkrypto.se.crypto.CipherSystem;
import cia.arkrypto.se.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class DIBAEKS extends CipherSystem {

    public DIBAEKS(Field G, Field GT, Field Zr, Pairing bp, int n){
        super(G, GT, Zr, bp, n);
    }


    private Element g, h, sk_svr, id_r, id_s, a;
    @Override
    public void setup(){
        g = this.getG().newRandomElement().getImmutable();
        h = this.getG().newRandomElement().getImmutable();

        sk_svr = this.getZr().newRandomElement().getImmutable();

        id_r = this.getZr().newRandomElement().getImmutable();
        id_s = this.getZr().newRandomElement().getImmutable();
        a = this.getZr().newRandomElement().getImmutable();
    }

    public Element pk_svr;
    @Override
    public void keygen(){
        pk_svr = g.powZn(sk_svr).getImmutable();
    }

    public Element C1, C2, C3;
    @Override
    public void enc(String str){
        Element[] w = h(str);
        Element sk_id = HashUtil.hashZr2G(g, id_s).powZn(a).getImmutable();
        Element k = this.getBp().pairing(sk_id, HashUtil.hashZr2G(g, id_r));

        Element s = this.getZr().newRandomElement().getImmutable();
        C1 = this.getBp().pairing(HashUtil.hashGT2GWithZrArr(this.getG(), k, w), pk_svr.powZn(s)).getImmutable();
        C2 = g.powZn(s).getImmutable();
        C3 = h.powZn(s).getImmutable();
    }

    public Element T1, T2;
    @Override
    public void trap(String str){
        Element[] w = h(str);
        Element sk_id = HashUtil.hashZr2G(g, id_r).powZn(a).getImmutable();
        Element k = this.getBp().pairing(HashUtil.hashZr2G(g, id_s), sk_id);

        Element r = this.getZr().newRandomElement().getImmutable();
        T1 = HashUtil.hashGT2GWithZrArr(this.getG(), k, w).mul(h.powZn(r)).getImmutable();
        T2 = g.powZn(r).getImmutable();
    }

    public boolean search(){
        Element p = this.getBp().pairing(T2.powZn(sk_svr), C3).getImmutable();
        Element left = C1.mul(p).getImmutable();
        Element right = this.getBp().pairing(T1.powZn(sk_svr), C2);
        System.out.println("dIBAEKS left: " + left);
        System.out.println("dIBAEKS right: " + right);
        return left.isEqual(right);
    }
}

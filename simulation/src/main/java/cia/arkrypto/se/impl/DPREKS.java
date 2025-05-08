package cia.arkrypto.se.impl;

import cia.arkrypto.se.CipherSystem;
import cia.arkrypto.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class DPREKS extends CipherSystem {

    public DPREKS(Field G, Field GT, Field Zr, Pairing bp, int n) {
        super(G, GT, Zr, bp, n);
    }

    public Element H(Element z){
        return HashUtil.hashZr2G(g, z);
    }

    public Element H1(Element gt){
        return HashUtil.hashGT2Zr(getZr(), gt);
    }

    public Element H2(String str){
        return HashUtil.hashZrArr2Zr(getZr(), h(str));
    }

    Element g, sk_kgc, sk_svr, pk_svr;
    @Override
    public void setup() {
        g = randomG();
        sk_kgc = randomZ(); // x
        sk_svr = randomZ();
        pk_svr = g.powZn(sk_svr).getImmutable();

    }


    Element ID_o, sk_co, sk_ao, V, pk_co, pk_ao;
    Element ID_u, sk_tu, sk_au, pk_tu, pk_au;
    @Override
    public void keygen() {
        ID_o = randomZ();
        sk_co = randomZ(); // s
        sk_ao = H(ID_o).powZn(sk_kgc).getImmutable();
        V = randomG();
        pk_co = g.powZn(sk_co).getImmutable();
        pk_ao = H(ID_o);

        ID_u = randomZ();
        sk_tu = randomZ(); // t
        sk_au = H(ID_u).powZn(sk_kgc).getImmutable();
        pk_tu = g.powZn(sk_tu).getImmutable();
        pk_au = H(ID_u);
    }



    Element C_w1, C_w2, C_o;
    @Override
    public void enc(String w) {
        Element r = randomZ();
        Element T = pairing(pk_svr, V).powZn(sk_co).getImmutable();

        C_w1 = pk_co.powZn(r).getImmutable();
        C_w2 = pairing(g, g.powZn(H2(w))).powZn(r).mul(T).getImmutable();
        C_o = H1(pairing(H(ID_o).powZn(sk_kgc), H(ID_u)));
    }


    Element T_q1, T_q2, C_u;
    @Override
    public void trap(String q) {
        Element k = randomZ();
        T_q1 = g.powZn(H2(q)).powZn(sk_tu.invert()).mul(pk_svr.powZn(k)).getImmutable();
        T_q2 = g.powZn(k).getImmutable();
        C_u = H1(pairing(H(ID_o), H(ID_u).powZn(sk_kgc)));
    }

    @Override
    public void reEnc(){
        C_w1 = C_w1.powZn(sk_tu.div(sk_co)).getImmutable();
    }

    @Override
    public boolean search() {
        reEnc();

        Element T = pairing(V, pk_co).powZn(sk_svr).getImmutable();
        Element left = pairing(C_w1, T_q1.div(T_q2.powZn(sk_svr))).mul(T).getImmutable();
        Element right = C_w2.getImmutable();

        System.out.println("left: " + left);
        System.out.println("right: " + right);
        return left.isEqual(right);
    }
}

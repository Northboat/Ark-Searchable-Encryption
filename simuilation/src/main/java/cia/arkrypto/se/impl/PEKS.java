package cia.arkrypto.se.impl;

import cia.arkrypto.se.CipherSystem;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class PEKS extends CipherSystem {

    public PEKS(Field G, Field GT, Field Zr, Pairing bp, int n){
        super(G, GT, Zr, bp, n);
    }


    Element g;
    @Override
    public void setup(){
        g = randomG();
    }

    Element g1, g2, s, r, gs, gr;
    @Override
    public void keygen(){
        g1 = g.powZn(randomZ()).getImmutable();
        g2 = g.powZn(randomZ()).getImmutable();
        s = randomZ();
        r = randomZ();
        gs = g.powZn(s).getImmutable();
        gr = g.powZn(r).getImmutable();
    }



    Element[] C, B, D;
    @Override
    public void enc(String word){
        B = new Element[3];
        C = new Element[3];
        D = new Element[3];

        Element s1 = randomZ();
        B[0] = g2.powZn(s1.mul(getI(1))).mul(gr.powZn(s1)).getImmutable();
        B[1] = g2.powZn(s1.mul(getI(2))).mul(gr.powZn(s1)).getImmutable();
        B[2] = g2.powZn(s1.mul(getI(4))).mul(gr.powZn(s1)).getImmutable();

        C[0] = g.powZn(s1).getImmutable();
        C[1] = g2.powZn(s1.mul(s).mul(getI(7))).getImmutable();
        C[2] = g2.powZn(s1.mul(s).mul(getI(13))).getImmutable();

        D[0] = g2.powZn(s1.mul(getI(1))).mul(gr.powZn(s1)).getImmutable();
        D[1] = g2.powZn(s1.mul(getI(3))).mul(gr.powZn(s1)).getImmutable();
        D[2] = g2.powZn(s1.mul(getI(9))).mul(gr.powZn(s1)).getImmutable();

    }

    Element[] T;
    Element[][] E;
    @Override
    public void trap(String word){
        E = new Element[2][3];
        T = new Element[4];

        Element r1 = randomZ();
        E[0][0] = g1.powZn(r1.mul(getI(6))).mul(gs.powZn(r1)).getImmutable();
        E[0][1] = g1.powZn(r1.mul(getI(-5))).mul(gs.powZn(r1)).getImmutable();
        E[0][2] = g1.powZn(r1.mul(getI(1))).mul(gs.powZn(r1)).getImmutable();
        E[1][0] = g1.powZn(r1.mul(getI(12))).mul(gs.powZn(r1)).getImmutable();
        E[1][1] = g1.powZn(r1.mul(getI(-7))).mul(gs.powZn(r1)).getImmutable();
        E[1][2] = g1.powZn(r1.mul(getI(1))).mul(gs.powZn(r1)).getImmutable();

        T[0] = g1.powZn(r1.mul(getI(2)).mul(r)).getImmutable();
        T[1] = g.powZn(r1).getImmutable();
        T[2] = gs.powZn(r1.mul(getI(3).mul(r))).getImmutable();
        T[3] =g1.powZn(r1.mul(r).mul(getI(6))).getImmutable();
    }


    @Override
    public boolean search(){
        Element z1 = pairing(B[0], E[0][0]).mul(pairing(B[1], E[0][1])).mul(pairing(B[2], E[0][2])).getImmutable();
        Element z2 = pairing(C[0], T[0]).mul(pairing(C[1], T[1])).mul(pairing(C[0], T[2])).getImmutable();

        Element tau1 = pairing(D[0], E[1][0]).mul(pairing(D[1], E[1][1])).mul(pairing(D[2], E[1][2])).getImmutable();
        Element tau2 = pairing(C[0], T[3]).mul(pairing(C[2], T[1])).mul(pairing(C[0], T[2])).getImmutable();

        System.out.println("z1: " + z1);
        System.out.println("z2: " + z2);
        System.out.println("tau1: " + tau1);
        System.out.println("tau2: " + tau2);

        return z1.isEqual(z2) && tau1.isEqual(tau2);
    }
}

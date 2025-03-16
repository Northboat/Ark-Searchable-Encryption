package cia.northboat.se;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public abstract class CipherSystem implements SearchableEncryption{
    Field G, GT, Zr;
    Pairing bp;
    int n;

    public CipherSystem(Field G, Field GT, Field Zr, Pairing bp, int n) {
        this.G = G;
        this.GT = GT;
        this.Zr = Zr;
        this.bp = bp;
        this.n = n;
    }

    public Field getG(){
        return G;
    }

    public Field getGT() {
        return GT;
    }

    public Field getZr() {
        return Zr;
    }

    public int getN() {
        return n;
    }

    public Pairing getBp() {
        return bp;
    }

}

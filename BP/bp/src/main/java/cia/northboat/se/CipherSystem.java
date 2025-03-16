package cia.northboat.se;

import cia.northboat.util.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public abstract class CipherSystem implements SearchableEncryption{
    Field G, GT, Zr;
    Pairing bp;
    int n;
    boolean updatable;

    public CipherSystem(Field G, Field GT, Field Zr, Pairing bp, int n) {
        this.G = G;
        this.GT = GT;
        this.Zr = Zr;
        this.bp = bp;
        this.n = n;
        this.updatable = false;
    }

    public CipherSystem(Field G, Field GT, Field Zr, Pairing bp, int n, boolean updatable) {
        this.G = G;
        this.GT = GT;
        this.Zr = Zr;
        this.bp = bp;
        this.n = n;
        this.updatable = updatable;
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
    public boolean getUpdatable(){
        return updatable;
    }

    public Element[] h(String str){
        return HashUtil.hashStr2ZrArr(Zr, str, n);
    }

    public Element randomZ(){
        return Zr.newRandomElement().getImmutable();
    }

    public Element randomG(){
        return G.newRandomElement().getImmutable();
    }

    public Element randomGT(){
        return GT.newRandomElement().getImmutable();
    }

    public Element pairing(Element u, Element v){
        return bp.pairing(u, v).getImmutable();
    }
}

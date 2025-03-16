package cia.northboat.se.impl;

import cia.northboat.se.CipherSystem;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.List;

public class AP extends CipherSystem {
    public AP(Field G, Field GT, Field Zr, Pairing bp, int n){
        super(G, GT, Zr, bp, n);
    }


    @Override
    public void setup() {

    }

    @Override
    public void keygen() {

    }

    @Override
    public void trap(List<String> W) {
        super.trap(W);
    }

    @Override
    public void enc(List<String> W) {
        super.enc(W);
    }

    @Override
    public boolean search() {
        return false;
    }
}

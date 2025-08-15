package cia.arkrypto.se.model.dto;

import lombok.Data;

@Data
public class KeyPair {
    public CryptoMap sk;
    public CryptoMap pk;

    public KeyPair(){
        sk = new CryptoMap();
        pk = new CryptoMap();
    }

    public String toString(){
        return pk.toString() + "\n" + sk.toString();
    }
}

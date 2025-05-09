package cia.arkrypto.se.service;

import cia.arkrypto.se.crypto.CipherSystem;
import cia.arkrypto.se.crypto.impl.AP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CryptoService {
    private final CipherSystem ap;
    @Autowired
    public CryptoService(AP ap){
        this.ap = ap;
    }


    public Map<String, Object> test(String algo, List<String> words, int round){
        if(algo.equalsIgnoreCase("ap")){
            ap.test(words, round);
            return null;
        }
        return null;
    }
}

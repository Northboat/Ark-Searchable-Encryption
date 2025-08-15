package cia.arkrypto.se.service;


import cia.arkrypto.se.crypto.sign.CipherSystem;
import cia.arkrypto.se.crypto.sign.impl.Elgamal;
import cia.arkrypto.se.crypto.sign.impl.RSA;
import cia.arkrypto.se.model.dto.KeyPair;
import cia.arkrypto.se.crypto.sign.impl.rfid.Schnorr;
import cia.arkrypto.se.model.dto.CryptoMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CipherSystem schnorr_rfid, rsa, schnorr, elgamal;
    @Autowired
    public AuthService(Schnorr schnorr_rfid, RSA rsa, cia.arkrypto.se.crypto.sign.impl.Schnorr schnorr, Elgamal elgamal){
        this.schnorr_rfid = schnorr_rfid;
        this.rsa = rsa;
        this.schnorr = schnorr;
        this.elgamal = elgamal;
    }


    public CipherSystem selectSystem(String algo){
        if (algo.equalsIgnoreCase("schnorr")){
            return schnorr;
        } else if (algo.equalsIgnoreCase("rsa")){
            return rsa;
        } else if (algo.equalsIgnoreCase("elgamal")){
            return elgamal;
        } else if (algo.equalsIgnoreCase("schnorr_rfid")){
            return schnorr_rfid;
        }
        return null;
    }


    public KeyPair keygen(String algo){
        CipherSystem cipherSystem = selectSystem(algo);
        if(cipherSystem == null){
            return null;
        }
        return cipherSystem.keygen();
    }


    public CryptoMap sign(String algo, String message, CryptoMap sk){
        CipherSystem cipherSystem = selectSystem(algo);
        if(cipherSystem == null){
            return null;
        }
        return cipherSystem.sign(message, sk);
    }

    public Boolean verify(String algo, CryptoMap pk, CryptoMap signature){
        CipherSystem cipherSystem = selectSystem(algo);
        if(cipherSystem == null){
            return null;
        }
        return cipherSystem.verify(pk, signature);
    }
}

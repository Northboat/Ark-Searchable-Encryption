package cia.arkrypto.se.service;


import cia.arkrypto.se.crypto.sign.SignatureSystem;
import cia.arkrypto.se.crypto.sign.impl.Elgamal;
import cia.arkrypto.se.crypto.sign.impl.RSA;
import cia.arkrypto.se.model.dto.KeyPair;
import cia.arkrypto.se.crypto.sign.impl.Schnorr;
import cia.arkrypto.se.model.dto.CryptoMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SignatureSystem rsa, schnorr, elgamal;
    @Autowired
    public AuthService(RSA rsa, Schnorr schnorr, Elgamal elgamal){
        this.rsa = rsa;
        this.schnorr = schnorr;
        this.elgamal = elgamal;
    }


    public SignatureSystem selectSystem(String algo){
        if (algo.equalsIgnoreCase("schnorr")){
            return schnorr;
        } else if (algo.equalsIgnoreCase("rsa")){
            return rsa;
        } else if (algo.equalsIgnoreCase("elgamal")){
            return elgamal;
        }
        return null;
    }


    public KeyPair keygen(String algo){
        SignatureSystem signatureSystem = selectSystem(algo);
        if(signatureSystem == null){
            return null;
        }
        return signatureSystem.keygen();
    }


    public CryptoMap sign(String algo, String message, CryptoMap sk){
        SignatureSystem signatureSystem = selectSystem(algo);
        if(signatureSystem == null){
            return null;
        }
        return signatureSystem.sign(message, sk);
    }

    public Boolean verify(String algo, CryptoMap pk, CryptoMap signature){
        SignatureSystem signatureSystem = selectSystem(algo);
        if(signatureSystem == null){
            return null;
        }
        return signatureSystem.verify(pk, signature);
    }
}

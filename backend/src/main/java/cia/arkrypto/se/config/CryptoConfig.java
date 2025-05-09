package cia.arkrypto.se.config;


import cia.arkrypto.se.crypto.impl.AP;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

    @Bean
    public Pairing pairing() {
        return PairingFactory.getPairing("a.properties");
    }

    @Bean
    public Field G1(Pairing pairing) {
        return pairing.getG1();
    }

    @Bean
    public Field G2(Pairing pairing) {
        return pairing.getG2();
    }

    @Bean
    public Field GT(Pairing pairing) {
        return pairing.getGT();
    }

    @Bean
    public Field Zr(Pairing pairing) {
        return pairing.getZr();
    }

    @Bean
    public int n(){
        return 26;
    }

    @Bean
    public AP ap(Field G1, Field GT, Field Zr, Pairing bp, int n, Field G2){
        return new AP(G1, GT, Zr, bp, n, G2);
    }
}
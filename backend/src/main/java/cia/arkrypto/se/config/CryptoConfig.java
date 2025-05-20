package cia.arkrypto.se.config;


import ch.qos.logback.core.joran.sanity.Pair;
import cia.arkrypto.se.crypto.EncryptedQuadtree;
import cia.arkrypto.se.crypto.RangedSEArchetype;
import cia.arkrypto.se.crypto.SimpleMiner;
import cia.arkrypto.se.crypto.impl.*;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.parameters.P;

@Configuration
public class CryptoConfig {

    private static final int n = 26;
    private static final int q = 1024;
    private static final int k = 3;
    private static final int l = 4;

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
    public RangedSEArchetype rangedSEArchetype(Field G1, Field GT, Field Zr, Pairing bp){
        return new RangedSEArchetype(G1, GT, Zr, bp);
    }

    @Bean
    public EncryptedQuadtree encryptedQuadtree(Field G1, Field Zr){
        return new EncryptedQuadtree(G1, Zr, n);
    }

    @Bean
    public SimpleMiner simpleMiner(){
        return new SimpleMiner();
    }

    @Bean
    public AP ap(Field G1, Field GT, Field Zr, Pairing bp, Field G2){
        return new AP(G1, GT, Zr, bp, n, G2);
    }

    @Bean
    public CRIMA crima(Field G1, Field GT, Field Zr, Pairing bp){
        return new CRIMA(G1, GT, Zr, bp, n);
    }

    @Bean
    public DPREKS dpreks(Field G1, Field GT, Field Zr, Pairing bp){
        return new DPREKS(G1, GT, Zr, bp, n);
    }

    @Bean
    public DIBAEKS dibaeks(Field G1, Field GT, Field Zr, Pairing bp){
        return new DIBAEKS(G1, GT, Zr, bp, n);
    }

    @Bean
    public DuMSE dumse(Field G1, Field GT, Field Zr, Pairing bp){
        return new DuMSE(G1, GT, Zr, bp, n, q);
    }

    @Bean
    public FIPECK fipeck(Field G1, Field GT, Field Zr, Pairing bp){
        return new FIPECK(G1, GT, Zr, bp, n);
    }

    @Bean
    public Gu2CKS gu2cks(Field G1, Field GT, Field Zr, Pairing bp){
        return new Gu2CKS(G1, GT, Zr, bp, n, l);
    }

    @Bean
    public PAEKS paeks(Field G1, Field GT, Field Zr, Pairing bp){
        return new PAEKS(G1, GT, Zr, bp, n);
    }

    @Bean
    public PAUKS pauks(Field G1, Field GT, Field Zr, Pairing bp){
        return new PAUKS(G1, GT, Zr, bp, n);
    }

    @Bean
    public PECKS pecks(Field G1, Field GT, Field Zr, Pairing bp){
        return new PECKS(G1, GT, Zr, bp, n);
    }

    @Bean
    public PEKS peks(Field G1, Field GT, Field Zr, Pairing bp){
        return new PEKS(G1, GT, Zr, bp, n);
    }

    @Bean
    public PMatch pmatch(Field G1, Field GT, Field Zr, Pairing bp){
        return new PMatch(G1, GT, Zr, bp, n);
    }

    @Bean
    public PREKS preks(Field G1, Field GT, Field Zr, Pairing bp){
        return new PREKS(G1, GT, Zr, bp, n);
    }

    @Bean
    public SAPAUKS sapauks(Field G1, Field GT, Field Zr, Pairing bp){
        return new SAPAUKS(G1, GT, Zr, bp, n);
    }

    @Bean
    public SCF scf(Field G1, Field GT, Field Zr, Pairing bp){
        return new SCF(G1, GT, Zr, bp, n);
    }

    @Bean
    public SPWSE1 spwse1(Field G1, Field GT, Field Zr, Pairing bp, Field G2){
        return new SPWSE1(G1, GT, Zr, bp, n, G2);
    }


    @Bean
    public SPWSE2 spwse2(Field G1, Field GT, Field Zr, Pairing bp){
        return new SPWSE2(G1, GT, Zr, bp, n);
    }


    @Bean
    public TBEKS tbeks(Field G1, Field GT, Field Zr, Pairing bp){
        return new TBEKS(G1, GT, Zr, bp, n, l);
    }

    @Bean
    public TMS tms(Field G1, Field GT, Field Zr, Pairing bp){
        return new TMS(G1, GT, Zr, bp, n, l);
    }

    @Bean
    public Tu2CKS tu2cks(Field G1, Field GT, Field Zr, Pairing bp){
        return new Tu2CKS(G1, GT, Zr, bp, n, l);
    }

    @Bean
    public TuCR tucr(Field G1, Field GT, Field Zr, Pairing bp){
        return new TuCR(G1, GT, Zr, bp, n);
    }


}
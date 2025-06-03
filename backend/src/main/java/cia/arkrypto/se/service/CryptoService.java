package cia.arkrypto.se.service;

import cia.arkrypto.se.crypto.sim.CipherSystem;
import cia.arkrypto.se.crypto.EncryptedQuadtree;
import cia.arkrypto.se.crypto.RangedSearchArchetype;
import cia.arkrypto.se.crypto.SimpleMiner;
import cia.arkrypto.se.crypto.sim.impl.*;
import cia.arkrypto.se.ds.Point;
import cia.arkrypto.se.ds.QuadtreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CryptoService {

    private final CipherSystem ap, crima, dibaeks, dpreks, dumse, fipeck, gu2cks, paeks, pauks, pecks, peks, pmatch, preks, sapauks, scf, spwse1, spwse2, tbeks, tms, tu2cks, tucr;
    private final RangedSearchArchetype rangedSearchArchetype;
    private final EncryptedQuadtree encryptedQuadtree;
    private final SimpleMiner simpleMiner;
    @Autowired
    public CryptoService(AP ap, CRIMA crima, DIBAEKS dibaeks, DPREKS dpreks, DuMSE dumse, FIPECK fipeck,
                         Gu2CKS gu2cks, PAEKS paeks, PAUKS pauks, PECKS pecks, PEKS peks, PMatch pmatch,
                         PREKS preks, SAPAUKS sapauks, SCF scf, SPWSE1 spwse1, SPWSE2 spwse2, TBEKS tbeks,
                         TMS tms, Tu2CKS tu2cks, TuCR tucr, RangedSearchArchetype rangedSearchArchetype,
                         EncryptedQuadtree encryptedQuadtree, SimpleMiner simpleMiner){
        this.ap = ap;
        this.crima = crima;
        this.dibaeks = dibaeks;
        this.dpreks = dpreks;
        this.dumse = dumse;
        this.fipeck = fipeck;
        this.gu2cks = gu2cks;
        this.paeks = paeks;
        this.pauks = pauks;
        this.pecks = pecks;
        this.peks = peks;
        this.pmatch = pmatch;
        this.preks = preks;
        this.sapauks = sapauks;
        this.scf = scf;
        this.spwse1 = spwse1;
        this.spwse2 = spwse2;
        this.tbeks = tbeks;
        this.tms = tms;
        this.tu2cks = tu2cks;
        this.tucr = tucr;
        this.rangedSearchArchetype = rangedSearchArchetype;
        this.encryptedQuadtree = encryptedQuadtree;
        this.simpleMiner = simpleMiner;
    }


    public Map<String, Object> test(String algo, String word, List<String> words, int round){
        if(algo.equalsIgnoreCase("ap")){
            return ap.test(word, words, round);
        } else if(algo.equalsIgnoreCase("crima")){
            return crima.test(word, words, round);
        } else if(algo.equalsIgnoreCase("dibaeks")){
            return dibaeks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("dpreks")){
            return dpreks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("dumse")){
            return dumse.test(word, words, round);
        } else if(algo.equalsIgnoreCase("fipeck")){
            return fipeck.test(word, words, round);
        } else if(algo.equalsIgnoreCase("gu2cks")){
            return gu2cks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("paeks")){
            return paeks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("pauks")){
            return pauks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("pecks")){
            return pecks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("peks")){
            return peks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("pmatch")){
            return pmatch.test(word, words, round);
        } else if(algo.equalsIgnoreCase("preks")){
            return preks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("sapauks")){
            return sapauks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("scf")){
            return scf.test(word, words, round);
        } else if(algo.equalsIgnoreCase("spwse1")){
            return spwse1.test(word, words, round);
        } else if(algo.equalsIgnoreCase("spwse2")){
            return spwse2.test(word, words, round);
        } else if(algo.equalsIgnoreCase("tbeks")){
            return tbeks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("tms")){
            return tms.test(word, words, round);
        } else if(algo.equalsIgnoreCase("tu2cks")){
            return tu2cks.test(word, words, round);
        } else if(algo.equalsIgnoreCase("tucr")){
            return tucr.test(word, words, round);
        }
        return Map.of("msg", "Algo Not Exists");
    }

    public Map<String, Object> params(){
        return rangedSearchArchetype.getSystemParams();
    }

    public Map<String, Object> auth(){
        long s = System.currentTimeMillis();
        Map<String, Object> data = rangedSearchArchetype.mutualAuth();
        long e = System.currentTimeMillis();
        data.put("time_cost", e-s);
        return data;
    }

    public Map<String, Object> buildMatrix(){
        long s = System.currentTimeMillis();
        Map<String, Object> data = rangedSearchArchetype.buildMatrix();
        long e = System.currentTimeMillis();
        data.put("time_cost", e-s);
        return data;
    }

    public Map<String, Object> query(){
        long s = System.currentTimeMillis();
        Map<String, Object> data = rangedSearchArchetype.search();
        long e = System.currentTimeMillis();
        data.put("time_cost", e-s);
        return data;
    }


    public Map<String, Object> buildTree(int count, int type){
        Map<String, Object> data = new HashMap<>();

        long s = System.currentTimeMillis();
        encryptedQuadtree.clean();
        encryptedQuadtree.build(count);
        long e = System.currentTimeMillis();
        data.put("time_cost", e-s);



        String treeStr = encryptedQuadtree.getTreeStruct(type);
        String htmlTreeStr = treeStr.replace("\n", "<br>");
        htmlTreeStr = htmlTreeStr.replace(" ", "&nbsp;");
        data.put("tree", htmlTreeStr);

        System.out.println("Tree Height: " + encryptedQuadtree.getHeight());
        data.put("height", encryptedQuadtree.getHeight());

        return data;
    }

    public Map<String, Object> mine(int difficulty){
        return simpleMiner.mine(difficulty);
    }


    public Map<String, Object> search(Point p){
        Map<String, Object> data = new HashMap<>();

        if(encryptedQuadtree.getHeight() == 1){
            data.put("Error", "Please build tree first");
            return data;
        }

        long s = System.currentTimeMillis();
        // 不知道怎么匹配，怎么样都不匹配
        QuadtreeNode target = encryptedQuadtree.search(p);
        long e = System.currentTimeMillis();

        data.put("time_cost", e-s);
        data.put("target_node", target == null ? "null" : target);

        data.put("Error", "Search haven't finished yet. Nothing seems to match here. I suspect there's an issue with the search logic. I'll fix it later when I have time. Maybe the matching formula is also problematic");

        return data;
    }
}

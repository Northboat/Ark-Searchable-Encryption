package cia.arkrypto.se.service;

import cia.arkrypto.se.crypto.bm25.RangedSearchArchetype;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RangedSearchService {

    private final RangedSearchArchetype rangedSearchArchetype;

    public RangedSearchService(RangedSearchArchetype rangedSearchArchetype){
        this.rangedSearchArchetype = rangedSearchArchetype;
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

}

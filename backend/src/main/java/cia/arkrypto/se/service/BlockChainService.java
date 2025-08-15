package cia.arkrypto.se.service;

import cia.arkrypto.se.crypto.miner.SimpleMiner;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BlockChainService {
    private final SimpleMiner simpleMiner;

    public BlockChainService(SimpleMiner simpleMiner){
        this.simpleMiner = simpleMiner;
    }


    public Map<String, Object> mine(int difficulty){
        return simpleMiner.mine(difficulty);
    }
}

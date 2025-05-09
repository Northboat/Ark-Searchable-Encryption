package cia.arkrypto.se.controller;

import cia.arkrypto.se.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CryptoController {
    private final CryptoService cryptoService;
    @Autowired
    public CryptoController(CryptoService cryptoService){
        this.cryptoService = cryptoService;
    }
}

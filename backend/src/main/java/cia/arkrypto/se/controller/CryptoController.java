package cia.arkrypto.se.controller;

import cia.arkrypto.se.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class CryptoController {
    private final CryptoService cryptoService;
    @Autowired
    public CryptoController(CryptoService cryptoService){
        this.cryptoService = cryptoService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
//    @ResponseBody → return Map<String, Object> data
    public String test(@RequestParam Map<String, String> params, Model model) {
        String algo = params.get("algo");
        String word = params.get("word");

        List<String> words = Collections.singletonList(word);

        int round = Integer.parseInt(params.get("round"));
        Map<String, Object> data = cryptoService.test(algo, word, words, round);


        model.addAttribute("algo", algo);
        model.addAttribute("data", data);
        model.addAttribute("word", word);
        model.addAttribute("round", Integer.toString(round));
        return "simulation";
    }
}

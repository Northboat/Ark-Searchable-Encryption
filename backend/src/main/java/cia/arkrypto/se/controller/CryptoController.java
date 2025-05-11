package cia.arkrypto.se.controller;

import cia.arkrypto.se.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        data.put("msg", "如果是多轮测试，由于每轮除了系统变量，其余都是随机选取，所以返回的变量将默认是最后一轮的");


        model.addAttribute("algo", algo);
        model.addAttribute("data", data);
        model.addAttribute("word", word);
        model.addAttribute("round", Integer.toString(round));
        return "simulation";
    }

    @GetMapping("/arch")
    public String arch(Model model) {
        model.addAttribute("params", cryptoService.params());
        return "archetype";  // 返回 templates/login.html 页面
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String auth(Model model) {
        model.addAttribute("params", cryptoService.params());
        model.addAttribute("data", cryptoService.auth());
        return "archetype";
    }

    @RequestMapping(value = "/buildMatrix", method = RequestMethod.GET)
    public String buildMatrix(Model model) {
        model.addAttribute("params", cryptoService.params());
        model.addAttribute("data", cryptoService.buildMatrix());
        return "archetype";
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(Model model) {
        model.addAttribute("params", cryptoService.params());
        model.addAttribute("data", cryptoService.query());
        return "archetype";
    }

    @RequestMapping(value = "/buildTree", method = RequestMethod.GET)
    public String buildTree(Model model) {
        model.addAttribute("data", Map.of("msg", "haven't write"));
        return "tree";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(Model model) {
        model.addAttribute("data", Map.of("msg", "haven't write"));
        return "tree";
    }

}

package cia.arkrypto.se.controller;

import cia.arkrypto.se.ds.Point;
import cia.arkrypto.se.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
        data.put("Msg", "If round > 1, the params showing on the page are the last round's");


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


    @RequestMapping(value = "/buildTree", method = RequestMethod.POST)
    public String buildTree(@RequestParam String count, @RequestParam String type, Model model) {
        int numPoints = Integer.parseInt(count);
        int numType = Integer.parseInt(type);
        model.addAttribute("data", cryptoService.buildTree(numPoints, numType));
        model.addAttribute("count", count);
        model.addAttribute("type", type);
        return "tree";
    }


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam String x, @RequestParam String y, Model model) {
        model.addAttribute("x", x);
        model.addAttribute("y", y);

        Point p;
        try{
            p = new Point(Integer.parseInt(x), Integer.parseInt(y));
        }catch (NumberFormatException e){
            model.addAttribute("data", Map.of("Error", "NumberFormatException"));
            return "tree";
        }
        model.addAttribute("data", cryptoService.search(p));

        return "tree";
    }

}

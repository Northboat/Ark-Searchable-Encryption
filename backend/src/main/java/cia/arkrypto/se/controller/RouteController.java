package cia.arkrypto.se.controller;


import cia.arkrypto.se.crypto.RangedSEArchetype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RouteController {

    @GetMapping({"/home", "/"})
    public String home(Model model) {
        return "index";  // 返回 templates/index.html 页面
    }

    @GetMapping("/sim")
    public String sim() {
        return "simulation";  // 返回 templates/login.html 页面
    }

    @GetMapping("/tree")
    public String tree() {
        return "tree";  // 返回 templates/login.html 页面
    }


    @RequestMapping(value = "/sim/{algo}", method = RequestMethod.GET)
    public String sim(@PathVariable("algo") String algo, Model model) {
//        System.out.println(algo);
        model.addAttribute("algo", algo);
        return "simulation";
    }
}

package cia.arkrypto.se.controller;


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

    @GetMapping("/pairing")
    public String pairing() {
        return "pairing";  // 返回 templates/login.html 页面
    }

    @GetMapping("/tree")
    public String tree() {
        return "tree";  // 返回 templates/login.html 页面
    }


    @RequestMapping(value = "/pairing/{algo}", method = RequestMethod.GET)
    public String sim(@PathVariable("algo") String algo, Model model) {
        model.addAttribute("algo", algo);
        return "pairing";
    }

    @GetMapping("/signature")
    public String signature() {
        return "sign";  // 返回 templates/login.html 页面
    }
}

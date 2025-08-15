package cia.arkrypto.se.controller;

import cia.arkrypto.se.model.bo.Point;
import cia.arkrypto.se.model.dto.CryptoMap;
import cia.arkrypto.se.model.dto.KeyPair;
import cia.arkrypto.se.service.*;
import cia.arkrypto.se.utils.ResultCode;
import cia.arkrypto.se.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class CryptoController {
    private final AuthService authService;
    private final BlockChainService blockChainService;
    private final IPFETreeService ipfeTreeService;
    private final PairingService pairingService;
    private final RangedSearchService rangedSearchService;
    @Autowired
    public CryptoController(AuthService authService, BlockChainService blockChainService,
                            IPFETreeService ipfeTreeService, PairingService pairingService,
                            RangedSearchService rangedSearchService){
        this.authService = authService;
        this.blockChainService = blockChainService;
        this.ipfeTreeService = ipfeTreeService;
        this.pairingService = pairingService;
        this.rangedSearchService = rangedSearchService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
//    @ResponseBody → return Map<String, Object> data
    public String test(@RequestParam Map<String, String> params, Model model) {
        String algo = params.get("algo");
        String word = params.get("word");

        List<String> words = Collections.singletonList(word);

        int round = Integer.parseInt(params.get("round"));
        Map<String, Object> data = pairingService.test(algo, word, words, round);
        data.put("Msg", "If round > 1, the params showing on the page are the last round's");


        model.addAttribute("algo", algo);
        model.addAttribute("data", data);
        model.addAttribute("word", word);
        model.addAttribute("round", Integer.toString(round));
        return "simulation";
    }

    @GetMapping("/arch")
    public String arch(Model model) {
        model.addAttribute("params", rangedSearchService.params());
        return "archetype";  // 返回 templates/login.html 页面
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String auth(Model model) {
        model.addAttribute("params", rangedSearchService.params());
        model.addAttribute("data", rangedSearchService.auth());
        return "archetype";
    }

    @RequestMapping(value = "/buildMatrix", method = RequestMethod.GET)
    public String buildMatrix(Model model) {
        model.addAttribute("params", rangedSearchService.params());
        model.addAttribute("data", rangedSearchService.buildMatrix());
        return "archetype";
    }


    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(Model model) {
        model.addAttribute("params", rangedSearchService.params());
        model.addAttribute("data", rangedSearchService.query());
        return "archetype";
    }


    @RequestMapping(value = "/buildTree", method = RequestMethod.POST)
    public String buildTree(@RequestParam String count, @RequestParam String type, Model model) {
        int numPoints = Integer.parseInt(count);
        int numType = Integer.parseInt(type);
        model.addAttribute("data", ipfeTreeService.buildTree(numPoints, numType));
        model.addAttribute("count", count);
        model.addAttribute("type", type);
        return "tree";
    }


    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    public String mine(Model model) {
        int difficulty = 5;
        model.addAttribute("data", blockChainService.mine(difficulty));
        return "auth";
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
        model.addAttribute("data", ipfeTreeService.search(p));

        return "tree";
    }

    @GetMapping("/signer")
    public String signer(@RequestParam("algo") String algo, Model model) {
        KeyPair keyPair = authService.keygen(algo);
        if(Objects.isNull(keyPair)){
            model.addAttribute("result", Map.of("error", "algo invalid"));
            return "auth";
        }

        CryptoMap signature = authService.sign(algo, "test", keyPair.sk);
        Boolean flag = authService.verify(algo, keyPair.pk, signature);

        model.addAttribute("algo", algo);
        model.addAttribute("data", Map.of(
                "pk", keyPair.pk,
                "sk", keyPair.sk,
                "signature", signature,
                "flag", flag
        ));

        return "auth";
    }

    // 密钥生成
    @RequestMapping("/keygen")
    @ResponseBody
    public ResultUtil keygen(@RequestParam Map<String, String> params){
        String algo = params.get("algo");
        KeyPair keyPair = authService.keygen(algo);
        if(keyPair == null){
            return ResultUtil.failure(ResultCode.PARAM_IS_INVALID);
        }
        return ResultUtil.success(keyPair);
    }

    // 签名
    @RequestMapping("/sign")
    @ResponseBody
    public ResultUtil sign(@RequestParam Map<String, Object> params){
        String algo = (String)params.get("algo");
        String message = (String)params.get("message");
        CryptoMap sk = (CryptoMap) params.get("sk");

        CryptoMap signature = authService.sign(algo, message, sk);
        if(signature == null){
            return ResultUtil.failure(ResultCode.INTERNAL_SERVER_ERROR);
        }

        return ResultUtil.success(signature);
    }


    // 认证
    @RequestMapping("/verify")
    @ResponseBody
    public ResultUtil verify(@RequestParam Map<String, Object> params){
        String algo = (String)params.get("algo");
        CryptoMap pk = (CryptoMap)params.get("pk");
        CryptoMap signature = (CryptoMap)params.get("signature");


        return ResultUtil.success(authService.verify(algo, pk, signature));
    }


}

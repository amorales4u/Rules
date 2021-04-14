package dev.c20.rules.engine;

import dev.c20.rules.engine.services.RuleRequest;
import dev.c20.rules.engine.services.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RuleRestController {

    @Autowired
    RuleService ruleService;

    @GetMapping("/version")
    public String version() {
        return "2021-04-13 18:50";
    }

    @PostMapping("/test")
    public List<String> evalBusinessRule(@RequestBody RuleRequest request) {
        return ruleService.evalBusinessRule(request);
    }
}

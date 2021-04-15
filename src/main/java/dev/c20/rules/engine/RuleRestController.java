package dev.c20.rules.engine;

import dev.c20.rules.engine.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RuleRestController {

    @Autowired
    RuleService ruleService;

    @GetMapping("/version")
    public String version() {
        return "2021-04-13 18:50";
    }

    @PostMapping("/evaluate")
    public BusinessRuleResponse evalBusinessRule(@RequestBody RuleRequest request) {
        return ruleService.evalBusinessRule(request);
    }

    @PostMapping("/evaluate-and-fire")
    public List<Object> evalAndFireBusinessRule(@RequestBody RuleRequest request) {
        return ruleService.evalAndFireBusinessRule(request);
    }


}

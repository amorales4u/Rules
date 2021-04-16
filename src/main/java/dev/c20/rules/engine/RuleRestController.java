package dev.c20.rules.engine;

import dev.c20.rules.engine.services.*;
import dev.c20.rules.engine.services.entities.BusinessEvalRuleResponse;
import dev.c20.rules.engine.services.entities.BusinessRuleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public BusinessEvalRuleResponse evalAndFireBusinessRule(@RequestBody RuleRequest request) {
        return ruleService.evalAndFireBusinessRule(request);
    }


}

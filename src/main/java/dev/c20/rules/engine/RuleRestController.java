package dev.c20.rules.engine;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.services.*;
import dev.c20.rules.engine.services.entities.BusinessEvalRuleResponse;
import dev.c20.rules.engine.services.entities.BusinessRuleResponse;
import dev.c20.rules.engine.storage.entities.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public BusinessRuleResponse evalBusinessRule(@RequestBody RuleRequest request, HttpServletRequest httpRequest) {
        return ruleService.setHttpRequest(httpRequest)
            .evalBusinessRule(request);
    }

    @PostMapping("/evaluate-and-fire")
    public BusinessEvalRuleResponse evalAndFireBusinessRule(@RequestBody RuleRequest request, HttpServletRequest httpRequest) {
        return ruleService.setHttpRequest(httpRequest)
                .evalAndFireBusinessRule(request);
    }

    @PostMapping("/rule/update/")
    public Storage addOrUpdateRule(@RequestBody Rule rule, HttpServletRequest httpRequest) {
        return ruleService.setHttpRequest(httpRequest)
                .addOrUpdateRule(rule);
    }

    @PostMapping("/fact/update/")
    public Storage addOrUpdateFact(@RequestBody Fact fact, HttpServletRequest httpRequest) {
        return ruleService.setHttpRequest(httpRequest)
                .addOrUpdateFact(fact);
    }

    @PostMapping("/group/update/")
    public Storage addOrUpdateGroup(@RequestBody Group group, HttpServletRequest httpRequest) {

        return ruleService.setHttpRequest(httpRequest)
                .addOrUpdateGroup(group);
    }

    @PostMapping("/group-rules/update/")
    public Storage addOrUpdateGroup(@RequestBody Map<String,Object> group, HttpServletRequest httpRequest) {
        String groupPath = (String)group.get("group");
        List<String> rules = (List<String>)group.get("rules");

        return ruleService.setHttpRequest(httpRequest)
                .addOrUpdateGroupRules(groupPath, rules);
    }





}

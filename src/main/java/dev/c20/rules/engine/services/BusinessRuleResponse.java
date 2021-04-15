package dev.c20.rules.engine.services;

import dev.c20.rules.engine.entities.Rule;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessRuleResponse {

    private boolean ruleGroupFound;
    private boolean complied;
    private String factNotFoundMessage;
    private String ruleGroupEvaluated;
    private List<Rule> rulesComplied;

    public boolean isValid() {
        return ruleGroupFound && complied;
    }

}

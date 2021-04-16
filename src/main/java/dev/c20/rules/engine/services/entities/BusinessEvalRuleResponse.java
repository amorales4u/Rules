package dev.c20.rules.engine.services.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessEvalRuleResponse {

    private boolean ruleGroupFound;
    private boolean complied;
    private String factNotFoundMessage;
    private String ruleGroupEvaluated;
    private List<EvaluateFactResponse> rulesEvaluated;

    public boolean isValid() {
        return ruleGroupFound && complied;
    }


}

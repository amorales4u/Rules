package dev.c20.rules.engine.services;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RuleRequest {

    private String ruleGroupName;
    private Map<String,Object> context;

}

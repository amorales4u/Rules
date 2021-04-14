package dev.c20.rules.engine.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.c20.rules.engine.entities.Rule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleResponse {
    String name;
    String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String fact;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String factNotFound;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean exclusive;

    static public RuleResponse fromRule(Rule rule) {
        RuleResponse response = new RuleResponse();
        response.name = rule.getName();
        response.description = rule.getDescription();
        response.fact = rule.getFact();
        response.exclusive = rule.isExclusive();
        return response;
    }

}

package dev.c20.rules.engine.entities;

import dev.c20.rules.engine.services.GroupResponse;
import dev.c20.rules.engine.services.RuleResponse;
import lombok.Getter;
import lombok.Setter;

public interface IFact {


    public Object execute(RuleResponse ruleResponse, Fact fact, Object context );
    public Object execute(GroupResponse groupResponse, Fact fact, Object context );
}

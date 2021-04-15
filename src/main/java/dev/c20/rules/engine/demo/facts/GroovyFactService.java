package dev.c20.rules.engine.demo.facts;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.services.GroupResponse;
import dev.c20.rules.engine.services.RuleResponse;
import org.springframework.stereotype.Service;

@Service
public class GroovyFactService implements IFact   {

    public Object execute(RuleResponse ruleResponse, Fact fact, Object context ) {

        return "Here execute fact " + fact.name();
    }
    public Object execute(GroupResponse groupResponse, Fact fact, Object context ) {
        return null;
    }
}

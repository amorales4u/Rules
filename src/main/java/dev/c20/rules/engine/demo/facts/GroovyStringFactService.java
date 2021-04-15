package dev.c20.rules.engine.demo.facts;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.services.EvaluateFactResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroovyStringFactService implements IFact   {

    public EvaluateFactResponse execute(Rule rule, Fact fact, Object context ) {
        EvaluateFactResponse result = new EvaluateFactResponse();
        result.setRuleEvaluated(rule.getName());
        result.setDescription(rule.getDescription());
        result.setResult("Es un Fact ejecutado, String FACT");
        return result;
    }


}

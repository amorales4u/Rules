package dev.c20.rules.engine.demo.facts;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.storage.repository.DataRepository;
import dev.c20.rules.engine.services.entities.EvaluateFactResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class GroovyFactService implements IFact {

    @Autowired
    DataRepository dataRepository;

    public EvaluateFactResponse execute(Rule rule, Fact fact, Object context, Map<String,Object> params) {
        String factToDo = dataRepository.getDataOf("/Catálogos/Facts/" + fact.name());
        EvaluateFactResponse result = new EvaluateFactResponse();
        result.setRuleEvaluated(rule.getName());
        result.setFact(rule.getFact().getName());
        result.setDescription(rule.getDescription());

        log.info(params.toString());

        if( factToDo == null ) {
            result.setEvaluatedCorrectly(false);
            result.setEvaluationError( "Fact " + fact.name() + " has not implementation" );
            return result;
        }
        result.setResult("Here execute fact " + fact.name() + "\n" + factToDo);
        return result;
    }


}


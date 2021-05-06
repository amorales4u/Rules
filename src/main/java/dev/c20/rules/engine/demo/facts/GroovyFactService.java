package dev.c20.rules.engine.demo.facts;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.services.RuleRequest;
import dev.c20.rules.engine.storage.repository.DataRepository;
import dev.c20.rules.engine.services.entities.EvaluateFactResponse;
import dev.c20.rules.engine.tools.Eval;
import dev.c20.rules.engine.tools.EvalResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

@Service
@Slf4j
public class GroovyFactService implements IFact {

    @Autowired
    DataSource dataSource;

    @Autowired
    DataRepository dataRepository;

    public EvaluateFactResponse execute(Rule rule, Fact fact, Map<String,Object> context, Map<String,Object> params) {
        String factToDo = dataRepository.getDataOf("/system/business/facts/" + fact.name());
        EvaluateFactResponse result = new EvaluateFactResponse();
        result.setRuleEvaluated(rule.getName());
        result.setFact(rule.getFact().getName());
        result.setDescription(rule.getDescription());

        log.info("Fact params:");
        log.info(params.toString());

        context.put("dataSource", dataSource);

        if( factToDo == null ) {
            result.setEvaluatedCorrectly(false);
            result.setEvaluationError( "Fact " + fact.name() + " has not implementation" );
            return result;
        }

        EvalResult evalResult = Eval.getInstance().run(factToDo,context,fact.name());
        if( evalResult.isError() ) {
            result.setEvaluatedCorrectly(false);
            result.setEvaluationError(evalResult.getErrorMessage());
            return result;
        }
        result.setEvaluatedCorrectly(true);
        result.setResult(evalResult.getResult());

        return result;
    }


}


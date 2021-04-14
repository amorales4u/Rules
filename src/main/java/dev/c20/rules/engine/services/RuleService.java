package dev.c20.rules.engine.services;

import dev.c20.rules.engine.demo.WorkFlowBusiness;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.tools.Eval;
import dev.c20.rules.engine.tools.EvalResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RuleService {


    public String eval(String code, Map<String,Object> context, String nameCode, String defaultFact) {
        String fact = defaultFact;

        return fact;

    }

    public List<String> evalBusinessRule( RuleRequest request) {

        Group rulesGroup = WorkFlowBusiness.getInstance().getBussinessRules().find(request.getRuleGroupName());

        if( rulesGroup == null ) {
            List<String> result = new ArrayList<>();
            return result;
        }

        return evalBusinessRule(rulesGroup, request.getContext());

    }

    public List<String> evalBusinessRule(Group rulesGroup, Map<String,Object> context) {
        List<String> facts = new ArrayList<>();
        String fact = null;
        for( Rule rule : rulesGroup.getRules() ) {
            fact = evalRule( facts, rule, context, 0);
            if( fact != null )
                return facts;
        }

        log.info("Return defaultFact from rules group " + rulesGroup.getFactNotFound());

        if( rulesGroup.getFactNotFound() != null ) {
            facts.add(rulesGroup.getName() + " -- " + rulesGroup.getFactNotFound());
        }
        return facts;
    }
    public String evalRule( List<String> facts, Rule rule, Map<String,Object> context, int level) {
        EvalResult evalResult = Eval.getInstance().run(rule.getExpression(), context, rule.getName() );
        boolean result = (boolean)evalResult.getResult();
        log.info("Eval expression rule (in level " + level + " ):" + rule.getName() + " => " + result);
        if( rule.getRules() != null && rule.getRules().size() > 0 && result ) {
            for( Rule childRule : rule.getRules()) {
                String fact = evalRule(facts,childRule,context, level + 1);
                if( fact != null ) {
                    log.info("Finded Fact: " + fact);
                    //facts.add(fact);
                    if( childRule.isExclusive() ) {
                        return fact;
                    }
                }
            }
        }

        if( result ) {
            if( rule.getFact() != null) {
                facts.add(rule.getName() + " -- " + rule.getFact());
            }
            return rule.getFact();
        }

        return null;

    }

    private void addFact( List<String> facts, String fact) {
        if( fact != null ) {
            facts.add(fact);
        }
    }

}

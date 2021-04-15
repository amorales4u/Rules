package dev.c20.rules.engine.services;

import dev.c20.rules.engine.demo.WorkFlowBusiness;
import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.tools.Eval;
import dev.c20.rules.engine.tools.EvalResult;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RuleService {


    public BusinessEvalRuleResponse evalAndFireBusinessRule( RuleRequest request) {

        BusinessRuleResponse businessRuleResponse = evalBusinessRule( request);

        BusinessEvalRuleResponse businessEvalRuleResponse = new BusinessEvalRuleResponse();
        businessEvalRuleResponse.setComplied( businessRuleResponse.isComplied() );
        businessEvalRuleResponse.setRuleGroupFound( businessRuleResponse.isRuleGroupFound() );
        businessEvalRuleResponse.setFactNotFoundMessage( businessRuleResponse.getFactNotFoundMessage() );
        businessEvalRuleResponse.setRuleGroupEvaluated( businessRuleResponse.getRuleGroupEvaluated() );

        if( !businessRuleResponse.isValid() ) {
            return businessEvalRuleResponse;
        }

        List<EvaluateFactResponse> responses = new ArrayList<>();

        for( Rule rule : businessRuleResponse.getRulesComplied() ) {
            String factName = rule.getFact().name();
            Fact factToFire = FactsRegistered.getInstance().get(factName);

            if( factToFire == null ) {
                throw new RuntimeException("Fact named [" + factName + " => " + rule.getName() + "] not exists");
            }

            // mapping Rule context data to Fact parameters
            Map<String,Object> params = new HashMap<>();
            List<String> paramKeys = new ArrayList<String>(rule.getFact().getParameters().keySet());

            for( String paramKey : paramKeys) {
                params.put(paramKey, Eval.getInstance().run(rule.getFact().getParameters().get(paramKey),request.getContext(),"Eval" + ( new Date().getTime())));
            }

            responses.add( ((IFact)factToFire.instance()).execute(rule, factToFire,request.getContext(), params) );

        }

        businessEvalRuleResponse.setRulesEvaluated(responses);

        return businessEvalRuleResponse;
    }

    public BusinessRuleResponse evalBusinessRule(RuleRequest request) {
        BusinessRuleResponse businessRuleResponse = new BusinessRuleResponse();
        Group rulesGroup = WorkFlowBusiness.getInstance().getBussinessRules().find(request.getRuleGroupName());

        if( rulesGroup == null ) {
            businessRuleResponse.setRuleGroupFound(false);
            businessRuleResponse.setComplied(false);
            businessRuleResponse.setFactNotFoundMessage("Not found business rule group " + request.getRuleGroupName());
            return null;
        }
        businessRuleResponse.setRuleGroupFound(true);
        businessRuleResponse.setRulesComplied(evalBusinessRule(rulesGroup, request.getContext()));
        businessRuleResponse.setComplied(businessRuleResponse.getRulesComplied().size() > 0);
        businessRuleResponse.setRuleGroupEvaluated(request.getRuleGroupName());
        if(!businessRuleResponse.isComplied()) {
            businessRuleResponse.setFactNotFoundMessage(stringTemplate(rulesGroup.getFactNotFoundMessage(),request.getContext()));
        }
        return businessRuleResponse;

    }

    public List<Rule> evalBusinessRule(Group rulesGroup, Map<String,Object> context) {
        List<Rule> facts = new ArrayList<>();
        for( Rule rule : rulesGroup.getRules() ) {
            String fact = evalRule( facts, rule, context, 0);
            if( fact != null )
                return facts;
        }

        log.info("Return defaultFact from rules group " + rulesGroup.getFactNotFound());

        return facts;
    }
    public String evalRule(List<Rule> facts, Rule rule, Map<String,Object> context, int level) {
        EvalResult evalResult = Eval.getInstance().run(rule.getExpression(), context, rule.getName() );
        boolean result = (boolean)evalResult.getResult();
        log.info("Eval expression rule (in level " + level + " ):" + rule.getName() + " => " + result);
        if( rule.getRules() != null && rule.getRules().size() > 0 && result ) {
            for( Rule childRule : rule.getRules()) {
                String fact = evalRule(facts,childRule,context, level + 1);
                if( fact != null ) {
                    log.info("Found Fact: " + fact);
                    //facts.add(fact);
                    if( childRule.isExclusive() ) {
                        return fact;
                    }
                }
            }
        }

        if( result && rule.getFact() != null ) {
            facts.add( rule );
            return rule.getFact().name();
        }

        return null;

    }

    public String stringTemplate(String templateStr, Object context ) {
        try {
            Map<String,Object> ctx = new HashMap<>();
            ctx.put("context",context);
            SimpleTemplateEngine engine = new SimpleTemplateEngine();
            Template template = engine.createTemplate(templateStr);
            return template.make(ctx).toString();
        } catch( Exception ex ) {
            log.error(ex.getMessage());
            return "error in factNotFoundMessage";
        }
    }


}

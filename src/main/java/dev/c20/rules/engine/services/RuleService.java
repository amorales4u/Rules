package dev.c20.rules.engine.services;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.services.entities.BusinessEvalRuleResponse;
import dev.c20.rules.engine.services.entities.BusinessRuleResponse;
import dev.c20.rules.engine.services.entities.EvaluateFactResponse;
import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.rules.engine.tools.Eval;
import dev.c20.rules.engine.tools.EvalResult;
import dev.c20.workflow.commons.tools.StringUtils;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Service
public class RuleService {


    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    BusinessStorageService businessStorageService;

    HttpServletRequest httpServletRequest;

    public RuleService setHttpRequest( HttpServletRequest httpServletRequest ) {
        this.httpServletRequest = httpServletRequest;
        return this;
    }


    public Class<?> getClassFromName(String clazzName) {
        try {
            return  Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            log.error("Class not found " + clazzName,e);
        }

        return null;
    }
    public Object getInstance(String clazzName) {
        try {
            Class<?> act = getClassFromName(clazzName);
            Object instance;

            if( act.isAnnotationPresent(Service.class) ) {
                instance = applicationContext.getBean(act);
            } else{
                instance = act.newInstance();
            }
            return instance;
        } catch (InstantiationException e) {
            log.error("InstantiationException " + clazzName,  e );
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException " + clazzName, e );
        } catch (Exception e) {
            log.error("Exception " + clazzName ,  e );
        }

        return null;
    }

    public BusinessEvalRuleResponse evalAndFireBusinessRule(RuleRequest request) {

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
            Fact factToFire = RulesAndFactsRegistered.getInstance().getFact(factName);

            if( factToFire == null ) {
                throw new RuntimeException("Fact named [" + factName + " => " + rule.getName() + "] not exists");
            }

            // mapping Rule context data to Fact parameters
            List<String> paramKeys = new ArrayList<>(rule.getFact().getParameters().keySet());
            StringBuilder ruleSetFactParams = new StringBuilder("\n");

            log.warn("Fact to fire parameters definition:" + rule.getFact().name());
            log.warn("\n" + StringUtils.collectionAsString(factToFire.getParameters(),"\n"));

            for( String paramKey : paramKeys) {
                ruleSetFactParams.append(paramKey).append(" = ").append(rule.getFact().getParameters().get(paramKey)).append("\n");
            }

            log.warn("MapRuleToFact:");
            log.warn(ruleSetFactParams.toString());

            log.warn("Eval Expression:Fact to fire parameters definition");
            EvalResult paramDefinition = Eval.getInstance().run(StringUtils.collectionAsString(factToFire.getParameters(),"\n"),request.getContext(),"nocache");
            // add param var to context
            log.warn("Eval Expression:MapRuleToFact");
            request.getContext().put("param", paramDefinition.getResult());
            EvalResult paramResult = Eval.getInstance().run(ruleSetFactParams.toString(),request.getContext(),"nocache");
            if( paramResult.isError() ) {
                log.error( paramResult.getErrorMessage());
            }
            if( factToFire.getInstance() == null ) {
                factToFire.setInstance( getInstance(factToFire.getClazzName()));
            }
            Map<String,Object> params = (Map<String,Object>)request.getContext().get("param");
            // remove param from context for other calls, clean
            request.getContext().remove("param");

            EvaluateFactResponse factResponse = ((IFact)factToFire.getInstance()).execute(rule, factToFire,request.getContext(), params);
            if( businessEvalRuleResponse.isFactsEvaluatedCorrectly() && !factResponse.isCorrectlyEvaluated() ) {
                businessEvalRuleResponse.setFactsEvaluatedCorrectly(false);
            }
            responses.add( factResponse );


        }

        businessEvalRuleResponse.setRulesEvaluated(responses);

        return businessEvalRuleResponse;
    }

    public BusinessRuleResponse evalBusinessRule(RuleRequest request) {
        BusinessRuleResponse businessRuleResponse = new BusinessRuleResponse();
        Group rulesGroup = RulesAndFactsRegistered.getInstance().getGroup(request.getRuleGroupName());

        if( rulesGroup == null ) {
            businessRuleResponse.setRuleGroupFound(false);
            businessRuleResponse.setComplied(false);
            businessRuleResponse.setFactNotFoundMessage("Not found business rule group " + request.getRuleGroupName());
            return null;
        }
        StringBuilder mapOfRules = new StringBuilder();
        businessRuleResponse.setRuleGroupFound(true);
        businessRuleResponse.setRulesComplied(evalBusinessRule(rulesGroup, request.getContext(),mapOfRules));
        businessRuleResponse.setComplied(businessRuleResponse.getRulesComplied().size() > 0);
        businessRuleResponse.setRuleGroupEvaluated(request.getRuleGroupName());
        businessRuleResponse.setMapOfRules( mapOfRules.toString() );

        if(!businessRuleResponse.isComplied()) {
            businessRuleResponse.setFactNotFoundMessage(stringTemplate(rulesGroup.getFactNotFoundMessage(),request.getContext()));
        }
        return businessRuleResponse;

    }

    public List<Rule> evalBusinessRule(Group rulesGroup, Map<String,Object> context,StringBuilder mapOfRules) {
        List<Rule> facts = new ArrayList<>();
        log.info("Eval busines rules group:" + rulesGroup.getName());
        log.info(context.toString());
        context.put("httpRequest",httpServletRequest);

        for( Rule rule : rulesGroup.getRules() ) {
            String fact = evalRule( facts, rule, context, 0,mapOfRules);
            if( fact != null )
                return facts;
        }

        log.info("Return defaultFact from rules group " + rulesGroup.getFactNotFound());

        return facts;
    }
    public String evalRule(List<Rule> facts, Rule rule, Map<String,Object> context, int level, StringBuilder mapOfRules) {
        EvalResult evalResult = Eval.getInstance().run(rule.getExpression(), context, rule.getName() );
        boolean result = (boolean)evalResult.getResult();
        //log.info("Eval expression rule (in level " + level + " ):" + rule.getName() + " => " + rule.getExpression() + " => " + result);
        String divisor = level > 1 ? "+" : "";
        log.info("Eval Rule  " + ( String.join("", Collections.nCopies(level,   "|    " ) )  + "|----"
                + rule.getName() ) + " => " + ( result ? " Passed" : " x"));
        mapOfRules.append("Eval Rule  " + ( String.join("", Collections.nCopies(level,   "|    " ) )  + "|----"
                + rule.getName() ) + " => " + ( result ? " Passed" : " x") + "\n");
        if( rule.getRules() != null && rule.getRules().size() > 0 && result ) {
            for( Rule childRule : rule.getRules()) {
                String fact = evalRule(facts,childRule,context, level + 1, mapOfRules);
                if( fact != null ) {
                    //log.info("Found Fact: " + fact);
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

    public Storage addOrUpdateRule(Rule rule ) {

        return businessStorageService.persistRule(rule);
    }

    public Storage addOrUpdateFact(Fact fact ) {

        return businessStorageService.persistFact(fact);
    }

    public Storage addOrUpdateGroup(Group group)  {
        return businessStorageService.persistGroup(group);

    }

    public Storage addOrUpdateGroupRules(String groupPath, List<String> rules)  {
        return businessStorageService.setRulesForGroup(groupPath,rules);

    }


}

package dev.c20.rules.engine.demo.facts;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.services.GroupResponse;
import dev.c20.rules.engine.services.RuleResponse;
import dev.c20.rules.engine.tools.Eval;
import groovy.text.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import groovy.text.SimpleTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GroovyStringFactService implements IFact   {

    public Object execute(RuleResponse ruleResponse, Fact fact, Object context ) {

        return "Es un Fact ejecutado, String FACT";
    }

    public Object execute(GroupResponse groupResponse, Fact fact, Object context ) {
        try {
            Map<String,Object> ctx = new HashMap<>();
            ctx.put("context",context);
            SimpleTemplateEngine engine = new SimpleTemplateEngine();
            Template template = engine.createTemplate(groupResponse.getFactNotFoundMessage());
            String msg = template.make(ctx).toString();
            return msg;
        } catch( Exception ex ) {
            log.error(ex.getMessage());
            return "error in factNotFoundMessage";
        }
    }


}

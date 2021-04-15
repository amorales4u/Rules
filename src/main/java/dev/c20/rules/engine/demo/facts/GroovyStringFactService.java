package dev.c20.rules.engine.demo.facts;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.entities.Rule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroovyStringFactService implements IFact   {

    public Object execute(Rule rule, Fact fact, Object context ) {

        return "Es un Fact ejecutado, String FACT";
    }


}

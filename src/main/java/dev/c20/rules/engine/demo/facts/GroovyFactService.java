package dev.c20.rules.engine.demo.facts;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import org.springframework.stereotype.Service;

@Service
public class GroovyFactService implements IFact   {

    public Object execute( Fact fact, Object context ) {
        return null;
    }
}

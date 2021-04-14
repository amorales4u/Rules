package dev.c20.rules.engine.services;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;

import java.util.HashMap;
import java.util.Map;

public class FactsRegistered {

    static private FactsRegistered instance;

    private Map<String,Fact> facts = new HashMap<>();
    static public FactsRegistered getInstance() {
        if( instance == null )
            instance = new FactsRegistered();

        return instance;
    }

    private FactsRegistered() {

    }

    public void register( Fact fact ) {
        if( !( fact.instance() instanceof IFact ) ) {
            throw new RuntimeException("Fact instance not implements IFact interface");
        }
        facts.put( fact.name(), fact );
    }

    public Fact get( String name ) {
        return facts.get(name);
    }

}

package dev.c20.rules.engine.services;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.entities.Rule;

import java.util.HashMap;
import java.util.Map;

public class RulesAndFactsRegistered {

    static private RulesAndFactsRegistered instance;

    private Map<String, Fact> facts = new HashMap<>();
    private Map<String, Rule> rules = new HashMap<>();
    private Map<String, Group> groups = new HashMap<>();

    static public RulesAndFactsRegistered getInstance() {
        if( instance == null )
            instance = new RulesAndFactsRegistered();

        return instance;
    }

    private RulesAndFactsRegistered() {

    }

    public void register( Fact fact ) {
        if( fact.getInstance() != null && !( fact.getInstance() instanceof IFact ) ) {
            throw new RuntimeException("Fact instance not implements IFact interface");
        }
        facts.put( fact.getName(), fact );
    }

    public Fact getFact(String name ) {
        return facts.get(name);
    }

    public void register( Rule rule ) {
        rules.put( rule.getName(), rule );
    }

    public Rule getRule(String name ) {
        return rules.get(name);
    }

    public void register( Group group ) {
        groups.put( group.getName(), group );
    }

    public Group getGroup(String name ) {
        return groups.get(name);
    }

}

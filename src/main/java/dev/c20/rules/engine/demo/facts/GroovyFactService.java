package dev.c20.rules.engine.demo.facts;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.IFact;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.repositories.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroovyFactService implements IFact {

    @Autowired
    DataRepository dataRepository;

    public Object execute(Rule rule, Fact fact, Object context) {
        String factToDo = dataRepository.getDataOf("/Catálogos/Facts/" + fact.name());
        if( factToDo == null ) {
            return "Fact " + fact.name() + " has not implementation";
        }
        return "Here execute fact " + fact.name() + "\n" + factToDo;
    }
}


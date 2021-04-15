package dev.c20.rules.engine.entities;

import dev.c20.rules.engine.services.EvaluateFactResponse;

public interface IFact {


    public EvaluateFactResponse execute(Rule rile, Fact fact, Object context );
}

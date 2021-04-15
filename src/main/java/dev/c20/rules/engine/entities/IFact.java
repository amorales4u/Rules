package dev.c20.rules.engine.entities;

import dev.c20.rules.engine.services.EvaluateFactResponse;

import java.util.Map;

public interface IFact {


    public EvaluateFactResponse execute(Rule rile, Fact fact, Object context, Map<String,Object> params );
}

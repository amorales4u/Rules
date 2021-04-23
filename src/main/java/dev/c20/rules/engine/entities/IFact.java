package dev.c20.rules.engine.entities;

import dev.c20.rules.engine.services.RuleRequest;
import dev.c20.rules.engine.services.entities.EvaluateFactResponse;

import java.util.Map;

public interface IFact {


    public EvaluateFactResponse execute(Rule rile, Fact fact, Map<String,Object> context, Map<String,Object> params );
}

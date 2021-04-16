package dev.c20.rules.engine.services.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EvaluateFactResponse {
    boolean evaluatedCorrectly;
    String evaluationError;
    String ruleEvaluated;
    String description;
    String fact;
    Object result;
}

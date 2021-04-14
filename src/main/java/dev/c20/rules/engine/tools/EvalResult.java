package dev.c20.rules.engine.tools;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EvalResult {

    private boolean error;
    private String errorMessage;
    private Object result;
}

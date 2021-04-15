package dev.c20.rules.engine.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true) @Getter
@Setter
public class RuleMapToFact {
    String name;
    Map<String,String> parameters = new HashMap<>();

    public RuleMapToFact addParameter( String target, String source ) {
        parameters.put(target, source);
    }
}

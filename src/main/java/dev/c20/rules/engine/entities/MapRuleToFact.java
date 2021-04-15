package dev.c20.rules.engine.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class MapRuleToFact implements Serializable {
    String name;
    Map<String,String> parameters = new HashMap<>();

    public MapRuleToFact addParameter(String target, String source ) {
        parameters.put(target, source);
        return this;
    }

    public MapRuleToFact name( String name ) {
        this.name = name;
        return this;
    }
    public String name() {
        return this.name;
    }

}

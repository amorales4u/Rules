package dev.c20.rules.engine.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.c20.rules.engine.entities.Rule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleResponseBase {
    String name;
    String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String fact;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Boolean exclusive;



}

package dev.c20.rules.engine.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Group implements Serializable {

    String name;
    String factNotFound = null;
    List<Rule> rules = null;


    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnore
    public BusinessRules parent;

    public Group name(String name) {
        this.name = name;
        return this;
    }

    public Group factNotFound(String defaultFact) {
        this.factNotFound = defaultFact;
        return this;
    }

    public Rule startRule() {
        if( rules == null )
            rules = new ArrayList<>();
        Rule currentRule = new Rule();
        currentRule.parent = this;
        return currentRule;
    }



    public BusinessRules finishGroup() {
        parent.rulesGroups.add(this);
        return parent;
    }



}

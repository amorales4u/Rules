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
public class Rule  implements Serializable {

    String name;
    String description;
    MapRuleToFact fact;
    boolean exclusive = true;

    List<String> condition = null;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnore
    public Group parent ;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnore
    private Rule parentRule;

    List<Rule> rules = null;

    public Rule clean() {
        condition = new ArrayList<>();
        return this;
    }

    public Rule name( String name ) {
        this.name = name;
        return this;
    }

    public Rule fact( MapRuleToFact fact ) {
        this.fact = fact;
        return this;
    }

    public Rule exclusive( Boolean exclusive ) {
        this.exclusive = exclusive;
        return this;
    }


    public Group finishRule() {
        parent.rules.add(this);
        return parent;
    }

    public Rule addLine(String line) {
        if( condition == null ) {
            condition = new ArrayList<>();
        }
        condition.add(line);
        return this;
    }

    public Rule startChildRule() {
        Rule rule = new Rule();
        rule.parentRule = this;
        return rule;
    }

    public Rule finishChildRule() {
        if( parentRule.rules == null ) {
            parentRule.rules = new ArrayList<>();
        }
        parentRule.rules.add(this);
        return parentRule;
    }

    @JsonIgnore
    public String getExpression() {
        String expression = "";
        for( String line : condition ) {
            expression += line + "\n";
        }
        return expression;
    }
}

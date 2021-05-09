package dev.c20.rules.engine.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.c20.workflow.commons.tools.PathUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Accessors( chain = true)
@Getter
@Setter
@Slf4j
public class Group implements Serializable {

    String name;
    String description;
    String factNotFound = null;
    String factNotFoundMessage = null;
    String path;
    boolean configured = false;
    List<Rule> rules = null;


    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnore
    public BusinessRules parent;

    public Group name(String name) {
        this.name = name;
        return this;
    }

    public Group description(String description) {
        this.description = description;
        return this;
    }

    public Group factNotFoundMessage(String factNotFoundMessage) {
        this.factNotFoundMessage = factNotFoundMessage;
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

    public Group addTreeRule( String path, Rule rule) {
        if( rule == null ) {
            log.error("Rule is null for path " + path);
            return this;
        }

        if( rules == null ) {
            rules = new ArrayList<>();
        }
        String[] paths = PathUtils.splitPath(PathUtils.getParentFolder( path ));
        List<Rule> addIn = rules;
        for( int i = 0; i < paths.length; i ++ ) {
            for (Rule parentRule : addIn) {
                if (parentRule.getName().equals(paths[i])) {
                    if( parentRule.getRules() == null ) {
                        parentRule.setRules(new ArrayList<>());
                    }
                    addIn = parentRule.getRules();
                    break;
                }
            }
        }
        addIn.add( rule );
        return this;
    }


}

package dev.c20.rules.engine.entities;

import lombok.Getter;
import lombok.Setter;

public interface IFact {


    public Object execute( Fact fact, Object context );
}

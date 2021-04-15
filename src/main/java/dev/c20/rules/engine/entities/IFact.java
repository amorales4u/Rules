package dev.c20.rules.engine.entities;

public interface IFact {


    public Object execute(Rule rile,Fact fact, Object context );
}

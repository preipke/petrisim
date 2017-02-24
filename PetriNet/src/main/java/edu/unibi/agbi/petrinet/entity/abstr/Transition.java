/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.petrinet.entity.abstr;

import edu.unibi.agbi.petrinet.model.Function;
import edu.unibi.agbi.petrinet.entity.PN_Element;
import edu.unibi.agbi.petrinet.entity.PN_Node;

/**
 *
 * @author PR
 */
public abstract class Transition extends PN_Node
{
    private static final String IDENT = "T";
    private static int COUNT = 0;
    
    private Function function;
    
    private Type transitionType;
    
    public Transition() {

        super(IDENT + ++COUNT);
        
        type = PN_Element.Type.TRANSITION;
        
        function = new Function();
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }
    
    public void setTransitionType(Type transitionType) {
        this.transitionType = transitionType;
    }
    
    public Type getTransitionType() {
        return transitionType;
    }
    
    public enum Type {
        CONTINUOUS, DISCRETE, STOCHASTIC;
    }
}

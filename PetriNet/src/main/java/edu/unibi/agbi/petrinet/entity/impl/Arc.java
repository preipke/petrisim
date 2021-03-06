/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.petrinet.entity.impl;

import edu.unibi.agbi.petrinet.model.Colour;
import edu.unibi.agbi.petrinet.model.Weight;
import java.util.HashMap;
import java.util.Map;
import edu.unibi.agbi.petrinet.entity.IArc;
import edu.unibi.agbi.petrinet.entity.INode;
import edu.unibi.agbi.petrinet.entity.abstr.Element;
import java.util.Collection;

/**
 *
 * @author PR
 */
public class Arc extends Element implements IArc
{
    private Type arcType;

    protected INode target;
    protected INode source;

    private final Map<Colour, Weight> weights;
//    private final Map<Colour, Double> conflicts; // conflict resolution based on colours
    private double conflictResValue = 1.0d;

    public Arc(String id, INode source, INode target, Type arcType) {
        super(id, Element.Type.ARC);
        this.weights = new HashMap();
//        this.conflicts = new HashMap();
        this.source = source;
        this.target = target;
        this.arcType = arcType;
    }

    @Override
    public INode getTarget() {
        return target;
    }

    @Override
    public INode getSource() {
        return source;
    }
    
    @Override
    public void addWeight(Weight weight) {
        weights.put(weight.getColour(), weight);
    }

    @Override
    public Weight getWeight(Colour colour) {
        return weights.get(colour);
    }

    @Override
    public Collection<Weight> getWeights() {
        return weights.values();
    }

    @Override
    public void setArcType(Type arcType) {
        this.arcType = arcType;
    }

    @Override
    public Type getArcType() {
        return arcType;
    }

    @Override
    public double getConflictResolutionValue() {
        return conflictResValue;
    }

    @Override
    public void setConflictResolutionValue(double conflictResValue) {
        this.conflictResValue = conflictResValue;
    }

    public enum Type
    {
        NORMAL, INHIBITORY, TEST;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.petrinet.model;

import edu.unibi.agbi.petrinet.entity.impl.Place;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.unibi.agbi.petrinet.entity.IArc;
import edu.unibi.agbi.petrinet.entity.INode;
import edu.unibi.agbi.petrinet.exception.IdConflictException;

/**
 *
 * @author PR
 */
public class PetriNet
{
    public final static Colour DEFAULT_COLOUR = new Colour("DEFAULT", "Default colour");
    
    private String author;
    private String name;
    private String description;
    
    private final List<Colour> colors;
    
    private final Map<String,IArc> arcs;
    private final Map<String,INode> places;
    private final Map<String,INode> transitions;
    private final Map<String,INode> placesAndTransitions; // TODO remove if places and transitions can have the same names
    
    public PetriNet() {
        colors = new ArrayList();
        colors.add(DEFAULT_COLOUR);
        
        arcs = new HashMap();
        places = new HashMap();
        transitions = new HashMap();
        placesAndTransitions = new HashMap();
    }
    
    public void add(Colour color) throws IdConflictException {
        if (colors.contains(color)) {
            throw new IdConflictException("A color with the same ID has already been stored!");
        } 
        colors.add(color);
    }
    
    public void add(IArc arc) throws IdConflictException {
        if (arcs.containsKey(arc.getId())) {
            if (!arcs.get(arc.getId()).equals(arc)) {
                throw new IdConflictException("Another arc has already been stored using the same ID!");
            }
        } else {
            arcs.put(arc.getId(), arc);
        }
        arc.getSource().getArcsOut().add(arc);
        arc.getTarget().getArcsIn().add(arc);
    }
    
    public void add(INode node) throws IdConflictException {
        if (placesAndTransitions.containsKey(node.getId())) {
            if (!placesAndTransitions.get(node.getId()).equals(node)) {
                throw new IdConflictException("Another node has already been stored using the same ID!");
            }
        } else {
            placesAndTransitions.put(node.getId(), node);
            if (node instanceof Place) {
                places.put(node.getId(), node);
            } else {
                transitions.put(node.getId(), node);
            }
        }
    }
    
    public IArc remove(IArc arc) {
        arc.getSource().getArcsOut().remove(arc);
        arc.getTarget().getArcsIn().remove(arc);
        return arcs.remove(arc.getId());
    }
    
    public INode remove(INode node) {
        if (placesAndTransitions.remove(node.getId()) == null) {
            return null;
        } 
        while (!node.getArcsIn().isEmpty()) {
            remove(node.getArcsIn().remove(0));
        }
        while (!node.getArcsOut().isEmpty()) {
            remove(node.getArcsOut().remove(0));
        }
        if (node instanceof Place) {
            return places.remove(node.getId());
        } else {
            return transitions.remove(node.getId());
        }
    }
    
    public Collection<IArc> getArcs() {
        return arcs.values();
    }
    
    public List<Colour> getColours() {
        return colors;
    }
    
    public Collection<INode> getPlaces() {
        return places.values();
    }
    
    public Collection<INode> getPlacesAndTransitions() {
        return placesAndTransitions.values();
    }
    
    public Collection<INode> getTransitions() {
        return transitions.values();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

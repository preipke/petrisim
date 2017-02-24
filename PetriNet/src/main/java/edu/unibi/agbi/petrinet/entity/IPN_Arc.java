/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.petrinet.entity;

import edu.unibi.agbi.petrinet.entity.abstr.Arc;
import edu.unibi.agbi.petrinet.model.Colour;
import edu.unibi.agbi.petrinet.model.Weight;
import java.util.Map;

/**
 *
 * @author PR
 */
public interface IPN_Arc extends IPN_Element
{
    public IPN_Node getSource();
    public IPN_Node getTarget();
    public Weight getWeight(Colour colour);
    public Map<Colour,Weight> getWeightMap();
}

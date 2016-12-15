/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gravisfx.graph.entity;

import java.util.List;

/**
 *
 * @author PR
 */
public interface IGravisNode extends IGravisElement
{
    public void setTranslate(double positionX, double positionY);
    
    public double getOffsetX();
    public double getOffsetY();
    
    public void addParentNode(IGravisNode parent);
    public void addChildNode(IGravisNode child);
    public void addEdge(IGravisEdge edge);
    
    public boolean removeParent(IGravisNode node);
    public boolean removeChild(IGravisNode node);
    public boolean removeEdge(IGravisEdge node);
    
    public List<IGravisNode> getParents();
    public List<IGravisNode> getChildren();
    public List<IGravisEdge> getEdges();
}
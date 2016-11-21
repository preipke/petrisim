/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gravisfx.graph.node.entity;

import edu.unibi.agbi.gravisfx.graph.node.IGravisNode;
import edu.unibi.agbi.gravisfx.PropertiesController;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 *
 * @author PR
 */
public class GravisCircle extends Circle implements IGravisNode
{
    private final List<IGravisNode> children = new ArrayList();
    private final List<IGravisNode> parents = new ArrayList();
    private final List<GravisEdge> edges = new ArrayList();
    
    public GravisCircle(String id) {
        super();
        setId(id);
        setRadius(PropertiesController.CIRCLE_RADIUS);
    }
    
    public GravisCircle(String id, Paint color) {
        super();
        setId(id);
        setStroke(color);
        setFill(color);
        setRadius(PropertiesController.CIRCLE_RADIUS);
    }
    
    /**
     * Position the center of the node at the given coordinates.
     * @param centerX
     * @param centerY 
     */
    @Override
    public void setTranslate(double centerX , double centerY) {
        
        setTranslateX(centerX);
        setTranslateY(centerY);
    }

    @Override
    public double getOffsetX() {
        return 0; // position is fixed to shape center
    }

    @Override
    public double getOffsetY() {
        return 0; // position is fixed to shape center
    }
    
    @Override
    public void addParentNode(IGravisNode parent) {
        parents.add(parent);
    }
    
    @Override
    public List<IGravisNode> getParents() {
        return parents;
    }
    
    @Override
    public void addChildNode(IGravisNode child) {
        children.add(child);
    }
    
    @Override
    public List<IGravisNode> getChildren() {
        return children;
    }
    
    @Override
    public void addEdge(GravisEdge edge) {
        edges.add(edge);
    }
    
    @Override
    public List<GravisEdge> getEdges() {
        return edges;
    }
    
    @Override
    public Shape getShape() {
        return this;
    }
}
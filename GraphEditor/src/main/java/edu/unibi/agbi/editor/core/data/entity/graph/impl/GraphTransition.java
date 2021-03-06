/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.editor.core.data.entity.graph.impl;

import edu.unibi.agbi.editor.core.data.entity.data.impl.DataTransition;
import edu.unibi.agbi.editor.core.data.entity.graph.IGraphArc;
import edu.unibi.agbi.editor.core.data.entity.graph.IGraphNode;
import edu.unibi.agbi.gravisfx.entity.root.GravisType;
import edu.unibi.agbi.gravisfx.entity.root.node.GravisRectangle;

/**
 *
 * @author PR
 */
public class GraphTransition extends GravisRectangle implements IGraphNode {
    
    private final DataTransition dataTransition;
    private boolean isDisabled = false;
    
    public GraphTransition(String id, DataTransition dataTransition) {
        super(id, GravisType.NODE);
        this.dataTransition = dataTransition;
        this.dataTransition.getShapes().add(this);
        this.setInnerCircleVisible(false);
    }
    
    @Override
    public DataTransition getData() {
        return dataTransition;
    }

    @Override
    public boolean isElementDisabled() {
        return isDisabled;
    }

    @Override
    public void setElementDisabled(boolean value) {

        if (isDisabled != value) {
            isDisabled = value;
            getElementHandles().forEach(handle -> handle.setDisabled(value));
            getConnections().forEach(conn -> {
                IGraphArc arc = (IGraphArc) conn;
                arc.setElementDisabled(value);
            });
        }
    }
}

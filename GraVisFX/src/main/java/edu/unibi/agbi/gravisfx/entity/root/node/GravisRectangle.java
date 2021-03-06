/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gravisfx.entity.root.node;

import edu.unibi.agbi.gravisfx.GravisProperties;
import edu.unibi.agbi.gravisfx.entity.root.GravisType;
import edu.unibi.agbi.gravisfx.entity.root.connection.IGravisConnection;
import edu.unibi.agbi.gravisfx.entity.child.GravisChildCircle;
import edu.unibi.agbi.gravisfx.entity.child.GravisChildLabel;
import edu.unibi.agbi.gravisfx.entity.child.GravisChildRectangle;
import edu.unibi.agbi.gravisfx.entity.util.GravisShapeHandle;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author PR
 */
public class GravisRectangle extends Rectangle implements IGravisNode
{
    private final List<GravisShapeHandle> shapeHandles = new ArrayList();
    private final List<Shape> shapes = new ArrayList();
    private final List<GravisChildLabel> labels = new ArrayList();

    private final Set<IGravisNode> children = new HashSet();
    private final Set<IGravisNode> parents = new HashSet();
    private final Set<IGravisConnection> connections = new HashSet();

    private final GravisChildCircle circle;
    private final GravisChildRectangle rectangle;
    
    private final GravisType type;

    /**
     * 
     * @param id
     * @param type 
     */
    public GravisRectangle(String id, GravisType type) {

        super();
        setId(id);
        setWidth(GravisProperties.RECTANGLE_WIDTH);
        setHeight(GravisProperties.RECTANGLE_HEIGHT);
        setArcWidth(GravisProperties.RECTANGLE_ARC_WIDTH);
        setArcHeight(GravisProperties.RECTANGLE_ARC_HEIGHT);
        this.type = type;

        labels.add(new GravisChildLabel(this));
        labels.get(0).xProperty().bind(translateXProperty().add(getCenterOffsetX() + GravisProperties.LABEL_OFFSET_X));
        labels.get(0).yProperty().bind(translateYProperty().add(getCenterOffsetY() + GravisProperties.LABEL_OFFSET_Y));
        
        circle = new GravisChildCircle(this);
        circle.setRadius(GravisProperties.CIRCLE_RADIUS - GravisProperties.BASE_INNER_DISTANCE);
        circle.translateXProperty().bind(translateXProperty());
        circle.translateYProperty().bind(translateYProperty());
        
        rectangle = new GravisChildRectangle(this);
        rectangle.setWidth(GravisProperties.RECTANGLE_WIDTH - GravisProperties.BASE_INNER_DISTANCE * 2);
        rectangle.setHeight(GravisProperties.RECTANGLE_HEIGHT - GravisProperties.BASE_INNER_DISTANCE * 2);
        rectangle.translateXProperty().bind(translateXProperty().add(GravisProperties.BASE_INNER_DISTANCE));
        rectangle.translateYProperty().bind(translateYProperty().add(GravisProperties.BASE_INNER_DISTANCE));
        
        shapes.add(this);
        shapes.add(circle);
        shapes.add(rectangle);

        shapeHandles.add(new GravisShapeHandle(this));
        shapeHandles.addAll(circle.getElementHandles());
        shapeHandles.addAll(rectangle.getElementHandles());
    }
    
    public GravisChildCircle getCircle() {
        return circle;
    }
    
    public GravisChildRectangle getRectangle() {
        return rectangle;
    }

    @Override
    public final double getCenterOffsetX() {
        return getWidth() / 2;
    }

    @Override
    public final double getCenterOffsetY() {
        return getHeight() / 2;
    }

    @Override
    public final Set<IGravisNode> getParents() {
        return parents;
    }

    @Override
    public final Set<IGravisNode> getChildren() {
        return children;
    }

    @Override
    public final Set<IGravisConnection> getConnections() {
        return connections;
    }

    @Override
    public final List<GravisChildLabel> getLabels() {
        return labels;
    }

    @Override
    public final List<GravisShapeHandle> getElementHandles() {
        return shapeHandles;
    }

    @Override
    public List<GravisShapeHandle> getRootHandles() {
        List<GravisShapeHandle> handles = new ArrayList();
        handles.add(shapeHandles.get(0));
        return handles;
    }

    @Override
    public List<GravisShapeHandle> getChildHandles() {
        List<GravisShapeHandle> handles = new ArrayList();
        handles.addAll(circle.getElementHandles());
        handles.addAll(rectangle.getElementHandles());
        return handles;
    }
    
    @Override 
    public GravisType getType() {
        return type;
    }

    @Override
    public List<Shape> getShapes() {
        return shapes;
    }

    @Override
    public void setInnerCircleVisible(boolean value) {
        this.circle.setVisible(value);
    }

    @Override
    public void setInnerRectangleVisible(boolean value) {
        this.rectangle.setVisible(value);
    }
    
    @Override
    public String toString() {
        return getId() + " (x = " + getTranslateX() + ", y = " + getTranslateY() + ")";
    }
}

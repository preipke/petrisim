/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gravisfx.entity.child;

import edu.unibi.agbi.gravisfx.entity.util.GravisShapeHandle;
import edu.unibi.agbi.gravisfx.GravisProperties;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import edu.unibi.agbi.gravisfx.entity.root.IGravisRoot;

/**
 *
 * @author PR
 */
public class GravisChildRectangle extends Rectangle implements IGravisChild
{
    private final List<GravisShapeHandle> elementHandles;
    private final IGravisRoot parentElement;

    public GravisChildRectangle(IGravisRoot parentNode) {

        super();

        this.parentElement = parentNode;

        elementHandles = new ArrayList();
        elementHandles.add(new GravisShapeHandle(this));

        setWidth(GravisProperties.RECTANGLE_WIDTH - GravisProperties.BASE_INNER_DISTANCE * 2);
        setHeight(GravisProperties.RECTANGLE_HEIGHT - GravisProperties.BASE_INNER_DISTANCE * 2);
        setArcWidth(GravisProperties.RECTANGLE_ARC_WIDTH - GravisProperties.BASE_INNER_DISTANCE * 2);
        setArcHeight(GravisProperties.RECTANGLE_ARC_HEIGHT - GravisProperties.BASE_INNER_DISTANCE * 2);
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
    public List<GravisShapeHandle> getElementHandles() {
        return elementHandles;
    }

    @Override
    public IGravisRoot getParentShape() {
        return parentElement;
    }

    @Override
    public List<Shape> getShapes() {
        List<Shape> shapes = new ArrayList();
        shapes.add(this);
        return shapes;
    }
}

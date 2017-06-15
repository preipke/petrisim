/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gnius.core.model.dao;

import edu.unibi.agbi.gravisfx.graph.Graph;
import edu.unibi.agbi.gravisfx.presentation.GraphPane;
import edu.unibi.agbi.petrinet.model.Model;
import java.io.File;
import java.time.LocalDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author PR
 */
public class DataDao
{
    private final Model model;
    private final Graph graphParent;
    private GraphPane graphPane;
    
    private int scalePower = 0;
    
    private final StringProperty name;
    private LocalDateTime creationDateTime;
    private String id;
    private String author;
    private String description;

    private File file;
    private boolean hasChanges;
    
    private int nextClusterId;
    private int nextNodeId;
    private int nextPlaceId;
    private int nextTransitionId;
    
    public DataDao() {
        name = new SimpleStringProperty();
        model = new Model();
        graphParent = new Graph(null);
        graphParent.nameProperty().bind(name);
        nextClusterId = 1;
        nextNodeId = 1;
        nextPlaceId = 1;
        nextTransitionId = 1;
    }
    
    public void clear() {
        graphParent.clear();
        model.clear();
        nextNodeId = 1;
        nextClusterId = 1;
        nextPlaceId = 1;
        nextTransitionId = 1;
    }
    
    public Model getModel() {
        return model;
    }
    
    public Graph getGraphRoot() {
        return graphParent;
    }
    
    public GraphPane getGraphPane() {
        return graphPane;
    }
    
    public void setGraphPane(GraphPane graphPane) {
        this.graphPane = graphPane;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public boolean hasChanges() {
        return hasChanges;
    }
    
    public void setHasChanges(boolean value) {
        hasChanges = value;
    }
    
    public int getNextClusterId() {
        return nextClusterId++;
    }
    
    public int getNextNodeId() {
        return nextNodeId++;
    }
    
    public int getNextPlaceId() {
        return nextPlaceId++;
    }
    
    public int getNextTransitionId() {
        return nextTransitionId++;
    }
    
    public int getScalePower() {
        return scalePower;
    }
    
    public void setScalePower(int power) {
        scalePower = power;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }
    
    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getModelDescription() {
        return description;
    }

    public void setModelDescription(String description) {
        this.description = description;
    }
    
    public String getDaoId() {
        return id;
    }
    
    public void setDaoId(String id) {
        this.id = id;
    }

    public String getModelName() {
        return name.get();
    }

    public void setModelName(String name) {
        this.name.set(name);
    }

    public StringProperty modelNameProperty() {
        return name;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.gnius.exception.controller;

/**
 *
 * @author pr
 */
public class GraphNotNullException extends Exception {
    
    public GraphNotNullException() {
        super();
    }
    
    public GraphNotNullException(String msg) {
        super(msg);
    }
    
    public GraphNotNullException(Throwable thr) {
        super(thr);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unibi.agbi.editor.business.exception;

/**
 *
 * @author PR
 */
public class ParameterException extends Exception
{
    public ParameterException(String msg) {
        super(msg);
    }
    
    public ParameterException(String msg, Throwable thr) {
        super(msg, thr);
    }
}

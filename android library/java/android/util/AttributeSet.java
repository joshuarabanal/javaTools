/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.util;

/**
 *
 * @author Joshua
 */
public interface AttributeSet {
    
    abstract boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue);

    abstract boolean getAttributeBooleanValue(int index, boolean defaultValue);

    abstract int getAttributeCount();
    
    abstract float getAttributeFloatValue(int index, float defaultValue);

    abstract float getAttributeFloatValue(String namespace, String attribute, float defaultValue);

    abstract int getAttributeIntValue(String namespace, String attribute, int defaultValue);

    abstract int getAttributeIntValue(int index, int defaultValue);

    abstract int getAttributeListValue(int index, String[] options, int defaultValue);

    abstract int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue);

    abstract String getAttributeName(int index);
    
    abstract int
getAttributeNameResource(int index);

    abstract int getAttributeResourceValue(String namespace, String attribute, int defaultValue);

    abstract int getAttributeResourceValue(int index, int defaultValue);
    
    
    
    
}

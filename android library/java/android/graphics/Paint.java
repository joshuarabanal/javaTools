/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.graphics;

/**
 *
 * @author Joshua
 */
public class Paint {
    private int color = 0xFF000000;
    public enum Style{ FILL, FILL_AND_STROKE, STROKE }
    private Style style = Style.STROKE;

    public int getColor() {
        return color;
    }
    public void setColor(int color){
        this.color = color;
    }
    public void setStyle(Style style){
        this.style = style;
    }
    public Style getStyle(){
        return style;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * see: https://developer.android.com/reference/android/graphics/Canvas.html#clipOutRect(float,%20float,%20float,%20float)
 * @author Joshua
 */
public class Canvas {
 
    /**
     * generic class for all types of clips
     * <ul>
     * <li> if top  = -1; clip is save clip</li>
     * </ul>
     */
    private class Clip{
        int top, left, width, height;
        boolean isSaveClip(){ return top == -1; }
    }
    
    private Graphics g;
    private Color currentColor;
    private ArrayList<Clip> clips = new ArrayList<Clip>();
    private JFrame frame;
    public Canvas(JFrame frame){
        this.frame = frame;
    }
    public void setGraphics(Graphics g){
        Rectangle rect = g.getClipBounds();
        if(rect.width == 0 || rect.height == 0){
            throw new NullPointerException("cannot draw on graphics of 0 width");
        }
        this.g = g;
    }
    /**
    public boolean clipOutPath(Path p){
        throw new UnsupportedOperationException("this function is not yet implemented");
    }
    **/
    public boolean clipOutRect(int left, int top, int right, int bottom){
        throw new UnsupportedOperationException("this function is not yet implemented");
    }
    /**
    boolean clipOutRect(RectF rect){
        return false;
    }
    boolean clipPath(Path path, Region.Op op){
        return false;
    }
    boolean clipPath(Path path){
        return false;
    }
    void clipRect(float left, float top, float right, float bottom, Region.Op op){
        
    }
    **/
    private void setColor(Paint color){
        g.setColor(new Color(color.getColor()));
    }
    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint){
        setColor(paint);
        g.drawLine((int)startX, (int)startY, (int)stopX, (int)stopY);
    }
    public void drawARGB(int alpha, int red, int green, int blue){
        g.setColor(new Color(alpha, red, green, blue));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    public void drawRect(float left, float top, float right, float bottom, Paint paint){
        setColor(paint);
        if(paint.getStyle() == Paint.Style.FILL || paint.getStyle() == Paint.Style.FILL_AND_STROKE){
            g.fillRect((int)left, (int)top, (int)(right-left), (int)(bottom-top));
        }
        if(paint.getStyle() == Paint.Style.STROKE  ||paint.getStyle() == Paint.Style.FILL_AND_STROKE){
            g.drawRect((int)left, (int)top, (int)(right-left), (int)(bottom-top));
        }
        
    }
    public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint){
        setColor(paint);
        g.drawString(text.toString().substring(start, end-start), (int)x, (int)y);
    }
    
    /**
     * 
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return true if the clip is possible, false if the clip is bigger than the current clip
     */
    public boolean clipRect(float left, float top, float right, float bottom){
        Clip c = new Clip();
        clips.add(c);
        
        c.top = (int) top;
        c.left = (int) left;
        c.width = (int) (right-left);
        c.height = (int) (bottom-top);
        g.setClip(c.left, c.top, c.width, c.height);
        return true;
    }
    /**
     * return to the clip under the last save
     */
    public void restore (){
        if(clips.size() == 0){ return; }
        Clip remove = clips.remove(clips.size()-1);
        if(!remove.isSaveClip()){
            restore();
        }
        g.setClip(remove.left, remove.top, remove.width, remove.height);
    }
    public void save(){
        Clip s = new Clip();
        clips.add(s);
        s.top = -1;
        
    }
    public int getWidth(){ return g.getClipBounds().width; }
    public int getHeight(){ return g.getClipBounds().height; }
    
    
}

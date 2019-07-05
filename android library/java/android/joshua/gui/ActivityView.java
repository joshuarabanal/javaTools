/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.joshua.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author Joshua
 */
public class ActivityView extends ViewGroup{
    private View root;
    
    public ActivityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    
    

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c); //To change body of generated methods, choose Tools | Templates.
        c.drawARGB(128, 128, 0, 0);
        Paint p = new Paint();
        p.setColor(0xFF00FF00);
        p.setStyle(Paint.Style.FILL);
        c.drawRect(0, 0, c.getWidth(), c.getHeight(), p);
        p.setColor(0xFFFF0000);
        c.drawLine(0, 0, c.getWidth(), c.getHeight(), p);
        if(root != null){
            root.onDraw(c);
        }
    }

    @Override
    public void addView(View child, int index) {
       root = child;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        root.layout(0, 0, width, height);
    }
    
    
    
}

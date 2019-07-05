/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import java.util.ArrayList;

/**
 *
 * @author Joshua
 */
public abstract class ViewGroup extends android.view.View{
    
    
    public ViewGroup(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);
    }
    
    @Override
    public void onDraw(Canvas c) {
      
    }
    
    public void addView(View child, int index){
    }
    protected abstract void onLayout (boolean changed,  int l, int t, int r, int b);
    public static class LayoutParams {
        public int width = 0, height = 0;

        public LayoutParams() {
        }
    }
    
    
}

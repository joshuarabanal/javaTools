/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;


/**
 *
 * @author Joshua
 */
public class View {
    private final int minimum_width, minimum_height;
    private int mMeasuredWidth,mMeasuredHeight;
    
    /**
     * positioning of this view relative to parents
     */
    protected int top,left,width,height;
    
    public View(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if(attrs == null){
            minimum_width = 0;
            minimum_height = 0;
        }
        else{

            int[] set = {
                    android.R.attr.minWidth, // idx 0
                    android.R.attr.minHeight       // idx 1
            };
            TypedArray a = context.obtainStyledAttributes(
				attrs,
				set
            );
            minimum_width = a.getDimensionPixelSize(0, 0);
            minimum_height = a.getDimensionPixelSize(1, 0);
            a.recycle();
        }
        
    }
    public void layout(int left, int top, int right, int bottom){
        this.top = top;
        this.left = left;
        this.width = right-left;
        this.height = bottom-top;
    }
    
    protected void onMeasure (int widthMeasureSpec, 
                int heightMeasureSpec){
        int width = minimum_width;
        int height = minimum_height;
        if(MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY){
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY){
            height= MeasureSpec.getSize(heightMeasureSpec);
        }
        if(MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST){
            width = 20;
        }
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST){
            height = 20;
        }
        setMeasuredDimension( width,height );
    }
    private void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        mMeasuredWidth = measuredWidth;
        mMeasuredHeight = measuredHeight;
    }
    
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    
    public void draw(Canvas c){
        c.save();
        c.clipRect(left, top, left+getWidth(), top+getHeight());
        onDraw(c);
        c.restore();
    }
    public void onDraw(Canvas c){
        
        
    }
    
    //-------------------------------------------------
    /**
     * A MeasureSpec encapsulates the layout requirements passed from parent to child.
     * Each MeasureSpec represents a requirement for either the width or the height.
     * A MeasureSpec is comprised of a size and a mode. There are three possible
     * modes:
     * <dl>
     * <dt>UNSPECIFIED</dt>
     * <dd>
     * The parent has not imposed any constraint on the child. It can be whatever size
     * it wants.
     * </dd>
     *
     * <dt>EXACTLY</dt>
     * <dd>
     * The parent has determined an exact size for the child. The child is going to be
     * given those bounds regardless of how big it wants to be.
     * </dd>
     *
     * <dt>AT_MOST</dt>
     * <dd>
     * The child can be as large as it wants up to the specified size.
     * </dd>
     * </dl>
     *
     * MeasureSpecs are implemented as ints to reduce object allocation. This class
     * is provided to pack and unpack the &lt;size, mode&gt; tuple into the int.
     */
    public static class MeasureSpec {
        
        private static final int MODE_SHIFT = 30;
        private static final int MODE_MASK  = 0x3 << MODE_SHIFT;


        /**
         * Measure specification mode: The parent has not imposed any constraint
         * on the child. It can be whatever size it wants.
         */
        public static final int UNSPECIFIED = 0 << MODE_SHIFT;

        /**
         * Measure specification mode: The parent has determined an exact size
         * for the child. The child is going to be given those bounds regardless
         * of how big it wants to be.
         */
        public static final int EXACTLY     = 1 << MODE_SHIFT;

        /**
         * Measure specification mode: The child can be as large as it wants up
         * to the specified size.
         */
        public static final int AT_MOST     = 2 << MODE_SHIFT;

        /**
         * Creates a measure specification based on the supplied size and mode.
         *
         * The mode must always be one of the following:
         * <ul>
         *  <li>{@link android.view.View.MeasureSpec#UNSPECIFIED}</li>
         *  <li>{@link android.view.View.MeasureSpec#EXACTLY}</li>
         *  <li>{@link android.view.View.MeasureSpec#AT_MOST}</li>
         * </ul>
         *
         * <p><strong>Note:</strong> On API level 17 and lower, makeMeasureSpec's
         * implementation was such that the order of arguments did not matter
         * and overflow in either value could impact the resulting MeasureSpec.
         * {@link android.widget.RelativeLayout} was affected by this bug.
         * Apps targeting API levels greater than 17 will get the fixed, more strict
         * behavior.</p>
         *
         * @param size the size of the measure specification
         * @param mode the mode of the measure specification
         * @return the measure specification based on size and mode
         */
        public static int makeMeasureSpec( int size,
                                           int mode) {
                return (size & ~MODE_MASK) | (mode & MODE_MASK);
        }

        /**
         * Like {@link #makeMeasureSpec(int, int)}, but any spec with a mode of UNSPECIFIED
         * will automatically get a size of 0. Older apps expect this.
         *
         * @hide internal use only for compatibility with system widgets and older apps
         */
        public static int makeSafeMeasureSpec(int size, int mode) {
            return makeMeasureSpec(size, mode);
        }

        /**
         * Extracts the mode from the supplied measure specification.
         *
         * @param measureSpec the measure specification to extract the mode from
         * @return {@link android.view.View.MeasureSpec#UNSPECIFIED},
         *         {@link android.view.View.MeasureSpec#AT_MOST} or
         *         {@link android.view.View.MeasureSpec#EXACTLY}
         */
        public static int getMode(int measureSpec) {
            //noinspection ResourceType
            return (measureSpec & MODE_MASK);
        }

        /**
         * Extracts the size from the supplied measure specification.
         *
         * @param measureSpec the measure specification to extract the size from
         * @return the size in pixels defined in the supplied measure specification
         */
        public static int getSize(int measureSpec) {
            return (measureSpec & ~MODE_MASK);
        }

        static int adjust(int measureSpec, int delta) {
            final int mode = getMode(measureSpec);
            int size = getSize(measureSpec);
            if (mode == UNSPECIFIED) {
                // No need to adjust size for UNSPECIFIED mode.
                return makeMeasureSpec(size, UNSPECIFIED);
            }
            size += delta;
            if (size < 0) {
                Log.e("android.view.View", "MeasureSpec.adjust: new size would be negative! (" + size +
                        ") spec: " + toString(measureSpec) + " delta: " + delta);
                size = 0;
            }
            return makeMeasureSpec(size, mode);
        }

        /**
         * Returns a String representation of the specified measure
         * specification.
         *
         * @param measureSpec the measure specification to convert to a String
         * @return a String with the following format: "MeasureSpec: MODE SIZE"
         */
        public static String toString(int measureSpec) {
            int mode = getMode(measureSpec);
            int size = getSize(measureSpec);

            StringBuilder sb = new StringBuilder("MeasureSpec: ");

            if (mode == UNSPECIFIED)
                sb.append("UNSPECIFIED ");
            else if (mode == EXACTLY)
                sb.append("EXACTLY ");
            else if (mode == AT_MOST)
                sb.append("AT_MOST ");
            else
                sb.append(mode).append(" ");

            sb.append(size);
            return sb.toString();
        }
    }

}

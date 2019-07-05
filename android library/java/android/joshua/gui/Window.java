package android.joshua.gui;


import android.app.Activity;
import android.graphics.Canvas;
import android.util.Log;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Joshua
 */
public class Window extends javax.swing.JPanel implements WindowListener{

        private MouseAdapter clikListener = new ClickEvents();
        private JFrame frame = new JFrame();
        private Activity act;
        private Canvas c = new Canvas(frame);
        
        
    public Window(){
        this.setBackground(new java.awt.Color(255, 255, 255));
        this.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.addMouseListener(clikListener);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(this);
        frame.getContentPane().add(this);
        frame.setSize(300, 600);
        


    //this.addMouseMotionListener(dragListener);
    }
    public void setActivity( Activity a){
        if(a == null){
            throw new NullPointerException("Activity not initialized");
        }
        this.act = a;
        if(act.rootView == null){
            throw new NullPointerException("root view not initialized");
        }
        act.rootView.layout(0,0,frame.getWidth(),frame.getHeight());
        frame.setVisible(true);
    }
    
    
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString("BLAH", 20, 20);
        g.drawRect(200, 200, 200, 200);
        g.setClip(0, 0, frame.getWidth(), frame.getHeight());
        g.drawLine(0, frame.getHeight(), frame.getWidth(), 0);
        c.setGraphics(g);
        
        act.rootView.onDraw(c);
        
    }

    @Override
    public void windowOpened(WindowEvent we) {
        Log.i("window state","opened");
    }

    @Override
    public void windowClosing(WindowEvent we) {
        Log.i("window state","closing");
    }

    @Override
    public void windowClosed(WindowEvent we) {
        Log.i("window state","closed");
    }

    @Override
    public void windowIconified(WindowEvent we) {
        Log.i("window state","iconified");
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
        Log.i("window state","deiconified");
    }

    @Override
    public void windowActivated(WindowEvent we) {
        Log.i("window state","activated");
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
        Log.i("window state","deactivated");
    }
    
    private class ClickEvents extends MouseAdapter{
        public void mousePressed(java.awt.event.MouseEvent evt) {
        }
        public void mouseReleased(java.awt.event.MouseEvent evt) {
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            super.mouseDragged(me); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            super.mouseWheelMoved(mwe); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    private class DragEvents extends MouseMotionAdapter{
        
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.content;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import java.io.File;

/**
 *
 * @author Joshua
 */
public class Context {
    public Context(){
        
    }
    public File getExternalFilesDir(String type){
        if(type == null){
            type = "files";
        }
        File retu =  new File(System.getProperty("user.dir"),type);
        retu.mkdirs();
        return retu;
    }

    public TypedArray obtainStyledAttributes(AttributeSet attrs, int[] set) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  

   
}

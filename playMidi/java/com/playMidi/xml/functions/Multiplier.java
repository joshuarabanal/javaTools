package com.playMidi.xml.functions;

import java.util.ArrayList;

import xml.XMLelement;

/**
 * Created by ra on 11/21/2016.
 */

public class Multiplier implements Function {
    private ArrayList<Function> functions = new ArrayList<Function>();
    private Function[] children;


    @Override
    public double valueAt(int x) {
        double retu = 1;
        for(Function child: children){
            retu = retu*child.valueAt(x);
        }
        return retu;
    }


    @Override
    public void addChild(XMLelement f) {
        functions.add((Function) f);
    }

    @Override
    public void closeElement() throws Exception {
        if(functions.size() == 0){
            throw new Exception("ATTRIBUTE_Multiplier needs child functions");
        }
        children = new Function[functions.size()];
        for(int i = 0; i<children.length; i++){
            children[i] = functions.get(i);
        }
    }
    public String toString(){
        String retu = "(";
        for(int i = 0; i < functions.size(); i++){
            if(i >0){ retu +="*";}
            retu += functions.get(i).toString();
        }
        return retu + ")";
    }
}

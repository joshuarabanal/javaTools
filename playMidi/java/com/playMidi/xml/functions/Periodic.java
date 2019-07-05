package com.playMidi.xml.functions;

import xml.XMLelement;

/**
 * Created by ra on 11/21/2016.
 */

public class Periodic implements Function {
    private Function function = null;
    @Override
    public double valueAt(int x) {
        double t = function.valueAt(x);
        return Math.sin(t);
    }

    @Override
    public void addChild(XMLelement f) {
        function = (Function) f;
    }

    @Override
    public void closeElement() throws Exception {
        if(function == null){
            throw new Exception("periodic has no child function");
        }
    }
    public String toString(){
        return "sin("+function.toString()+")";
    }
}

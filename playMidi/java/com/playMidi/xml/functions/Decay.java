package com.playMidi.xml.functions;

import xml.XMLelement;

/**
 * Created by ra on 11/21/2016.
 */

public class Decay implements Function {
    private Function function = null;

    @Override
    public double valueAt(int x) {
        return Math.exp(function.valueAt(x));
    }

    @Override
    public void addChild(XMLelement f) {
        function = (Function) f;
    }

    @Override
    public void closeElement() throws Exception {
        if(function == null){
            throw new Exception("decay has no child function");
        }
    }
    public String toString(){
        return "EXP("+function.toString()+")";
    }
}

package com.playMidi.xml.functions;

import xml.XMLelement;

/**
 * Created by ra on 2/3/2017.
 */

public class Delta implements Function {
    private Function function = null;

    @Override
    public double valueAt(int x) {
        return function.valueAt(x);
    }

    @Override
    public void addChild(XMLelement f) {
        function  = (Function) f;
    }

    @Override
    public void closeElement() throws Exception {
        if(function == null){
            throw new Exception("decay has no child function");
        }
    }
    public Function getFunction(){
        return function;
    }
    public String toString(){
        return "Amp("+function.toString()+")";
    }
}

package com.playMidi.xml.functions;

import xml.XMLelement;

/**
 * Created by ra on 11/21/2016.
 */

public interface Function extends XMLelement {
    public double valueAt(int x);
}

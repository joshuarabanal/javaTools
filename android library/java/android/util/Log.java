/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.util;


/**
 *
 * @author Joshua
 */
public class Log {
    private static final String INFO = "I";
    private static final String WARNING = "W";
    private static final String ERROR = "E";
    private static final String SEPARATOR = ":";
    public static void i(String tag, String body) {
        log(INFO, tag, body);
    }
    public static void w(String Tag, String body){
        log(WARNING, Tag, body);
    }
    private static void log(String type, String tag, String body){
        StackTraceElement stack = Thread.currentThread().getStackTrace()[3];//show line numbers
        System.out.print(type+SEPARATOR+tag+SEPARATOR+body);
        System.out.println("::\t"+stack.getClassName()+"."+stack.getMethodName()+"("+stack.getFileName()+"="+stack.getLineNumber()+")");
    }

    public static void e(String tag, String body) {
        log(ERROR, tag,body);
    }
}

package xml;

import java.util.ArrayList;
import java.util.Iterator;

import Analytics.CrashReporter;

public class NameValuePairList  extends ArrayList<NameValuePairList.NameValuePair>{
    //private ArrayList<NameValuePair> nvps;
    private String[] values;
    public NameValuePairList(String[] allPossibleValues){
        values = allPossibleValues;
        //nvps = new ArrayList<NameValuePair>();
    }
    public NameValuePairList() {
    	this(new String[] {});
    }

   
    
    
    public String getAttributeValue(int nameIndex){
        for(NameValuePair nvp : this){
            if(nvp.name  == nameIndex){//if the nvp has no name index the name index will be -1
                return nvp.getValue();
            }
        }
        return null;
    }
    public String getAttributeValue(String name){
        for(int i = 0; i<size(); i++){
            if(get(i).getName().equals(name)){
                return get(i).getValue();
            }
        }
        return null;
    }
    public void add(int name, String value){
        this.add(new NameValuePair(name,value, values));
    }
    public void add(xml.unoptimized.NameValuePair nvp){
        add(new NameValuePair(nvp.getName(), nvp.getValue()));
    }
    public void add(String name, String value){
        add(new NameValuePair(name, value));
    }
    public void add(byte[] buffer, int nameStart, int nameLength, int valueStart, int valueLength){
        int nameIndex = getIndex(buffer, nameStart,nameLength);
        int valueIndex = getIndex(buffer,valueStart,valueLength);
        if(nameIndex == -1 || valueIndex == -1){
            if(nameIndex ==-1 && valueIndex == -1){
                String nameString = new String(buffer, nameStart, nameLength);
                String valueString = new String(buffer, valueStart, valueLength);
                add(new NameValuePair(nameString,valueString));
            }
            else if(valueIndex == -1){
                add(
                        new NameValuePair(
                                nameIndex,
                                new String(buffer, valueStart,valueLength),
                                values
                        )
                );
            }
            else if(nameIndex ==-1){
                add(
                        new NameValuePair(
                            new String(buffer, nameStart,nameLength),
                            valueIndex,
                            values
                        )
                );
            }
        }
        else{
            add(
                    new NameValuePair(nameIndex, valueIndex, values)
            );
        }

    }
    private int getIndex(byte[] buffer, int start, int length){
        String s;
        int stringLength = length;
        for(int string = 0; string< values.length; string++){//find string in predetermined strings

            s = values[string];
            if(s.length() != stringLength){ continue; }
            else{
                boolean goodString = true;
                for(int i = 0; i<stringLength; i++){
                    if(buffer[start+i] != s.charAt(i)){  goodString = false; break; }
                }
                if(goodString){ return string; }
            }
        }
        return -1;
    }

    public String toString() {
    	String retu = "{";
    	for(int i = 0; i<size(); i++) {
    		retu += get(i)+",";
    	}
    	return retu +"}";
    }
    public static class NameValuePair implements xml.unoptimized.NameValuePair {
        int name = -1,value = -1;
        String nameString, valueString;
        String[] values;
        NameValuePair(int name, int value, String[] values){
            this.name = name;
            this.value = value;
            this.values = values;
        }
        NameValuePair( String name, int value, String[] values){
            this.nameString = name;
            this.value = value;
            this.values = values;
        }
        NameValuePair( int name, String value, String[] values){
            this.name = name;
            this.valueString = value;
            this.values = values;
        }
        public NameValuePair(String name, String value){
            this.nameString = name;
            this.valueString = value;
        }

        public String getName() {
            if(nameString != null){
                return nameString;
            }
            else{
                return new String(values[name]);
            }
        }
        public String getValue(){
            if(valueString != null){
                return valueString;
            }
            else if(values!= null){
                return new String(values[value]);
            }
            return null;
        }
        public int getNameIndex(){
            if(name == -1){
                CrashReporter.log("name error:"+nameString);
                throw new IndexOutOfBoundsException("name not set correctly");
            }
            return name;
        }
        public int getValueIndex(){
            if( value == -1){
                CrashReporter.log("value error:"+valueString);
                throw new IndexOutOfBoundsException("value not set correctly");
            }
            return value;
        }
        public String toString() {
        	return getName()+":"+getValue();
        }
		public void setValue(String text) {
			// TODO Auto-generated method stub
			this.valueString = text;
			this.value = -1;
		}
    }


}

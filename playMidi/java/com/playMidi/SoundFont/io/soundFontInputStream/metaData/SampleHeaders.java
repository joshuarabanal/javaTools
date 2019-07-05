package com.playMidi.SoundFont.io.soundFontInputStream.metaData;

/**
 * Created by ra on 5/5/2017.
 */

public class SampleHeaders {
    public static int SAMPLE_LIKE_TYPE_monoSample = 1;
    public static int SAMPLE_LIKE_TYPE_rightSample = 2;
    public static int SAMPLE_LIKE_TYPE_leftSample = 4;
    public static int SAMPLE_LIKE_TYPE_linkedSample = 8;
    public static int SAMPLE_LIKE_TYPE_RomMonoSample = 0x8001;
    public static int SAMPLE_LIKE_TYPE_RomRightSample = 0x8002;
    public static int SAMPLE_LIKE_TYPE_RomLeftSample = 0x8004;
    public static int SAMPLE_LIKE_TYPE_RomLinkedSample = 0x8008;

    public String hSampleName="unknown";
    private int start = -1;
    private int end = -1;
    private int startLoop = -1;
    private int endLoop = -1;
    private int sampleRate = 0;
    public int originalPitch = -1;
    public int pitchCorrection = 0;
    public int sampleLink = -1;
    public String fSampleType = "null";


    //setters
    public void setStart(int start){
        this.start = start;
    }
    public void setEnd(int end){
        this.end = end;
    }
    public void setStartLoop(int startLoop){
        this.startLoop = startLoop;
    }
    public void setEndLoop(int endLoop){
        this.endLoop = endLoop;
    }
    public void setSampleRate(int sampleRate){
        this.sampleRate = sampleRate;
    }
    //getters
    public int getStart(){
        return this.start;
    }
    public int getEnd(){
        return this.end;
    }
    public int getStartLoop(){
        return this.startLoop;
    }
    public int getEndLoop(){
        return this.endLoop;
    }
    public int getSampleRate(){
        return this.sampleRate;
    }


    @Override
    public String toString() {
        return "\n{"
                +"hSampleName:"+hSampleName
                +"start:"+start
                +"end:"+end
                +"startLoop:"+startLoop
                +"endLoop:"+endLoop
                +"ATTRIBUTE_sampleRate:"+sampleRate
                +"originalPitch:"+originalPitch
                +"pitchCorrection:"+pitchCorrection
                +"sampleLink:"+sampleLink
                +"fSampleType:"+fSampleType
                +"}";
    }

    public SampleHeaders(
            String hSampleName,
             int start,
             int end,
             int startLoop,
             int endLoop,
             int sampleRate,
             int originalPitch,
             int pitchCorrection,
             int sampleLink,
             String fSampleType
    ) {
        this.hSampleName = hSampleName;
        this.start = start;
        this.end = end;
        this.startLoop = startLoop;
        this.endLoop = endLoop;
        this.sampleRate = sampleRate;
        this.originalPitch = originalPitch;
        this.pitchCorrection = pitchCorrection;
        this.sampleLink = sampleLink;
        this.fSampleType = fSampleType;
    }
    public SampleHeaders(){

    }
    public int sapleTypeToInt(){
        if(fSampleType.contains("mono")){ return 1; }
        else if(fSampleType.contains("right")){ return 2; }
        else if(fSampleType.contains("left")){ return 4; }
        else if(fSampleType.contains("linked")){ return 8; }
        else {
            return 0;
        }
    }
    public int sampleRomtoInt(){
        if(fSampleType.indexOf("rom") == 0){
            return 0x80;
        }
        return 0;
    }
}

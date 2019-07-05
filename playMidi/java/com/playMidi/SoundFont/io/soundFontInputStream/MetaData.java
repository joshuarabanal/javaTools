package com.playMidi.SoundFont.io.soundFontInputStream;

/**
 * Created by ra on 5/5/2017.
 */

public class MetaData {
    // of the sound font header data
    /**
     * sound font file version this set was created from
     */
    public double version = -1;
    /*
    the engine this file was deigned for
     */
    public String soundEngine = "com.musicxml";
    /**
     * application that generated this file
     */
    public String creatorApplication = "com.musicxml";
    /**
     * the sound engine that the {@link #creatorApplication} intended this for
     */
    public String targetSoundEngine = "com.musicxml";
    /**
     * the product containing the {@link #targetSoundEngine} that this file was made for
     */
    public String intendedProduct = "com.musicxml";
    /**
     * company or person who created this file
     */
    public String engineersOfFile = "unknown";
    /**
     * date this file was created
     */
    public String creationDate = "unknown";
    /**
     * comments from the {@link #engineersOfFile} about the file, this is a usefull place to make website links
     */
    public String comments = "https://sites.google.com/view/joshuarabanal/home";
    /**
     * copyright statement about the file and its owners
     */
    public String copyright = "2017 This file was intended only for the use of com.musicxml any use of this file outside of its intended purposes is breaching this copyright";

    public String toString(){
        String logs = "";
        if (creatorApplication != null) {
            logs += "\n creatorApplication:" +  creatorApplication;
        }
        if ( soundEngine != null) {
            logs += "\n soundEngine:" +  soundEngine;
        }
        if ( engineersOfFile != null) {
            logs += "\n engineersOfFile:" +  engineersOfFile;
        }
        if ( comments != null) {
            logs += "\ncomments:" +  comments;
        }
        if ( copyright != null) {
            logs += "\ncopyright:" +  copyright;
        }
        if ( creationDate != null) {
            logs += "\ncreationDate:" +  creationDate;
        }
        if ( intendedProduct != null) {
            logs += "\nintendedProduct:" +  intendedProduct;
        }
        if ( version != -1) {
            logs += "\n version:" +  version;
        }
        if ( targetSoundEngine != null) {
            logs += "\ntargetSoundEngine:" +  targetSoundEngine;
        }
        return logs;
    }
}

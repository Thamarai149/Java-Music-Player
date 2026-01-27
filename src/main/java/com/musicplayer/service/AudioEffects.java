package com.musicplayer.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Audio Effects and Equalizer System
 * Provides various audio enhancements and frequency control
 */
public class AudioEffects {
    private boolean equalizerEnabled;
    private final Map<String, Integer> equalizerBands;
    private boolean bassBoostEnabled;
    private boolean reverbEnabled;
    private boolean virtualSurroundEnabled;
    private int bassBoostLevel;
    private int reverbLevel;
    private int virtualSurroundLevel;
    private float masterVolume;
    
    // Equalizer frequency bands (Hz)
    private static final String[] EQ_BANDS = {
        "60Hz", "170Hz", "310Hz", "600Hz", "1kHz", 
        "3kHz", "6kHz", "12kHz", "14kHz", "16kHz"
    };
    
    public AudioEffects() {
        this.equalizerEnabled = false;
        this.equalizerBands = new HashMap<>();
        this.bassBoostEnabled = false;
        this.reverbEnabled = false;
        this.virtualSurroundEnabled = false;
        this.bassBoostLevel = 0;
        this.reverbLevel = 0;
        this.virtualSurroundLevel = 0;
        this.masterVolume = 1.0f;
        
        // Initialize equalizer bands to 0 (neutral)
        for (String band : EQ_BANDS) {
            equalizerBands.put(band, 0);
        }
    }
    
    public void toggleEqualizer() {
        equalizerEnabled = !equalizerEnabled;
        System.out.println("[EQ] Equalizer: " + (equalizerEnabled ? "ON" : "OFF"));
    }
    
    public void setEqualizerBand(String frequency, int level) {
        if (equalizerBands.containsKey(frequency)) {
            // Clamp level between -12 and +12 dB
            level = Math.max(-12, Math.min(12, level));
            equalizerBands.put(frequency, level);
            System.out.printf("[EQ] EQ %s: %+d dB\n", frequency, level);
        }
    }
    
    public void resetEqualizer() {
        for (String band : EQ_BANDS) {
            equalizerBands.put(band, 0);
        }
        System.out.println("[EQ] Equalizer reset to flat");
    }
    
    public void loadEqualizerPreset(String presetName) {
        resetEqualizer();
        
        switch (presetName.toLowerCase()) {
            case "rock":
                setEqualizerBand("60Hz", 5);
                setEqualizerBand("170Hz", 3);
                setEqualizerBand("310Hz", -2);
                setEqualizerBand("600Hz", -1);
                setEqualizerBand("1kHz", 2);
                setEqualizerBand("3kHz", 4);
                setEqualizerBand("6kHz", 3);
                setEqualizerBand("12kHz", 2);
                break;
                
            case "pop":
                setEqualizerBand("60Hz", 2);
                setEqualizerBand("170Hz", 1);
                setEqualizerBand("310Hz", 0);
                setEqualizerBand("600Hz", 1);
                setEqualizerBand("1kHz", 3);
                setEqualizerBand("3kHz", 4);
                setEqualizerBand("6kHz", 3);
                setEqualizerBand("12kHz", 2);
                break;
                
            case "classical":
                setEqualizerBand("60Hz", 3);
                setEqualizerBand("170Hz", 2);
                setEqualizerBand("310Hz", 1);
                setEqualizerBand("600Hz", 0);
                setEqualizerBand("1kHz", -1);
                setEqualizerBand("3kHz", 1);
                setEqualizerBand("6kHz", 3);
                setEqualizerBand("12kHz", 4);
                break;
                
            case "jazz":
                setEqualizerBand("60Hz", 4);
                setEqualizerBand("170Hz", 2);
                setEqualizerBand("310Hz", 1);
                setEqualizerBand("600Hz", 2);
                setEqualizerBand("1kHz", -1);
                setEqualizerBand("3kHz", 1);
                setEqualizerBand("6kHz", 2);
                setEqualizerBand("12kHz", 3);
                break;
                
            case "electronic":
                setEqualizerBand("60Hz", 6);
                setEqualizerBand("170Hz", 4);
                setEqualizerBand("310Hz", 1);
                setEqualizerBand("600Hz", 0);
                setEqualizerBand("1kHz", -1);
                setEqualizerBand("3kHz", 2);
                setEqualizerBand("6kHz", 4);
                setEqualizerBand("12kHz", 5);
                break;
                
            default:
                System.out.println("[ERROR] Unknown preset: " + presetName);
                return;
        }
        
        equalizerEnabled = true;
        System.out.println("[EQ] Loaded EQ preset: " + presetName);
    }
    
    public void toggleBassBoost() {
        bassBoostEnabled = !bassBoostEnabled;
        if (bassBoostEnabled && bassBoostLevel == 0) {
            bassBoostLevel = 5; // Default boost level
        }
        System.out.println("[BASS] Bass Boost: " + (bassBoostEnabled ? "ON (" + bassBoostLevel + ")" : "OFF"));
    }
    
    public void setBassBoostLevel(int level) {
        bassBoostLevel = Math.max(0, Math.min(10, level));
        if (bassBoostLevel > 0) {
            bassBoostEnabled = true;
        }
        System.out.println("🔊 Bass Boost Level: " + bassBoostLevel);
    }
    
    public void toggleReverb() {
        reverbEnabled = !reverbEnabled;
        if (reverbEnabled && reverbLevel == 0) {
            reverbLevel = 3; // Default reverb level
        }
        System.out.println("[REVERB] Reverb: " + (reverbEnabled ? "ON (" + reverbLevel + ")" : "OFF"));
    }
    
    public void setReverbLevel(int level) {
        reverbLevel = Math.max(0, Math.min(10, level));
        if (reverbLevel > 0) {
            reverbEnabled = true;
        }
        System.out.println("[REVERB] Reverb Level: " + reverbLevel);
    }
    
    public void toggleVirtualSurround() {
        virtualSurroundEnabled = !virtualSurroundEnabled;
        if (virtualSurroundEnabled && virtualSurroundLevel == 0) {
            virtualSurroundLevel = 5; // Default surround level
        }
        System.out.println("[SURROUND] Virtual Surround: " + (virtualSurroundEnabled ? "ON (" + virtualSurroundLevel + ")" : "OFF"));
    }
    
    public void setVirtualSurroundLevel(int level) {
        virtualSurroundLevel = Math.max(0, Math.min(10, level));
        if (virtualSurroundLevel > 0) {
            virtualSurroundEnabled = true;
        }
        System.out.println("🎭 Virtual Surround Level: " + virtualSurroundLevel);
    }
    
    public void setMasterVolume(float volume) {
        masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        System.out.printf("🔊 Master Volume: %.0f%%\n", masterVolume * 100);
    }
    
    public void adjustMasterVolume(float delta) {
        setMasterVolume(masterVolume + delta);
    }
    
    public void displayCurrentSettings() {
        System.out.println("\n[EQ] === Audio Effects Settings ===");
        System.out.printf("Master Volume: %.0f%%\n", masterVolume * 100);
        System.out.println("Equalizer: " + (equalizerEnabled ? "ON" : "OFF"));
        
        if (equalizerEnabled) {
            System.out.println("EQ Bands:");
            for (String band : EQ_BANDS) {
                int level = equalizerBands.get(band);
                String bar = createLevelBar(level);
                System.out.printf("  %6s: %s %+2d dB\n", band, bar, level);
            }
        }
        
        System.out.println("Bass Boost: " + (bassBoostEnabled ? "ON (" + bassBoostLevel + ")" : "OFF"));
        System.out.println("Reverb: " + (reverbEnabled ? "ON (" + reverbLevel + ")" : "OFF"));
        System.out.println("Virtual Surround: " + (virtualSurroundEnabled ? "ON (" + virtualSurroundLevel + ")" : "OFF"));
    }
    
    private String createLevelBar(int level) {
        StringBuilder bar = new StringBuilder();
        int normalizedLevel = level + 12; // Convert -12 to +12 range to 0 to 24
        
        for (int i = 0; i < 24; i++) {
            if (i == 12) {
                bar.append("|"); // Center line
            } else if (i < normalizedLevel) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        
        return bar.toString();
    }
    
    public String[] getAvailablePresets() {
        return new String[]{"rock", "pop", "classical", "jazz", "electronic"};
    }
    
    // Getters
    public boolean isEqualizerEnabled() { return equalizerEnabled; }
    public Map<String, Integer> getEqualizerBands() { return new HashMap<>(equalizerBands); }
    public boolean isBassBoostEnabled() { return bassBoostEnabled; }
    public boolean isReverbEnabled() { return reverbEnabled; }
    public boolean isVirtualSurroundEnabled() { return virtualSurroundEnabled; }
    public int getBassBoostLevel() { return bassBoostLevel; }
    public int getReverbLevel() { return reverbLevel; }
    public int getVirtualSurroundLevel() { return virtualSurroundLevel; }
    public float getMasterVolume() { return masterVolume; }
    public String[] getEqualizerBandNames() { return EQ_BANDS; }
}
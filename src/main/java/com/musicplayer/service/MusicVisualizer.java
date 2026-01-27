package com.musicplayer.service;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ASCII-based Music Visualizer
 * Creates visual representations of audio playback
 */
public class MusicVisualizer {
    private boolean isEnabled;
    private boolean isRunning;
    private ScheduledExecutorService visualizerThread;
    private final Random random;
    private VisualizerMode currentMode;
    private int frameCount;
    
    public enum VisualizerMode {
        BARS, WAVE, SPECTRUM, PULSE, MATRIX
    }
    
    public MusicVisualizer() {
        this.isEnabled = false;
        this.isRunning = false;
        this.random = new Random();
        this.currentMode = VisualizerMode.BARS;
        this.frameCount = 0;
    }
    
    public void start() {
        if (isRunning) return;
        
        isRunning = true;
        visualizerThread = Executors.newSingleThreadScheduledExecutor();
        
        visualizerThread.scheduleAtFixedRate(() -> {
            if (isEnabled && isRunning) {
                clearScreen();
                renderFrame();
                frameCount++;
            }
        }, 0, 100, TimeUnit.MILLISECONDS); // 10 FPS
        
        System.out.println("[VIZ] Visualizer started");
    }
    
    public void stop() {
        if (!isRunning) return;
        
        isRunning = false;
        if (visualizerThread != null) {
            visualizerThread.shutdown();
        }
        System.out.println("[VIZ] Visualizer stopped");
    }
    
    public void toggle() {
        isEnabled = !isEnabled;
        if (isEnabled && !isRunning) {
            start();
        }
        System.out.println("[VIZ] Visualizer: " + (isEnabled ? "ON" : "OFF"));
    }
    
    public void setMode(VisualizerMode mode) {
        this.currentMode = mode;
        System.out.println("[VIZ] Visualizer mode: " + mode.name());
    }
    
    public void nextMode() {
        VisualizerMode[] modes = VisualizerMode.values();
        int currentIndex = currentMode.ordinal();
        currentMode = modes[(currentIndex + 1) % modes.length];
        System.out.println("[VIZ] Visualizer mode: " + currentMode.name());
    }
    
    private void clearScreen() {
        // ANSI escape code to clear screen and move cursor to top
        System.out.print("\033[2J\033[H");
    }
    
    private void renderFrame() {
        switch (currentMode) {
            case BARS:
                renderBars();
                break;
            case WAVE:
                renderWave();
                break;
            case SPECTRUM:
                renderSpectrum();
                break;
            case PULSE:
                renderPulse();
                break;
            case MATRIX:
                renderMatrix();
                break;
        }
    }
    
    private void renderBars() {
        System.out.println("[BARS] === MUSIC VISUALIZER - BARS MODE ===");
        System.out.println();
        
        int numBars = 20;
        int maxHeight = 10;
        
        // Generate random bar heights (simulating audio levels)
        for (int row = maxHeight; row >= 1; row--) {
            for (int bar = 0; bar < numBars; bar++) {
                int height = random.nextInt(maxHeight) + 1;
                if (height >= row) {
                    if (height > 8) {
                        System.out.print("█"); // High level - red
                    } else if (height > 5) {
                        System.out.print("▓"); // Medium level - yellow
                    } else {
                        System.out.print("▒"); // Low level - green
                    }
                } else {
                    System.out.print(" ");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        
        // Frequency labels
        System.out.println("60  170 310 600 1k  3k  6k  12k 14k 16k");
        System.out.println("Hz  Hz  Hz  Hz  Hz  Hz  Hz  Hz  Hz  Hz");
    }
    
    private void renderWave() {
        System.out.println("🌊 === MUSIC VISUALIZER - WAVE MODE ===");
        System.out.println();
        
        int width = 60;
        int height = 15;
        int centerY = height / 2;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double wave = Math.sin((x + frameCount) * 0.2) * 3 + 
                             Math.sin((x + frameCount) * 0.1) * 2;
                int waveY = (int) (centerY + wave);
                
                if (Math.abs(y - waveY) <= 1) {
                    if (Math.abs(wave) > 4) {
                        System.out.print("█");
                    } else if (Math.abs(wave) > 2) {
                        System.out.print("▓");
                    } else {
                        System.out.print("▒");
                    }
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    
    private void renderSpectrum() {
        System.out.println("[SPECTRUM] === MUSIC VISUALIZER - SPECTRUM MODE ===");
        System.out.println();
        
        String[] frequencies = {"60Hz", "170Hz", "310Hz", "600Hz", "1kHz", "3kHz", "6kHz", "12kHz"};
        
        for (String freq : frequencies) {
            int level = random.nextInt(20) + 1;
            System.out.printf("%6s |", freq);
            
            for (int i = 0; i < level; i++) {
                if (i < 5) {
                    System.out.print("▒"); // Low
                } else if (i < 12) {
                    System.out.print("▓"); // Medium
                } else {
                    System.out.print("█"); // High
                }
            }
            System.out.printf(" %d\n", level);
        }
    }
    
    private void renderPulse() {
        System.out.println("💓 === MUSIC VISUALIZER - PULSE MODE ===");
        System.out.println();
        
        int pulseSize = (int) (Math.sin(frameCount * 0.3) * 5 + 8);
        int centerX = 30;
        int centerY = 10;
        
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 60; x++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                
                if (Math.abs(distance - pulseSize) < 1.5) {
                    System.out.print("█");
                } else if (Math.abs(distance - pulseSize) < 2.5) {
                    System.out.print("▓");
                } else if (Math.abs(distance - pulseSize) < 3.5) {
                    System.out.print("▒");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    
    private void renderMatrix() {
        System.out.println("[MATRIX] === MUSIC VISUALIZER - MATRIX MODE ===");
        System.out.println();
        
        char[] chars = {'0', '1', '#', '%', ':', '.', '|', '-', '+', '*'};
        
        for (int y = 0; y < 15; y++) {
            for (int x = 0; x < 50; x++) {
                if (random.nextInt(10) < 3) { // 30% chance of character
                    char c = chars[random.nextInt(chars.length)];
                    System.out.print(c);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    
    public void displayModeHelp() {
        System.out.println("\n[VIZ] === Visualizer Modes ===");
        System.out.println("1. BARS - Frequency bars display");
        System.out.println("2. WAVE - Waveform visualization");
        System.out.println("3. SPECTRUM - Spectrum analyzer");
        System.out.println("4. PULSE - Pulsing circle effect");
        System.out.println("5. MATRIX - Matrix-style effect");
        System.out.println("\nUse 'viz mode <name>' to change modes");
        System.out.println("Use 'viz toggle' to enable/disable");
    }
    
    // Getters
    public boolean isEnabled() { return isEnabled; }
    public boolean isRunning() { return isRunning; }
    public VisualizerMode getCurrentMode() { return currentMode; }
}
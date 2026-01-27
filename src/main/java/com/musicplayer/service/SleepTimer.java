package com.musicplayer.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Sleep Timer Service
 * Automatically stops music playback after a specified duration
 */
public class SleepTimer {
    private boolean isActive;
    private LocalDateTime endTime;
    private final ScheduledExecutorService timerExecutor;
    private ScheduledFuture<?> timerTask;
    private final MusicPlayer musicPlayer;
    private SleepAction sleepAction;
    private int fadeOutDuration; // seconds
    
    public enum SleepAction {
        STOP, PAUSE, FADE_OUT
    }
    
    public SleepTimer(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
        this.isActive = false;
        this.sleepAction = SleepAction.FADE_OUT;
        this.fadeOutDuration = 10; // Default 10 seconds fade out
        this.timerExecutor = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void setTimer(int minutes) {
        setTimer(minutes, sleepAction);
    }
    
    public void setTimer(int minutes, SleepAction action) {
        if (minutes <= 0) {
            System.out.println("[ERROR] Timer duration must be positive");
            return;
        }
        
        cancelTimer(); // Cancel any existing timer
        
        this.sleepAction = action;
        this.endTime = LocalDateTime.now().plusMinutes(minutes);
        this.isActive = true;
        
        // Schedule the timer task
        timerTask = timerExecutor.schedule(() -> {
            executeTimerAction();
        }, minutes, TimeUnit.MINUTES);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.printf("[TIMER] Sleep timer set for %d minutes (until %s) - Action: %s\n", 
                         minutes, endTime.format(formatter), action.name());
        
        // Start countdown display
        startCountdownDisplay();
    }
    
    public void setTimerWithFadeOut(int minutes, int fadeSeconds) {
        this.fadeOutDuration = Math.max(1, Math.min(60, fadeSeconds)); // 1-60 seconds
        setTimer(minutes, SleepAction.FADE_OUT);
    }
    
    public void cancelTimer() {
        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel(false);
        }
        isActive = false;
        endTime = null;
        System.out.println("[TIMER] Sleep timer cancelled");
    }
    
    public void extendTimer(int additionalMinutes) {
        if (!isActive) {
            System.out.println("[ERROR] No active timer to extend");
            return;
        }
        
        // Cancel current timer and set new one
        int remainingMinutes = getRemainingMinutes();
        int newDuration = remainingMinutes + additionalMinutes;
        
        System.out.printf("[TIMER] Extending timer by %d minutes\n", additionalMinutes);
        setTimer(newDuration, sleepAction);
    }
    
    private void executeTimerAction() {
        if (!isActive) return;
        
        System.out.println("\n[TIMER] Sleep timer activated!");
        
        switch (sleepAction) {
            case STOP:
                musicPlayer.stop();
                System.out.println("🛑 Music stopped by sleep timer");
                break;
                
            case PAUSE:
                musicPlayer.pause();
                System.out.println("[PAUSE] Music paused by sleep timer");
                break;
                
            case FADE_OUT:
                performFadeOut();
                break;
        }
        
        isActive = false;
        endTime = null;
    }
    
    private void performFadeOut() {
        System.out.printf("🔉 Fading out over %d seconds...\n", fadeOutDuration);
        
        // Simulate fade out by gradually reducing volume
        ScheduledExecutorService fadeExecutor = Executors.newSingleThreadScheduledExecutor();
        
        final int steps = fadeOutDuration * 2; // 2 steps per second
        final long stepDelay = 500; // 500ms between steps
        
        for (int i = 0; i < steps; i++) {
            final int step = i;
            fadeExecutor.schedule(() -> {
                float volumeLevel = 1.0f - ((float) step / steps);
                
                if (step == steps - 1) {
                    // Final step - stop the music
                    musicPlayer.stop();
                    System.out.println("🔇 Fade out complete - Music stopped");
                    fadeExecutor.shutdown();
                } else {
                    // Show fade progress
                    if (step % 4 == 0) { // Update every 2 seconds
                        System.out.printf("🔉 Volume: %.0f%%\n", volumeLevel * 100);
                    }
                }
            }, i * stepDelay, TimeUnit.MILLISECONDS);
        }
    }
    
    private void startCountdownDisplay() {
        // Schedule periodic countdown updates
        ScheduledExecutorService countdownExecutor = Executors.newSingleThreadScheduledExecutor();
        
        countdownExecutor.scheduleAtFixedRate(() -> {
            if (!isActive) {
                countdownExecutor.shutdown();
                return;
            }
            
            int remaining = getRemainingMinutes();
            if (remaining <= 5 && remaining > 0) {
                System.out.printf("[TIMER] Sleep timer: %d minutes remaining\n", remaining);
            }
            
            if (remaining <= 0) {
                countdownExecutor.shutdown();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }
    
    public int getRemainingMinutes() {
        if (!isActive || endTime == null) return 0;
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) return 0;
        
        return (int) java.time.Duration.between(now, endTime).toMinutes();
    }
    
    public int getRemainingSeconds() {
        if (!isActive || endTime == null) return 0;
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) return 0;
        
        return (int) java.time.Duration.between(now, endTime).getSeconds();
    }
    
    public String getFormattedTimeRemaining() {
        if (!isActive) return "No timer active";
        
        int totalSeconds = getRemainingSeconds();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    public void displayStatus() {
        System.out.println("\n[TIMER] === Sleep Timer Status ===");
        
        if (!isActive) {
            System.out.println("Status: Inactive");
            System.out.println("Available actions: STOP, PAUSE, FADE_OUT");
            System.out.println("Usage: timer <minutes> [action]");
            return;
        }
        
        System.out.println("Status: Active");
        System.out.println("Time remaining: " + getFormattedTimeRemaining());
        System.out.println("Action: " + sleepAction.name());
        
        if (sleepAction == SleepAction.FADE_OUT) {
            System.out.println("Fade duration: " + fadeOutDuration + " seconds");
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println("End time: " + endTime.format(formatter));
    }
    
    public void setSleepAction(SleepAction action) {
        this.sleepAction = action;
        System.out.println("[TIMER] Sleep action set to: " + action.name());
    }
    
    public void setFadeOutDuration(int seconds) {
        this.fadeOutDuration = Math.max(1, Math.min(60, seconds));
        System.out.println("[TIMER] Fade out duration set to: " + fadeOutDuration + " seconds");
    }
    
    public void shutdown() {
        cancelTimer();
        if (timerExecutor != null && !timerExecutor.isShutdown()) {
            timerExecutor.shutdown();
        }
    }
    
    // Getters
    public boolean isActive() { return isActive; }
    public SleepAction getSleepAction() { return sleepAction; }
    public int getFadeOutDuration() { return fadeOutDuration; }
    public LocalDateTime getEndTime() { return endTime; }
}
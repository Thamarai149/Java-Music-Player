package com.musicplayer.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Song;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayer {
    private Song currentSong;
    private Playlist currentPlaylist;
    private int currentIndex;
    private boolean isPlaying;
    private boolean isPaused;
    private boolean shuffleMode;
    private boolean repeatMode;
    private List<Song> recentlyPlayed;
    private final List<Integer> shuffleOrder;
    private int shuffleIndex;
    
    // Audio playback components for WAV/AIFF/AU
    private Clip audioClip;
    private AudioInputStream audioStream;
    private long clipPosition = 0;
    
    // MP3 playback components (JLayer)
    private AdvancedPlayer mp3Player;
    private Thread mp3Thread;
    private FileInputStream mp3FileStream;
    private boolean isMp3File = false;
    
    // Simulation mode tracking (fallback)
    private boolean isSimulationMode = false;
    private long simulationStartTime = 0;
    private long simulationPauseTime = 0;
    
    private static final int MAX_RECENTLY_PLAYED = 20;
    
    public MusicPlayer() {
        this.currentIndex = 0;
        this.isPlaying = false;
        this.isPaused = false;
        this.shuffleMode = false;
        this.repeatMode = false;
        this.recentlyPlayed = new ArrayList<>();
        this.shuffleOrder = new ArrayList<>();
        this.shuffleIndex = 0;
    }
    
    public void loadPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        this.currentIndex = 0;
        this.shuffleIndex = 0;
        generateShuffleOrder();
        
        if (!playlist.isEmpty()) {
            this.currentSong = getCurrentSongFromPlaylist();
        }
    }
    
    public boolean play() {
        if (currentSong == null) return false;
        
        try {
            if (isPaused) {
                // Resume from pause
                if (isMp3File) {
                    // For MP3, we need to restart from beginning (JLayer limitation)
                    System.out.println(">> Restarting MP3: " + currentSong.getTitle());
                    return playNewSong();
                } else if (isSimulationMode) {
                    // Resume simulation
                    long pausedDuration = System.currentTimeMillis() - simulationPauseTime;
                    simulationStartTime += pausedDuration;
                    isPaused = false;
                    isPlaying = true;
                    System.out.println(">> Resumed: " + currentSong.getTitle());
                    return true;
                } else if (audioClip != null) {
                    // Resume WAV/AIFF/AU
                    audioClip.setMicrosecondPosition(clipPosition);
                    audioClip.start();
                    isPaused = false;
                    isPlaying = true;
                    System.out.println(">> Resumed: " + currentSong.getTitle());
                    return true;
                }
            }
            
            return playNewSong();
            
        } catch (Exception e) {
            System.out.println("X Error playing audio: " + e.getMessage());
            return startSimulation();
        }
    }
    
    private boolean playNewSong() {
        // Stop any currently playing audio
        stopAudio();
        
        File audioFile = new File(currentSong.getFilePath());
        if (!audioFile.exists()) {
            System.out.println("X Audio file not found: " + currentSong.getFilePath());
            return startSimulation();
        }
        
        String fileName = audioFile.getName().toLowerCase();
        isMp3File = fileName.endsWith(".mp3");
        
        if (isMp3File) {
            return playMp3File(audioFile);
        } else if (fileName.endsWith(".wav") || fileName.endsWith(".aiff") || fileName.endsWith(".au")) {
            return playRealAudioFile(audioFile);
        } else {
            // For other formats, use simulation
            return startSimulation();
        }
    }
    
    private boolean playMp3File(File audioFile) {
        try {
            mp3FileStream = new FileInputStream(audioFile);
            mp3Player = new AdvancedPlayer(mp3FileStream);
            
            // Add playback listener for when song ends
            mp3Player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    if (isPlaying && !isPaused) {
                        // Song finished, play next if repeat mode or move to next
                        if (repeatMode) {
                            playNewSong();
                        } else {
                            next();
                        }
                    }
                }
            });
            
            // Play MP3 in a separate thread
            mp3Thread = new Thread(() -> {
                try {
                    mp3Player.play();
                } catch (JavaLayerException e) {
                    if (isPlaying) { // Only show error if we're still trying to play
                        System.out.println("X Error playing MP3: " + e.getMessage());
                    }
                }
            });
            
            mp3Thread.start();
            isPlaying = true;
            isPaused = false;
            isSimulationMode = false;
            clipPosition = 0;
            
            System.out.println(">> Playing: " + currentSong.getTitle() + " - " + currentSong.getArtist() + " (" + formatDuration(currentSong.getDuration()) + ")");
            
            currentSong.setLastPlayed(LocalDateTime.now());
            addToRecentlyPlayed(currentSong);
            return true;
            
        } catch (IOException | JavaLayerException e) {
            System.out.println("X Error playing MP3 file: " + e.getMessage());
            System.out.println(">> Falling back to simulation mode");
            return startSimulation();
        }
    }
    
    private boolean playRealAudioFile(File audioFile) {
        try {
            audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            
            // Add listener for when song ends
            audioClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (isPlaying && !isPaused) {
                        // Song finished, play next if repeat mode or move to next
                        if (repeatMode) {
                            playCurrentSong();
                        } else {
                            next();
                        }
                    }
                }
            });
            
            audioClip.start();
            isPlaying = true;
            isPaused = false;
            isSimulationMode = false;
            clipPosition = 0;
            
            System.out.println(">> Playing: " + currentSong.getTitle() + " - " + currentSong.getArtist() + " (" + formatDuration(currentSong.getDuration()) + ")");
            
            currentSong.setLastPlayed(LocalDateTime.now());
            addToRecentlyPlayed(currentSong);
            return true;
            
        } catch (UnsupportedAudioFileException e) {
            System.out.println("X Unsupported audio format: " + audioFile.getName());
            return startSimulation();
        } catch (LineUnavailableException e) {
            System.out.println("X Audio line unavailable for: " + audioFile.getName());
            return startSimulation();
        } catch (IOException e) {
            System.out.println("X Error reading audio file: " + audioFile.getName());
            return startSimulation();
        }
    }
    
    private boolean startSimulation() {
        isPlaying = true;
        isPaused = false;
        isSimulationMode = true;
        simulationStartTime = System.currentTimeMillis();
        
        System.out.println(">> Playing (Simulation): " + currentSong.getTitle() + " - " + currentSong.getArtist() + " (" + formatDuration(currentSong.getDuration()) + ")");
        
        currentSong.setLastPlayed(LocalDateTime.now());
        addToRecentlyPlayed(currentSong);
        
        // Start a timer thread to auto-advance after song duration
        Thread simulationTimer = new Thread(() -> {
            try {
                Thread.sleep(currentSong.getDuration() * 1000L); // Convert to milliseconds
                if (isPlaying && isSimulationMode && !isPaused) {
                    // Song finished, play next if repeat mode or move to next
                    if (repeatMode) {
                        playNewSong();
                    } else {
                        next();
                    }
                }
            } catch (InterruptedException e) {
                // Timer was interrupted, ignore
            }
        });
        simulationTimer.setDaemon(true);
        simulationTimer.start();
        
        return true;
    }
    
    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }
    
    private void playCurrentSong() {
        if (audioClip != null) {
            audioClip.setMicrosecondPosition(0);
            audioClip.start();
        }
    }
    
    public void pause() {
        if (isPlaying) {
            if (isMp3File && mp3Player != null) {
                // For MP3, we need to stop (JLayer doesn't support pause/resume)
                stopAudio();
                isPaused = true;
                isPlaying = false;
                System.out.println("|| Paused: " + (currentSong != null ? currentSong.getTitle() : ""));
                System.out.println("   Note: MP3 will restart from beginning when resumed");
            } else if (isSimulationMode) {
                // Pause simulation
                simulationPauseTime = System.currentTimeMillis();
                isPaused = true;
                isPlaying = false;
                System.out.println("|| Paused: " + (currentSong != null ? currentSong.getTitle() : ""));
            } else if (audioClip != null && audioClip.isRunning()) {
                // Pause WAV/AIFF/AU
                clipPosition = audioClip.getMicrosecondPosition();
                audioClip.stop();
                isPaused = true;
                isPlaying = false;
                System.out.println("|| Paused: " + (currentSong != null ? currentSong.getTitle() : ""));
            }
        }
    }
    
    public void stop() {
        stopAudio();
        isPlaying = false;
        isPaused = false;
        clipPosition = 0;
        System.out.println("[] Stopped");
    }
    
    private void stopAudio() {
        // Stop WAV/AIFF/AU playback
        if (audioClip != null) {
            if (audioClip.isRunning()) {
                audioClip.stop();
            }
            audioClip.close();
            audioClip = null;
        }
        
        if (audioStream != null) {
            try {
                audioStream.close();
            } catch (IOException e) {
                // Ignore close errors
            }
            audioStream = null;
        }
        
        // Stop MP3 playback
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
        }
        
        if (mp3Thread != null && mp3Thread.isAlive()) {
            mp3Thread.interrupt();
            mp3Thread = null;
        }
        
        if (mp3FileStream != null) {
            try {
                mp3FileStream.close();
            } catch (IOException e) {
                // Ignore close errors
            }
            mp3FileStream = null;
        }
        
        // Reset simulation mode
        isSimulationMode = false;
        simulationStartTime = 0;
        simulationPauseTime = 0;
        isMp3File = false;
    }
    
    public boolean next() {
        if (currentPlaylist == null || currentPlaylist.isEmpty()) return false;
        
        // Stop current audio
        stopAudio();
        
        if (shuffleMode) {
            shuffleIndex = (shuffleIndex + 1) % shuffleOrder.size();
            currentIndex = shuffleOrder.get(shuffleIndex);
        } else {
            currentIndex = (currentIndex + 1) % currentPlaylist.size();
        }
        
        currentSong = getCurrentSongFromPlaylist();
        if (isPlaying || isPaused) {
            isPaused = false; // Reset pause state
            return playNewSong();
        }
        return true;
    }
    
    public boolean previous() {
        if (currentPlaylist == null || currentPlaylist.isEmpty()) return false;
        
        // Stop current audio
        stopAudio();
        
        if (shuffleMode) {
            shuffleIndex = (shuffleIndex - 1 + shuffleOrder.size()) % shuffleOrder.size();
            currentIndex = shuffleOrder.get(shuffleIndex);
        } else {
            currentIndex = (currentIndex - 1 + currentPlaylist.size()) % currentPlaylist.size();
        }
        
        currentSong = getCurrentSongFromPlaylist();
        if (isPlaying || isPaused) {
            isPaused = false; // Reset pause state
            return playNewSong();
        }
        return true;
    }
    
    public void toggleShuffle() {
        shuffleMode = !shuffleMode;
        if (shuffleMode) {
            generateShuffleOrder();
            // Find current song in shuffle order
            for (int i = 0; i < shuffleOrder.size(); i++) {
                if (shuffleOrder.get(i) == currentIndex) {
                    shuffleIndex = i;
                    break;
                }
            }
        }
    }
    
    public void toggleRepeat() {
        repeatMode = !repeatMode;
    }
    
    private void generateShuffleOrder() {
        if (currentPlaylist == null) return;
        
        shuffleOrder.clear();
        for (int i = 0; i < currentPlaylist.size(); i++) {
            shuffleOrder.add(i);
        }
        Collections.shuffle(shuffleOrder);
    }
    
    private Song getCurrentSongFromPlaylist() {
        if (currentPlaylist == null || currentIndex < 0 || currentIndex >= currentPlaylist.size()) {
            return null;
        }
        return currentPlaylist.getSong(currentIndex);
    }
    
    private void addToRecentlyPlayed(Song song) {
        recentlyPlayed.remove(song); // Remove if already exists
        recentlyPlayed.add(0, song); // Add to beginning
        
        // Keep only the most recent songs
        if (recentlyPlayed.size() > MAX_RECENTLY_PLAYED) {
            recentlyPlayed = recentlyPlayed.subList(0, MAX_RECENTLY_PLAYED);
        }
    }
    
    // Getters
    public Song getCurrentSong() { return currentSong; }
    public Playlist getCurrentPlaylist() { return currentPlaylist; }
    public boolean isPlaying() { return isPlaying; }
    public boolean isPaused() { return isPaused; }
    public boolean isShuffleMode() { return shuffleMode; }
    public boolean isRepeatMode() { return repeatMode; }
    public List<Song> getRecentlyPlayed() { return new ArrayList<>(recentlyPlayed); }
    public int getCurrentIndex() { return currentIndex; }
    
    public String getPlayerStatus() {
        if (currentSong == null) return "No song loaded";
        
        String status = isPlaying ? "Playing" : (isPaused ? "Paused" : "Stopped");
        String modes = "";
        if (shuffleMode) modes += " [Shuffle]";
        if (repeatMode) modes += " [Repeat]";
        
        // Add playback position if available
        String position = "";
        if (isMp3File && (isPlaying || isPaused)) {
            // For MP3, just show the total duration (JLayer doesn't provide position)
            int totalSeconds = currentSong.getDuration();
            position = String.format(" [%d:%02d]", totalSeconds / 60, totalSeconds % 60);
        } else if (isSimulationMode && (isPlaying || isPaused)) {
            // For simulation mode, calculate elapsed time
            long currentTime = isPaused ? simulationPauseTime : System.currentTimeMillis();
            long elapsedSeconds = (currentTime - simulationStartTime) / 1000;
            int totalSeconds = currentSong.getDuration();
            
            // Ensure elapsed time doesn't exceed total duration
            elapsedSeconds = Math.min(elapsedSeconds, totalSeconds);
            
            position = String.format(" [%d:%02d / %d:%02d]", 
                elapsedSeconds / 60, elapsedSeconds % 60,
                totalSeconds / 60, totalSeconds % 60);
        } else if (!isSimulationMode && !isMp3File && audioClip != null && (isPlaying || isPaused)) {
            // For real audio playback (WAV/AIFF/AU)
            long currentPos = isPaused ? clipPosition : audioClip.getMicrosecondPosition();
            long totalLength = audioClip.getMicrosecondLength();
            
            int currentSeconds = (int) (currentPos / 1_000_000);
            int totalSeconds = (int) (totalLength / 1_000_000);
            
            position = String.format(" [%d:%02d / %d:%02d]", 
                currentSeconds / 60, currentSeconds % 60,
                totalSeconds / 60, totalSeconds % 60);
        }
        
        return String.format("%s: %s%s%s", status, currentSong.toString(), modes, position);
    }
    
    public void cleanup() {
        stopAudio();
    }
    
    // Get supported audio formats
    public static String[] getSupportedFormats() {
        return new String[]{"MP3", "WAV", "AIFF", "AU"};
    }
    
    public static boolean isFormatSupported(String filename) {
        String extension = filename.toLowerCase();
        return extension.endsWith(".mp3") ||
               extension.endsWith(".wav") || 
               extension.endsWith(".aiff") || 
               extension.endsWith(".au");
    }
}
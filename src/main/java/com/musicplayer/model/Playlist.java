package com.musicplayer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
    private String name;
    private final List<Song> songs;
    
    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<Song> getSongs() { return new ArrayList<>(songs); }
    
    public void addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
        }
    }
    
    public boolean removeSong(Song song) {
        return songs.remove(song);
    }
    
    public boolean removeSong(int index) {
        if (index >= 0 && index < songs.size()) {
            songs.remove(index);
            return true;
        }
        return false;
    }
    
    public Song getSong(int index) {
        if (index >= 0 && index < songs.size()) {
            return songs.get(index);
        }
        return null;
    }
    
    public int size() {
        return songs.size();
    }
    
    public boolean isEmpty() {
        return songs.isEmpty();
    }
    
    public void shuffle() {
        Collections.shuffle(songs);
    }
    
    public int getTotalDuration() {
        return songs.stream().mapToInt(Song::getDuration).sum();
    }
    
    public String getFormattedTotalDuration() {
        int totalSeconds = getTotalDuration();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s (%d songs, %s)", name, songs.size(), getFormattedTotalDuration());
    }
}
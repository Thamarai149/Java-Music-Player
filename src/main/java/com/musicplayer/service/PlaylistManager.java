package com.musicplayer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Song;

public class PlaylistManager {
    private final Map<String, Playlist> playlists;
    private final MusicLibrary musicLibrary;
    
    public PlaylistManager(MusicLibrary musicLibrary) {
        this.playlists = new HashMap<>();
        this.musicLibrary = musicLibrary;
        createDefaultPlaylists();
    }
    
    private void createDefaultPlaylists() {
        createPlaylist("All Songs");
        createPlaylist("Favorites");
    }
    
    public boolean createPlaylist(String name) {
        if (playlists.containsKey(name)) {
            return false;
        }
        playlists.put(name, new Playlist(name));
        return true;
    }
    
    public boolean deletePlaylist(String name) {
        if ("All Songs".equals(name)) {
            return false; // Cannot delete default playlist
        }
        return playlists.remove(name) != null;
    }
    
    public Playlist getPlaylist(String name) {
        return playlists.get(name);
    }
    
    public List<Playlist> getAllPlaylists() {
        return new ArrayList<>(playlists.values());
    }
    
    public List<String> getPlaylistNames() {
        return new ArrayList<>(playlists.keySet());
    }
    
    public boolean addSongToPlaylist(String playlistName, Song song) {
        Playlist playlist = playlists.get(playlistName);
        if (playlist != null) {
            playlist.addSong(song);
            return true;
        }
        return false;
    }
    
    public boolean removeSongFromPlaylist(String playlistName, Song song) {
        Playlist playlist = playlists.get(playlistName);
        if (playlist != null) {
            return playlist.removeSong(song);
        }
        return false;
    }
    
    public boolean removeSongFromPlaylist(String playlistName, int index) {
        Playlist playlist = playlists.get(playlistName);
        if (playlist != null) {
            return playlist.removeSong(index);
        }
        return false;
    }
    
    public void refreshAllSongsPlaylist() {
        Playlist allSongs = playlists.get("All Songs");
        if (allSongs != null) {
            // Clear and repopulate with all songs from library
            allSongs = new Playlist("All Songs");
            for (Song song : musicLibrary.getAllSongs()) {
                allSongs.addSong(song);
            }
            playlists.put("All Songs", allSongs);
        }
    }
    
    public Playlist searchSongs(String query) {
        Playlist searchResults = new Playlist("Search Results");
        String lowerQuery = query.toLowerCase();
        
        for (Song song : musicLibrary.getAllSongs()) {
            if (song.getTitle().toLowerCase().contains(lowerQuery) ||
                song.getArtist().toLowerCase().contains(lowerQuery) ||
                song.getAlbum().toLowerCase().contains(lowerQuery)) {
                searchResults.addSong(song);
            }
        }
        
        return searchResults;
    }
    
    public void displayPlaylist(String playlistName) {
        Playlist playlist = playlists.get(playlistName);
        if (playlist == null) {
            System.out.println("Playlist not found: " + playlistName);
            return;
        }
        
        System.out.println("\n=== " + playlist.toString() + " ===");
        if (playlist.isEmpty()) {
            System.out.println("No songs in this playlist.");
            return;
        }
        
        List<Song> songs = playlist.getSongs();
        for (int i = 0; i < songs.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, songs.get(i).toString());
        }
    }
    
    public void displayAllPlaylists() {
        System.out.println("\n=== All Playlists ===");
        if (playlists.isEmpty()) {
            System.out.println("No playlists available.");
            return;
        }
        
        int index = 1;
        for (Playlist playlist : playlists.values()) {
            System.out.printf("%d. %s\n", index++, playlist.toString());
        }
    }
}
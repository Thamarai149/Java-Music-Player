package com.musicplayer.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.musicplayer.model.Song;

public class MusicLibrary {
    private final List<Song> songs;
    private static final String[] SUPPORTED_EXTENSIONS = {".mp3", ".wav", ".flac", ".m4a", ".aac", ".ogg"};
    
    public MusicLibrary() {
        this.songs = new ArrayList<>();
        // Start with completely empty library - songs will be loaded from folders only
    }
    
    public boolean loadMusicFromFolder(String folderPath) {
        File folder = new File(folderPath);
        
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("X Invalid folder path: " + folderPath);
            return false;
        }
        
        int songsAdded = 0;
        songsAdded += scanFolder(folder);
        
        System.out.println("+ Loaded " + songsAdded + " songs from: " + folderPath);
        return songsAdded > 0;
    }
    
    public List<String> detectMusicFolders() {
        List<String> musicFolders = new ArrayList<>();
        
        // Get user home directory
        String userHome = System.getProperty("user.home");
        String userName = System.getProperty("user.name");
        
        // Common music folder paths
        String[] commonPaths = {
            // User's specific path
            "C:\\Users\\" + userName + "\\Music\\poruilu",
            
            // Standard Windows music folders
            userHome + "\\Music",
            userHome + "\\Documents\\Music",
            userHome + "\\Downloads\\Music",
            "C:\\Users\\" + userName + "\\Music",
            "C:\\Users\\" + userName + "\\Documents\\Music",
            "C:\\Users\\" + userName + "\\Downloads\\Music",
            
            // Common drive locations
            "D:\\Music",
            "E:\\Music",
            "F:\\Music",
            "D:\\Songs",
            "E:\\Songs",
            "F:\\Songs",
            
            // Desktop music folders
            userHome + "\\Desktop\\Music",
            userHome + "\\Desktop\\Songs",
            
            // OneDrive music folders
            userHome + "\\OneDrive\\Music",
            userHome + "\\OneDrive\\Documents\\Music",
            
            // Google Drive music folders
            userHome + "\\Google Drive\\Music",
            
            // Dropbox music folders
            userHome + "\\Dropbox\\Music"
        };
        
        // Check each path
        for (String path : commonPaths) {
            File folder = new File(path);
            if (folder.exists() && folder.isDirectory()) {
                // Check if it contains music files
                if (containsMusicFiles(folder)) {
                    musicFolders.add(path);
                }
            }
        }
        
        // Also check for common music folder names in user directory
        File userDir = new File(userHome);
        if (userDir.exists() && userDir.isDirectory()) {
            File[] subDirs = userDir.listFiles(File::isDirectory);
            if (subDirs != null) {
                for (File dir : subDirs) {
                    String dirName = dir.getName().toLowerCase();
                    if (dirName.contains("music") || dirName.contains("song") || 
                        dirName.contains("audio") || dirName.contains("mp3") ||
                        dirName.contains("tamil") || dirName.contains("hindi") ||
                        dirName.contains("poruilu")) {
                        if (containsMusicFiles(dir)) {
                            musicFolders.add(dir.getAbsolutePath());
                        }
                    }
                }
            }
        }
        
        return musicFolders;
    }
    
    private boolean containsMusicFiles(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }
        
        File[] files = folder.listFiles();
        if (files == null) return false;
        
        int musicFileCount = 0;
        int maxCheck = 10; // Don't check too many files for performance
        
        for (File file : files) {
            if (maxCheck-- <= 0) break;
            
            if (file.isFile() && isSupportedAudioFile(file)) {
                musicFileCount++;
                if (musicFileCount >= 3) { // If we find 3+ music files, consider it a music folder
                    return true;
                }
            } else if (file.isDirectory()) {
                // Quick check of subdirectories
                File[] subFiles = file.listFiles();
                if (subFiles != null) {
                    for (File subFile : subFiles) {
                        if (subFile.isFile() && isSupportedAudioFile(subFile)) {
                            musicFileCount++;
                            if (musicFileCount >= 3) {
                                return true;
                            }
                        }
                        if (--maxCheck <= 0) break;
                    }
                }
            }
        }
        
        return musicFileCount > 0;
    }
    
    private int scanFolder(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        
        if (files == null) return 0;
        
        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively scan subdirectories
                count += scanFolder(file);
            } else if (file.isFile() && isSupportedAudioFile(file)) {
                Song song = createSongFromFile(file);
                if (song != null && !songs.contains(song)) {
                    songs.add(song);
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private boolean isSupportedAudioFile(File file) {
        String fileName = file.getName().toLowerCase();
        for (String extension : SUPPORTED_EXTENSIONS) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
    
    private Song createSongFromFile(File file) {
        try {
            String fileName = file.getName();
            String filePath = file.getAbsolutePath();
            
            // Use original filename as title (including extension)
            String title = fileName;
            String artist = "Unknown Artist";
            String album = "Unknown Album";
            
            // Estimate duration (in real implementation, you'd use audio libraries)
            int estimatedDuration = (int) (Math.random() * 300) + 120; // Random 2-7 minutes
            
            return new Song(title, artist, album, estimatedDuration, filePath);
            
        } catch (Exception e) {
            System.out.println("X Error processing file: " + file.getName() + " - " + e.getMessage());
            return null;
        }
    }
    
    public void clearLibrary() {
        songs.clear();
        System.out.println("+ Music library cleared.");
    }
    
    public void addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
        }
    }
    
    public boolean removeSong(Song song) {
        return songs.remove(song);
    }
    
    public List<Song> getAllSongs() {
        return new ArrayList<>(songs);
    }
    
    public List<Song> searchByTitle(String title) {
        List<Song> results = new ArrayList<>();
        String lowerTitle = title.toLowerCase();
        
        for (Song song : songs) {
            if (song.getTitle().toLowerCase().contains(lowerTitle)) {
                results.add(song);
            }
        }
        return results;
    }
    
    public List<Song> searchByArtist(String artist) {
        List<Song> results = new ArrayList<>();
        String lowerArtist = artist.toLowerCase();
        
        for (Song song : songs) {
            if (song.getArtist().toLowerCase().contains(lowerArtist)) {
                results.add(song);
            }
        }
        return results;
    }
    
    public List<Song> searchByAlbum(String album) {
        List<Song> results = new ArrayList<>();
        String lowerAlbum = album.toLowerCase();
        
        for (Song song : songs) {
            if (song.getAlbum().toLowerCase().contains(lowerAlbum)) {
                results.add(song);
            }
        }
        return results;
    }
    
    public Song getSongByIndex(int index) {
        if (index >= 0 && index < songs.size()) {
            return songs.get(index);
        }
        return null;
    }
    
    public int size() {
        return songs.size();
    }
    
    public void displayLibrary() {
        System.out.println("\n=== Music Library ===");
        if (songs.isEmpty()) {
            System.out.println("No songs in library.");
            return;
        }
        
        for (int i = 0; i < songs.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, songs.get(i).toString());
        }
    }
    
    public Map<String, List<Song>> getArtistGroups() {
        Map<String, List<Song>> artistGroups = new HashMap<>();
        
        for (Song song : songs) {
            artistGroups.computeIfAbsent(song.getArtist(), k -> new ArrayList<>()).add(song);
        }
        
        return artistGroups;
    }
    
    public Map<String, List<Song>> getAlbumGroups() {
        Map<String, List<Song>> albumGroups = new HashMap<>();
        
        for (Song song : songs) {
            albumGroups.computeIfAbsent(song.getAlbum(), k -> new ArrayList<>()).add(song);
        }
        
        return albumGroups;
    }
}
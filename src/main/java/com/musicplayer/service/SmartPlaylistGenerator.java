package com.musicplayer.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Song;

/**
 * Smart Playlist Generator
 * Creates dynamic playlists based on various criteria and algorithms
 */
public class SmartPlaylistGenerator {
    private final MusicLibrary musicLibrary;
    private final MusicStatistics statistics;
    private final Random random;
    
    public enum PlaylistType {
        RECENTLY_ADDED,
        MOST_PLAYED,
        RECENTLY_PLAYED,
        NEVER_PLAYED,
        RANDOM_MIX,
        ARTIST_FOCUS,
        MOOD_BASED,
        DECADE_MIX,
        WORKOUT,
        CHILL,
        DISCOVERY
    }
    
    public SmartPlaylistGenerator(MusicLibrary musicLibrary, MusicStatistics statistics) {
        this.musicLibrary = musicLibrary;
        this.statistics = statistics;
        this.random = new Random();
    }
    
    public Playlist generateSmartPlaylist(PlaylistType type, int maxSongs) {
        return generateSmartPlaylist(type, maxSongs, null);
    }
    
    public Playlist generateSmartPlaylist(PlaylistType type, int maxSongs, String parameter) {
        List<Song> allSongs = musicLibrary.getAllSongs();
        if (allSongs.isEmpty()) {
            return new Playlist("Empty Smart Playlist");
        }
        
        List<Song> selectedSongs = new ArrayList<>();
        String playlistName = "";
        
        switch (type) {
            case RECENTLY_ADDED:
                selectedSongs = generateRecentlyAdded(allSongs, maxSongs);
                playlistName = "Recently Added";
                break;
                
            case MOST_PLAYED:
                selectedSongs = generateMostPlayed(maxSongs);
                playlistName = "Most Played";
                break;
                
            case RECENTLY_PLAYED:
                selectedSongs = generateRecentlyPlayed(maxSongs);
                playlistName = "Recently Played";
                break;
                
            case NEVER_PLAYED:
                selectedSongs = generateNeverPlayed(allSongs, maxSongs);
                playlistName = "Never Played";
                break;
                
            case RANDOM_MIX:
                selectedSongs = generateRandomMix(allSongs, maxSongs);
                playlistName = "Random Mix";
                break;
                
            case ARTIST_FOCUS:
                selectedSongs = generateArtistFocus(allSongs, parameter, maxSongs);
                playlistName = "Artist Focus: " + (parameter != null ? parameter : "Various");
                break;
                
            case MOOD_BASED:
                selectedSongs = generateMoodBased(allSongs, parameter, maxSongs);
                playlistName = "Mood: " + (parameter != null ? parameter : "Mixed");
                break;
                
            case DECADE_MIX:
                selectedSongs = generateDecadeMix(allSongs, parameter, maxSongs);
                playlistName = "Decade: " + (parameter != null ? parameter : "Mixed");
                break;
                
            case WORKOUT:
                selectedSongs = generateWorkout(allSongs, maxSongs);
                playlistName = "Workout Mix";
                break;
                
            case CHILL:
                selectedSongs = generateChill(allSongs, maxSongs);
                playlistName = "Chill Vibes";
                break;
                
            case DISCOVERY:
                selectedSongs = generateDiscovery(allSongs, maxSongs);
                playlistName = "Discovery Mix";
                break;
        }
        
        Playlist playlist = new Playlist(playlistName);
        for (Song song : selectedSongs) {
            playlist.addSong(song);
        }
        
        System.out.printf("[SMART] Generated smart playlist: %s (%d songs)\n", 
                         playlistName, selectedSongs.size());
        
        return playlist;
    }
    
    private List<Song> generateRecentlyAdded(List<Song> allSongs, int maxSongs) {
        // Since we don't have actual file creation dates, simulate based on file path
        return allSongs.stream()
                .sorted((s1, s2) -> s2.getFilePath().compareTo(s1.getFilePath())) // Reverse alphabetical as proxy
                .limit(maxSongs)
                .collect(Collectors.toList());
    }
    
    private List<Song> generateMostPlayed(int maxSongs) {
        return statistics.getTopSongs(maxSongs);
    }
    
    private List<Song> generateRecentlyPlayed(int maxSongs) {
        return statistics.getRecentlyPlayed(maxSongs);
    }
    
    private List<Song> generateNeverPlayed(List<Song> allSongs, int maxSongs) {
        return allSongs.stream()
                .filter(song -> statistics.getPlayCount(song) == 0)
                .limit(maxSongs)
                .collect(Collectors.toList());
    }
    
    private List<Song> generateRandomMix(List<Song> allSongs, int maxSongs) {
        List<Song> shuffled = new ArrayList<>(allSongs);
        Collections.shuffle(shuffled, random);
        return shuffled.stream().limit(maxSongs).collect(Collectors.toList());
    }
    
    private List<Song> generateArtistFocus(List<Song> allSongs, String artist, int maxSongs) {
        if (artist == null || artist.trim().isEmpty()) {
            // Pick a random popular artist
            List<String> topArtists = statistics.getTopArtists(5);
            if (!topArtists.isEmpty()) {
                artist = topArtists.get(random.nextInt(topArtists.size()));
            } else {
                // Fallback to first artist found
                artist = allSongs.stream()
                        .map(Song::getArtist)
                        .findFirst()
                        .orElse("Unknown Artist");
            }
        }
        
        final String targetArtist = artist;
        return allSongs.stream()
                .filter(song -> song.getArtist().toLowerCase().contains(targetArtist.toLowerCase()))
                .limit(maxSongs)
                .collect(Collectors.toList());
    }
    
    private List<Song> generateMoodBased(List<Song> allSongs, String mood, int maxSongs) {
        // Simulate mood detection based on song title keywords
        List<String> energeticKeywords = Arrays.asList("rock", "dance", "party", "energy", "power", "fast", "beat");
        List<String> chillKeywords = Arrays.asList("chill", "relax", "calm", "soft", "acoustic", "ambient", "slow");
        List<String> sadKeywords = Arrays.asList("sad", "blue", "melancholy", "tears", "lonely", "broken");
        List<String> happyKeywords = Arrays.asList("happy", "joy", "sunshine", "smile", "love", "celebration");
        
        if (mood == null) mood = "mixed";
        
        List<Song> moodSongs;
        
        switch (mood.toLowerCase()) {
            case "energetic":
            case "upbeat":
                moodSongs = filterByKeywords(allSongs, energeticKeywords);
                break;
            case "chill":
            case "relaxed":
                moodSongs = filterByKeywords(allSongs, chillKeywords);
                break;
            case "sad":
            case "melancholy":
                moodSongs = filterByKeywords(allSongs, sadKeywords);
                break;
            case "happy":
            case "joyful":
                moodSongs = filterByKeywords(allSongs, happyKeywords);
                break;
            default:
                moodSongs = new ArrayList<>(allSongs);
                Collections.shuffle(moodSongs, random);
        }
        
        return moodSongs.stream().limit(maxSongs).collect(Collectors.toList());
    }
    
    private List<Song> generateDecadeMix(List<Song> allSongs, String decade, int maxSongs) {
        // Since we don't have actual release dates, simulate based on file names
        if (decade == null) decade = "2000s";
        
        final String targetDecade = decade.toLowerCase();
        
        List<Song> decadeSongs = allSongs.stream()
                .filter(song -> {
                    String fileName = song.getFilePath().toLowerCase();
                    switch (targetDecade) {
                        case "80s":
                        case "1980s":
                            return fileName.contains("80") || fileName.contains("classic");
                        case "90s":
                        case "1990s":
                            return fileName.contains("90") || fileName.contains("retro");
                        case "2000s":
                            return fileName.contains("2000") || fileName.contains("00");
                        case "2010s":
                            return fileName.contains("201") || fileName.contains("modern");
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
        
        if (decadeSongs.isEmpty()) {
            decadeSongs = new ArrayList<>(allSongs);
        }
        
        Collections.shuffle(decadeSongs, random);
        return decadeSongs.stream().limit(maxSongs).collect(Collectors.toList());
    }
    
    private List<Song> generateWorkout(List<Song> allSongs, int maxSongs) {
        // Prefer songs with energetic keywords and higher play counts
        List<String> workoutKeywords = Arrays.asList("rock", "electronic", "dance", "hip", "rap", "metal", "punk", "energy");
        
        List<Song> workoutSongs = filterByKeywords(allSongs, workoutKeywords);
        
        // Add some popular songs even if they don't match keywords
        List<Song> popularSongs = statistics.getTopSongs(maxSongs / 2);
        for (Song song : popularSongs) {
            if (!workoutSongs.contains(song)) {
                workoutSongs.add(song);
            }
        }
        
        Collections.shuffle(workoutSongs, random);
        return workoutSongs.stream().limit(maxSongs).collect(Collectors.toList());
    }
    
    private List<Song> generateChill(List<Song> allSongs, int maxSongs) {
        List<String> chillKeywords = Arrays.asList("acoustic", "ambient", "chill", "jazz", "classical", "soft", "piano", "guitar");
        
        List<Song> chillSongs = filterByKeywords(allSongs, chillKeywords);
        
        if (chillSongs.size() < maxSongs) {
            // Add random songs to fill up
            List<Song> remaining = new ArrayList<>(allSongs);
            remaining.removeAll(chillSongs);
            Collections.shuffle(remaining, random);
            
            int needed = maxSongs - chillSongs.size();
            chillSongs.addAll(remaining.stream().limit(needed).collect(Collectors.toList()));
        }
        
        return chillSongs.stream().limit(maxSongs).collect(Collectors.toList());
    }
    
    private List<Song> generateDiscovery(List<Song> allSongs, int maxSongs) {
        // Mix of never played and rarely played songs
        List<Song> neverPlayed = generateNeverPlayed(allSongs, maxSongs / 2);
        List<Song> rarelyPlayed = allSongs.stream()
                .filter(song -> statistics.getPlayCount(song) > 0 && statistics.getPlayCount(song) <= 2)
                .collect(Collectors.toList());
        
        Collections.shuffle(rarelyPlayed, random);
        
        List<Song> discovery = new ArrayList<>(neverPlayed);
        discovery.addAll(rarelyPlayed.stream().limit(maxSongs - neverPlayed.size()).collect(Collectors.toList()));
        
        Collections.shuffle(discovery, random);
        return discovery.stream().limit(maxSongs).collect(Collectors.toList());
    }
    
    private List<Song> filterByKeywords(List<Song> songs, List<String> keywords) {
        return songs.stream()
                .filter(song -> {
                    String searchText = (song.getTitle() + " " + song.getArtist() + " " + song.getAlbum()).toLowerCase();
                    return keywords.stream().anyMatch(searchText::contains);
                })
                .collect(Collectors.toList());
    }
    
    public Playlist generatePersonalizedMix(int maxSongs) {
        List<Song> allSongs = musicLibrary.getAllSongs();
        if (allSongs.isEmpty()) {
            return new Playlist("Empty Personalized Mix");
        }
        
        List<Song> personalizedSongs = new ArrayList<>();
        
        // 40% most played songs
        List<Song> topSongs = statistics.getTopSongs(maxSongs * 2 / 5);
        personalizedSongs.addAll(topSongs);
        
        // 30% recently played songs
        List<Song> recentSongs = statistics.getRecentlyPlayed(maxSongs * 3 / 10);
        for (Song song : recentSongs) {
            if (!personalizedSongs.contains(song)) {
                personalizedSongs.add(song);
            }
        }
        
        // 20% discovery (never/rarely played)
        List<Song> discoverySongs = generateDiscovery(allSongs, maxSongs / 5);
        for (Song song : discoverySongs) {
            if (!personalizedSongs.contains(song)) {
                personalizedSongs.add(song);
            }
        }
        
        // 10% random for variety
        List<Song> randomSongs = generateRandomMix(allSongs, maxSongs / 10);
        for (Song song : randomSongs) {
            if (!personalizedSongs.contains(song)) {
                personalizedSongs.add(song);
            }
        }
        
        Collections.shuffle(personalizedSongs, random);
        
        Playlist playlist = new Playlist("Personalized Mix");
        for (Song song : personalizedSongs.stream().limit(maxSongs).collect(Collectors.toList())) {
            playlist.addSong(song);
        }
        
        System.out.printf("[SMART] Generated personalized mix: %d songs\n", playlist.size());
        return playlist;
    }
    
    public void displayAvailableSmartPlaylists() {
        System.out.println("\n[SMART] === Smart Playlist Types ===");
        System.out.println("1. recently-added    - Recently added songs");
        System.out.println("2. most-played       - Your most played tracks");
        System.out.println("3. recently-played   - Recently played songs");
        System.out.println("4. never-played      - Songs you haven't heard");
        System.out.println("5. random-mix        - Random selection");
        System.out.println("6. artist-focus      - Focus on specific artist");
        System.out.println("7. mood-based        - Based on mood (energetic/chill/sad/happy)");
        System.out.println("8. decade-mix        - Songs from specific decade");
        System.out.println("9. workout           - High-energy workout mix");
        System.out.println("10. chill            - Relaxing chill vibes");
        System.out.println("11. discovery        - Discover new music");
        System.out.println("12. personalized     - AI-curated personal mix");
        System.out.println("\nUsage: smart <type> [size] [parameter]");
        System.out.println("Example: smart artist-focus 20 \"The Beatles\"");
    }
}
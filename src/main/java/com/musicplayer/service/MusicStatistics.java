package com.musicplayer.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.musicplayer.model.Song;

/**
 * Music Statistics and Analytics Service
 * Tracks listening habits, play counts, and generates insights
 */
public class MusicStatistics {
    private final Map<Song, Integer> playCount;
    private final Map<Song, Long> totalPlayTime; // in seconds
    private final Map<Song, LocalDateTime> lastPlayed;
    private final Map<LocalDate, Integer> dailyPlayCount;
    private final Map<String, Integer> artistPlayCount;
    private final Map<String, Integer> genrePlayCount;
    private final List<PlaySession> playSessions;
    private LocalDateTime sessionStart;
    private int currentSessionSongs;
    
    private static class PlaySession {
        LocalDateTime startTime;
        LocalDateTime endTime;
        int songsPlayed;
        
        PlaySession(LocalDateTime start) {
            this.startTime = start;
            this.songsPlayed = 0;
        }
        
        void endSession() {
            this.endTime = LocalDateTime.now();
        }
        
        long getSessionDuration() {
            LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
            return java.time.Duration.between(startTime, end).toMinutes();
        }
    }
    
    public MusicStatistics() {
        this.playCount = new HashMap<>();
        this.totalPlayTime = new HashMap<>();
        this.lastPlayed = new HashMap<>();
        this.dailyPlayCount = new HashMap<>();
        this.artistPlayCount = new HashMap<>();
        this.genrePlayCount = new HashMap<>();
        this.playSessions = new ArrayList<>();
        this.currentSessionSongs = 0;
    }
    
    public void startListeningSession() {
        sessionStart = LocalDateTime.now();
        currentSessionSongs = 0;
        System.out.println("[STATS] Started new listening session");
    }
    
    public void endListeningSession() {
        if (sessionStart != null) {
            PlaySession session = new PlaySession(sessionStart);
            session.songsPlayed = currentSessionSongs;
            session.endSession();
            playSessions.add(session);
            
            System.out.printf("[STATS] Session ended: %d songs, %d minutes\n", 
                             currentSessionSongs, (int) session.getSessionDuration());
            sessionStart = null;
        }
    }
    
    public void recordSongPlay(Song song) {
        if (song == null) return;
        
        // Update play count
        playCount.put(song, playCount.getOrDefault(song, 0) + 1);
        
        // Update total play time
        totalPlayTime.put(song, totalPlayTime.getOrDefault(song, 0L) + song.getDuration());
        
        // Update last played
        lastPlayed.put(song, LocalDateTime.now());
        
        // Update daily play count
        LocalDate today = LocalDate.now();
        dailyPlayCount.put(today, dailyPlayCount.getOrDefault(today, 0) + 1);
        
        // Update artist play count
        artistPlayCount.put(song.getArtist(), artistPlayCount.getOrDefault(song.getArtist(), 0) + 1);
        
        // Update session count
        currentSessionSongs++;
        
        System.out.printf("[STATS] Recorded play: %s (Total plays: %d)\n", 
                         song.getTitle(), playCount.get(song));
    }
    
    public void recordPartialPlay(Song song, long secondsPlayed) {
        if (song == null) return;
        
        // Only count as full play if more than 30 seconds or 50% of song
        long threshold = Math.min(30, song.getDuration() / 2);
        
        if (secondsPlayed >= threshold) {
            recordSongPlay(song);
        } else {
            // Just update play time without incrementing play count
            totalPlayTime.put(song, totalPlayTime.getOrDefault(song, 0L) + secondsPlayed);
            System.out.printf("[STATS] Recorded partial play: %s (%d seconds)\n", 
                             song.getTitle(), secondsPlayed);
        }
    }
    
    public List<Song> getTopSongs(int limit) {
        return playCount.entrySet().stream()
                .sorted(Map.Entry.<Song, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    public List<String> getTopArtists(int limit) {
        return artistPlayCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    public List<Song> getRecentlyPlayed(int limit) {
        return lastPlayed.entrySet().stream()
                .sorted(Map.Entry.<Song, LocalDateTime>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    public List<Song> getMostPlayedToday() {
        LocalDate today = LocalDate.now();
        return playCount.entrySet().stream()
                .filter(entry -> {
                    LocalDateTime lastPlay = lastPlayed.get(entry.getKey());
                    return lastPlay != null && lastPlay.toLocalDate().equals(today);
                })
                .sorted(Map.Entry.<Song, Integer>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    public void displayOverallStats() {
        System.out.println("\n[STATS] === Music Statistics Overview ===");
        
        int totalSongs = playCount.size();
        int totalPlays = playCount.values().stream().mapToInt(Integer::intValue).sum();
        long totalMinutes = totalPlayTime.values().stream().mapToLong(Long::longValue).sum() / 60;
        
        System.out.printf("Total unique songs played: %d\n", totalSongs);
        System.out.printf("Total plays: %d\n", totalPlays);
        System.out.printf("Total listening time: %d hours %d minutes\n", 
                         totalMinutes / 60, totalMinutes % 60);
        System.out.printf("Average plays per song: %.1f\n", 
                         totalSongs > 0 ? (double) totalPlays / totalSongs : 0);
        
        // Today's stats
        LocalDate today = LocalDate.now();
        int todayPlays = dailyPlayCount.getOrDefault(today, 0);
        System.out.printf("Songs played today: %d\n", todayPlays);
        
        // Session stats
        if (!playSessions.isEmpty()) {
            double avgSessionLength = playSessions.stream()
                    .mapToLong(PlaySession::getSessionDuration)
                    .average().orElse(0);
            System.out.printf("Total sessions: %d\n", playSessions.size());
            System.out.printf("Average session length: %.1f minutes\n", avgSessionLength);
        }
    }
    
    public void displayTopSongs(int limit) {
        System.out.printf("\n[MUSIC] === Top %d Songs ===\n", limit);
        
        List<Song> topSongs = getTopSongs(limit);
        for (int i = 0; i < topSongs.size(); i++) {
            Song song = topSongs.get(i);
            int plays = playCount.get(song);
            long minutes = totalPlayTime.getOrDefault(song, 0L) / 60;
            
            System.out.printf("%d. %s - %s (%d plays, %d min)\n", 
                             i + 1, song.getTitle(), song.getArtist(), plays, minutes);
        }
    }
    
    public void displayTopArtists(int limit) {
        System.out.printf("\n🎤 === Top %d Artists ===\n", limit);
        
        List<String> topArtists = getTopArtists(limit);
        for (int i = 0; i < topArtists.size(); i++) {
            String artist = topArtists.get(i);
            int plays = artistPlayCount.get(artist);
            
            System.out.printf("%d. %s (%d plays)\n", i + 1, artist, plays);
        }
    }
    
    public void displayListeningHistory(int days) {
        System.out.printf("\n📅 === Listening History (%d days) ===\n", days);
        
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            int plays = dailyPlayCount.getOrDefault(date, 0);
            String bar = createPlayBar(plays, 50); // Max 50 plays for full bar
            
            System.out.printf("%s: %s %d plays\n", 
                             date.format(formatter), bar, plays);
        }
    }
    
    private String createPlayBar(int plays, int maxPlays) {
        int barLength = 20;
        int filledLength = Math.min(barLength, (plays * barLength) / Math.max(1, maxPlays));
        
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        return bar.toString();
    }
    
    public void displayRecentSessions() {
        System.out.println("\n[SESSIONS] === Recent Listening Sessions ===");
        
        if (playSessions.isEmpty()) {
            System.out.println("No completed sessions recorded.");
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm");
        
        // Show last 5 sessions
        int start = Math.max(0, playSessions.size() - 5);
        for (int i = start; i < playSessions.size(); i++) {
            PlaySession session = playSessions.get(i);
            System.out.printf("%d. %s - %d songs, %.1f minutes\n", 
                             i + 1, 
                             session.startTime.format(formatter),
                             session.songsPlayed,
                             (double) session.getSessionDuration());
        }
        
        // Current session
        if (sessionStart != null) {
            long currentDuration = java.time.Duration.between(sessionStart, LocalDateTime.now()).toMinutes();
            System.out.printf("Current: %s - %d songs, %.1f minutes (ongoing)\n",
                             sessionStart.format(formatter),
                             currentSessionSongs,
                             (double) currentDuration);
        }
    }
    
    public void generateInsights() {
        System.out.println("\n[INSIGHTS] === Music Insights ===");
        
        if (playCount.isEmpty()) {
            System.out.println("Not enough data for insights.");
            return;
        }
        
        // Most played song
        Song topSong = getTopSongs(1).get(0);
        System.out.printf("[TOP] Most played song: %s (%d plays)\n", 
                         topSong.getTitle(), playCount.get(topSong));
        
        // Favorite artist
        if (!artistPlayCount.isEmpty()) {
            String topArtist = getTopArtists(1).get(0);
            System.out.printf("🎤 Favorite artist: %s (%d plays)\n", 
                             topArtist, artistPlayCount.get(topArtist));
        }
        
        // Listening patterns
        Map<Integer, Integer> hourlyPlays = new HashMap<>();
        for (LocalDateTime playTime : lastPlayed.values()) {
            int hour = playTime.getHour();
            hourlyPlays.put(hour, hourlyPlays.getOrDefault(hour, 0) + 1);
        }
        
        if (!hourlyPlays.isEmpty()) {
            int peakHour = hourlyPlays.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get().getKey();
            System.out.printf("[TIME] Peak listening hour: %02d:00\n", peakHour);
        }
        
        // Diversity score
        double diversityScore = (double) playCount.size() / Math.max(1, playCount.values().stream().mapToInt(Integer::intValue).sum());
        System.out.printf("[STATS] Music diversity: %.2f (higher = more diverse)\n", diversityScore);
    }
    
    public void resetStatistics() {
        playCount.clear();
        totalPlayTime.clear();
        lastPlayed.clear();
        dailyPlayCount.clear();
        artistPlayCount.clear();
        genrePlayCount.clear();
        playSessions.clear();
        sessionStart = null;
        currentSessionSongs = 0;
        System.out.println("[STATS] All statistics have been reset");
    }
    
    // Getters
    public int getPlayCount(Song song) {
        return playCount.getOrDefault(song, 0);
    }
    
    public long getTotalPlayTime(Song song) {
        return totalPlayTime.getOrDefault(song, 0L);
    }
    
    public LocalDateTime getLastPlayed(Song song) {
        return lastPlayed.get(song);
    }
    
    public int getTotalSongsPlayed() {
        return playCount.size();
    }
    
    public int getTotalPlays() {
        return playCount.values().stream().mapToInt(Integer::intValue).sum();
    }
}
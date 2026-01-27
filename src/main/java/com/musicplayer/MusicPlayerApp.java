package com.musicplayer;

import java.util.List;
import java.util.Scanner;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Song;
import com.musicplayer.service.*;

public class MusicPlayerApp {
    private final MusicLibrary musicLibrary;
    private final MusicPlayer musicPlayer;
    private final PlaylistManager playlistManager;
    private final AudioEffects audioEffects;
    private final MusicVisualizer visualizer;
    private final SleepTimer sleepTimer;
    private final MusicStatistics statistics;
    private final SmartPlaylistGenerator smartPlaylistGenerator;
    
    public MusicPlayerApp() {
        this.musicLibrary = new MusicLibrary();
        this.statistics = new MusicStatistics();
        this.musicPlayer = new MusicPlayer();
        this.playlistManager = new PlaylistManager(musicLibrary);
        this.audioEffects = new AudioEffects();
        this.visualizer = new MusicVisualizer();
        this.sleepTimer = new SleepTimer(musicPlayer);
        this.smartPlaylistGenerator = new SmartPlaylistGenerator(musicLibrary, statistics);
        
        // Start statistics session
        statistics.startListeningSession();
        
        // Start with empty library - user can load folders or add sample songs
    }
    
    public void start() {
        System.out.println("[MUSIC] Welcome to Enhanced Java Music Player! [MUSIC]");
        System.out.println("=".repeat(50));
        System.out.println("[NEW] NEW FEATURES: Audio Effects | Visualizer | Smart Playlists");
        System.out.println("[TIMER] Sleep Timer | [STATS] Statistics | [AI] AI Recommendations");
        System.out.println("=".repeat(50));
        
        // Check if library is empty and offer to load music
        if (musicLibrary.size() == 0) {
            System.out.println("Your music library is empty.");
            System.out.println("Load music from a folder using: Library Menu (9) -> Load Music Folder (7)");
            System.out.println();
            System.out.println("Audio Playback Support:");
            System.out.println("- Full playback: MP3, WAV, AIFF, AU files");
            System.out.println("- Simulation mode: FLAC, M4A, AAC, OGG files");
            System.out.println();
        } else {
            // Load default playlist if library has songs
            Playlist allSongs = playlistManager.getPlaylist("All Songs");
            if (allSongs != null && !allSongs.isEmpty()) {
                musicPlayer.loadPlaylist(allSongs);
                System.out.println("Loaded: " + allSongs.toString());
            }
        }
        
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                displayMenu();
                String choice = scanner.nextLine().trim();
                
                switch (choice.toLowerCase()) {
                    case "1":
                    case "play":
                        handlePlay();
                        break;
                    case "2":
                    case "pause":
                        handlePause();
                        break;
                    case "3":
                    case "stop":
                        handleStop();
                        break;
                    case "4":
                    case "next":
                        handleNext();
                        break;
                    case "5":
                    case "prev":
                    case "previous":
                        handlePrevious();
                        break;
                    case "6":
                    case "shuffle":
                        handleShuffle();
                        break;
                    case "7":
                    case "repeat":
                        handleRepeat();
                        break;
                    case "8":
                    case "playlist":
                        handlePlaylistMenu(scanner);
                        break;
                    case "9":
                    case "library":
                        handleLibraryMenu(scanner);
                        break;
                    case "10":
                    case "recent":
                        handleRecentlyPlayed();
                        break;
                    case "11":
                    case "status":
                        handleStatus();
                        break;
                    case "12":
                    case "effects":
                        handleAudioEffectsMenu(scanner);
                        break;
                    case "13":
                    case "viz":
                    case "visualizer":
                        handleVisualizerMenu(scanner);
                        break;
                    case "14":
                    case "timer":
                        handleSleepTimerMenu(scanner);
                        break;
                    case "15":
                    case "stats":
                        handleStatisticsMenu(scanner);
                        break;
                    case "16":
                    case "smart":
                        handleSmartPlaylistMenu(scanner);
                        break;
                    case "vol+":
                        audioEffects.adjustMasterVolume(0.1f);
                        break;
                    case "vol-":
                        audioEffects.adjustMasterVolume(-0.1f);
                        break;
                    case "help":
                        displayHelpMenu();
                        break;
                    case "0":
                    case "quit":
                    case "exit":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Type 'help' for all commands.");
                }
            }
        }
        
        System.out.println("Thanks for using Enhanced Java Music Player!");
        
        // Cleanup resources
        statistics.endListeningSession();
        visualizer.stop();
        sleepTimer.shutdown();
        musicPlayer.cleanup();
    }
    
    private void displayMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(musicPlayer.getPlayerStatus());
        System.out.printf("[VOL] Volume: %.0f%% | [EQ] Effects: %s | [VIZ] Visualizer: %s\n", 
                         audioEffects.getMasterVolume() * 100,
                         (audioEffects.isEqualizerEnabled() || audioEffects.isBassBoostEnabled() || 
                          audioEffects.isReverbEnabled()) ? "ON" : "OFF",
                         visualizer.isEnabled() ? "ON" : "OFF");
        if (sleepTimer.isActive()) {
            System.out.println("[TIMER] Sleep Timer: " + sleepTimer.getFormattedTimeRemaining());
        }
        System.out.println("=".repeat(60));
        System.out.println("🎵 PLAYBACK:");
        System.out.println("1. Play          2. Pause         3. Stop");
        System.out.println("4. Next          5. Previous      6. Toggle Shuffle");
        System.out.println("7. Toggle Repeat 8. Playlists     9. Library");
        System.out.println("10. Recently Played  11. Status");
        System.out.println();
        System.out.println("[NEW] NEW FEATURES:");
        System.out.println("12. Audio Effects    13. Visualizer   14. Sleep Timer");
        System.out.println("15. Statistics       16. Smart Playlists");
        System.out.println();
        System.out.println("⚡ QUICK: vol+/vol- | help | 0. Quit");
        System.out.print("\nEnter your choice: ");
    }
    
    private void handlePlay() {
        if (musicPlayer.play()) {
            System.out.println(">> Playing: " + musicPlayer.getCurrentSong());
            // Record play in statistics
            if (musicPlayer.getCurrentSong() != null) {
                statistics.recordSongPlay(musicPlayer.getCurrentSong());
            }
        } else {
            System.out.println("X No song to play. Please select a playlist first.");
        }
    }
    
    private void handlePause() {
        musicPlayer.pause();
        System.out.println("|| Paused");
    }
    
    private void handleStop() {
        musicPlayer.stop();
        System.out.println("[] Stopped");
    }
    
    private void handleNext() {
        if (musicPlayer.next()) {
            System.out.println(">> Next: " + musicPlayer.getCurrentSong());
        } else {
            System.out.println("X No next song available.");
        }
    }
    
    private void handlePrevious() {
        if (musicPlayer.previous()) {
            System.out.println("<< Previous: " + musicPlayer.getCurrentSong());
        } else {
            System.out.println("X No previous song available.");
        }
    }
    
    private void handleShuffle() {
        musicPlayer.toggleShuffle();
        String status = musicPlayer.isShuffleMode() ? "ON" : "OFF";
        System.out.println("~ Shuffle: " + status);
    }
    
    private void handleRepeat() {
        musicPlayer.toggleRepeat();
        String status = musicPlayer.isRepeatMode() ? "ON" : "OFF";
        System.out.println("@ Repeat: " + status);
    }
    
    private void handleStatus() {
        System.out.println("\n--- Player Status ---");
        System.out.println(musicPlayer.getPlayerStatus());
        
        Playlist current = musicPlayer.getCurrentPlaylist();
        if (current != null) {
            System.out.println("Current Playlist: " + current.toString());
            System.out.println("Track " + (musicPlayer.getCurrentIndex() + 1) + " of " + current.size());
        }
    }
    
    private void handleRecentlyPlayed() {
        List<Song> recent = musicPlayer.getRecentlyPlayed();
        System.out.println("\n--- Recently Played ---");
        
        if (recent.isEmpty()) {
            System.out.println("No recently played songs.");
            return;
        }
        
        for (int i = 0; i < recent.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, recent.get(i));
        }
    }
    
    private void handlePlaylistMenu(Scanner scanner) {
        boolean inPlaylistMenu = true;
        
        while (inPlaylistMenu) {
            System.out.println("\n--- Playlist Menu ---");
            System.out.println("1. View All Playlists    2. Create Playlist");
            System.out.println("3. Load Playlist         4. View Playlist Songs");
            System.out.println("5. Add Song to Playlist  6. Remove Song from Playlist");
            System.out.println("7. Delete Playlist       8. Search Songs");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    playlistManager.displayAllPlaylists();
                    break;
                case "2":
                    createPlaylist(scanner);
                    break;
                case "3":
                    loadPlaylist(scanner);
                    break;
                case "4":
                    viewPlaylistSongs(scanner);
                    break;
                case "5":
                    addSongToPlaylist(scanner);
                    break;
                case "6":
                    removeSongFromPlaylist(scanner);
                    break;
                case "7":
                    deletePlaylist(scanner);
                    break;
                case "8":
                    searchSongs(scanner);
                    break;
                case "0":
                    inPlaylistMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void handleLibraryMenu(Scanner scanner) {
        boolean inLibraryMenu = true;
        
        while (inLibraryMenu) {
            System.out.println("\n--- Library Menu ---");
            System.out.println("1. View All Songs        2. Search by Artist");
            System.out.println("3. Search by Album       4. Search by Title");
            System.out.println("5. View by Artists       6. View by Albums");
            System.out.println("7. Load Music Folder     8. Clear Library");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    musicLibrary.displayLibrary();
                    break;
                case "2":
                    searchByArtist(scanner);
                    break;
                case "3":
                    searchByAlbum(scanner);
                    break;
                case "4":
                    searchByTitle(scanner);
                    break;
                case "5":
                    viewByArtists();
                    break;
                case "6":
                    viewByAlbums();
                    break;
                case "7":
                    loadMusicFolder(scanner);
                    break;
                case "8":
                    clearLibrary(scanner);
                    break;
                case "0":
                    inLibraryMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void createPlaylist(Scanner scanner) {
        System.out.print("Enter playlist name: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("X Playlist name cannot be empty.");
            return;
        }
        
        if (playlistManager.createPlaylist(name)) {
            System.out.println("+ Playlist '" + name + "' created successfully.");
        } else {
            System.out.println("X Playlist '" + name + "' already exists.");
        }
    }
    
    private void loadPlaylist(Scanner scanner) {
        playlistManager.displayAllPlaylists();
        System.out.print("Enter playlist name to load: ");
        String name = scanner.nextLine().trim();
        
        Playlist playlist = playlistManager.getPlaylist(name);
        if (playlist != null) {
            musicPlayer.loadPlaylist(playlist);
            System.out.println("+ Loaded playlist: " + playlist.toString());
        } else {
            System.out.println("X Playlist not found: " + name);
        }
    }
    
    private void viewPlaylistSongs(Scanner scanner) {
        System.out.print("Enter playlist name: ");
        String name = scanner.nextLine().trim();
        playlistManager.displayPlaylist(name);
    }
    
    private void addSongToPlaylist(Scanner scanner) {
        System.out.print("Enter playlist name: ");
        String playlistName = scanner.nextLine().trim();
        
        Playlist playlist = playlistManager.getPlaylist(playlistName);
        if (playlist == null) {
            System.out.println("X Playlist not found: " + playlistName);
            return;
        }
        
        musicLibrary.displayLibrary();
        System.out.print("Enter song number to add: ");
        
        try {
            int songIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            Song song = musicLibrary.getSongByIndex(songIndex);
            
            if (song != null) {
                if (playlistManager.addSongToPlaylist(playlistName, song)) {
                    System.out.println("+ Added '" + song.getTitle() + "' to playlist '" + playlistName + "'");
                } else {
                    System.out.println("X Failed to add song to playlist.");
                }
            } else {
                System.out.println("X Invalid song number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("X Please enter a valid number.");
        }
    }
    
    private void removeSongFromPlaylist(Scanner scanner) {
        System.out.print("Enter playlist name: ");
        String playlistName = scanner.nextLine().trim();
        
        playlistManager.displayPlaylist(playlistName);
        System.out.print("Enter song number to remove: ");
        
        try {
            int songIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (playlistManager.removeSongFromPlaylist(playlistName, songIndex)) {
                System.out.println("+ Song removed from playlist.");
            } else {
                System.out.println("X Failed to remove song from playlist.");
            }
        } catch (NumberFormatException e) {
            System.out.println("X Please enter a valid number.");
        }
    }
    
    private void deletePlaylist(Scanner scanner) {
        playlistManager.displayAllPlaylists();
        System.out.print("Enter playlist name to delete: ");
        String name = scanner.nextLine().trim();
        
        if (playlistManager.deletePlaylist(name)) {
            System.out.println("+ Playlist '" + name + "' deleted successfully.");
        } else {
            System.out.println("X Cannot delete playlist '" + name + "' (not found or protected).");
        }
    }
    
    private void searchSongs(Scanner scanner) {
        System.out.print("Enter search query: ");
        String query = scanner.nextLine().trim();
        
        if (query.isEmpty()) {
            System.out.println("X Search query cannot be empty.");
            return;
        }
        
        Playlist searchResults = playlistManager.searchSongs(query);
        playlistManager.displayPlaylist("Search Results");
        
        if (!searchResults.isEmpty()) {
            System.out.print("Load search results as current playlist? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            
            if (response.equals("y") || response.equals("yes")) {
                musicPlayer.loadPlaylist(searchResults);
                System.out.println("+ Search results loaded as current playlist.");
            }
        }
    }
    
    private void searchByArtist(Scanner scanner) {
        System.out.print("Enter artist name: ");
        String artist = scanner.nextLine().trim();
        
        List<Song> results = musicLibrary.searchByArtist(artist);
        displaySearchResults("Artist: " + artist, results);
    }
    
    private void searchByAlbum(Scanner scanner) {
        System.out.print("Enter album name: ");
        String album = scanner.nextLine().trim();
        
        List<Song> results = musicLibrary.searchByAlbum(album);
        displaySearchResults("Album: " + album, results);
    }
    
    private void searchByTitle(Scanner scanner) {
        System.out.print("Enter song title: ");
        String title = scanner.nextLine().trim();
        
        List<Song> results = musicLibrary.searchByTitle(title);
        displaySearchResults("Title: " + title, results);
    }
    
    private void viewByArtists() {
        System.out.println("\n--- Artists ---");
        musicLibrary.getArtistGroups().forEach((artist, songs) -> {
            System.out.printf("%s (%d songs)\n", artist, songs.size());
        });
    }
    
    private void viewByAlbums() {
        System.out.println("\n--- Albums ---");
        musicLibrary.getAlbumGroups().forEach((album, songs) -> {
            System.out.printf("%s (%d songs)\n", album, songs.size());
        });
    }
    
    private void displaySearchResults(String searchType, List<Song> results) {
        System.out.println("\n--- Search Results for " + searchType + " ---");
        
        if (results.isEmpty()) {
            System.out.println("No songs found.");
            return;
        }
        
        for (int i = 0; i < results.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, results.get(i));
        }
    }
    
    private void loadMusicFolder(Scanner scanner) {
        System.out.print("Enter folder path (e.g., C:\\Music or /home/user/Music): ");
        String folderPath = scanner.nextLine().trim();
        
        if (folderPath.isEmpty()) {
            System.out.println("X Folder path cannot be empty.");
            return;
        }
        
        System.out.println("Scanning folder for music files...");
        if (musicLibrary.loadMusicFromFolder(folderPath)) {
            // Refresh the "All Songs" playlist
            playlistManager.refreshAllSongsPlaylist();
            
            // Auto-load the playlist if it's the first time loading music
            if (musicPlayer.getCurrentPlaylist() == null) {
                Playlist allSongs = playlistManager.getPlaylist("All Songs");
                if (allSongs != null && !allSongs.isEmpty()) {
                    musicPlayer.loadPlaylist(allSongs);
                    System.out.println("+ Auto-loaded 'All Songs' playlist.");
                }
            }
        }
    }
    
    private void clearLibrary(Scanner scanner) {
        System.out.print("Are you sure you want to clear the entire music library? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("y") || response.equals("yes")) {
            musicLibrary.clearLibrary();
            playlistManager.refreshAllSongsPlaylist();
            
            // Clear current playlist if it becomes empty
            if (musicPlayer.getCurrentPlaylist() != null && musicPlayer.getCurrentPlaylist().isEmpty()) {
                musicPlayer.loadPlaylist(null);
            }
        } else {
            System.out.println("Library clear cancelled.");
        }
    }
    
    // NEW FEATURE HANDLERS
    
    private void handleAudioEffectsMenu(Scanner scanner) {
        boolean inEffectsMenu = true;
        
        while (inEffectsMenu) {
            System.out.println("\n[EQ] === Audio Effects Menu ===");
            System.out.println("1. Toggle Equalizer      2. EQ Presets        3. Manual EQ");
            System.out.println("4. Toggle Bass Boost     5. Toggle Reverb     6. Virtual Surround");
            System.out.println("7. Master Volume         8. View Settings     9. Reset All");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    audioEffects.toggleEqualizer();
                    break;
                case "2":
                    handleEqualizerPresets(scanner);
                    break;
                case "3":
                    handleManualEqualizer(scanner);
                    break;
                case "4":
                    audioEffects.toggleBassBoost();
                    break;
                case "5":
                    audioEffects.toggleReverb();
                    break;
                case "6":
                    audioEffects.toggleVirtualSurround();
                    break;
                case "7":
                    handleVolumeControl(scanner);
                    break;
                case "8":
                    audioEffects.displayCurrentSettings();
                    break;
                case "9":
                    audioEffects.resetEqualizer();
                    audioEffects.setMasterVolume(1.0f);
                    System.out.println("[EQ] All audio effects reset");
                    break;
                case "0":
                    inEffectsMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void handleEqualizerPresets(Scanner scanner) {
        System.out.println("\n[EQ] Available EQ Presets:");
        String[] presets = audioEffects.getAvailablePresets();
        for (int i = 0; i < presets.length; i++) {
            System.out.printf("%d. %s\n", i + 1, presets[i]);
        }
        System.out.print("Select preset (1-" + presets.length + "): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= presets.length) {
                audioEffects.loadEqualizerPreset(presets[choice - 1]);
            } else {
                System.out.println("Invalid preset number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void handleManualEqualizer(Scanner scanner) {
        System.out.println("\n[EQ] Manual Equalizer (-12 to +12 dB):");
        String[] bands = audioEffects.getEqualizerBandNames();
        
        for (String band : bands) {
            System.out.printf("Set %s level (-12 to +12): ", band);
            try {
                int level = Integer.parseInt(scanner.nextLine().trim());
                audioEffects.setEqualizerBand(band, level);
            } catch (NumberFormatException e) {
                System.out.println("Skipping " + band + " - invalid number");
            }
        }
    }
    
    private void handleVolumeControl(Scanner scanner) {
        System.out.printf("Current volume: %.0f%%\n", audioEffects.getMasterVolume() * 100);
        System.out.print("Enter new volume (0-100): ");
        
        try {
            int volume = Integer.parseInt(scanner.nextLine().trim());
            audioEffects.setMasterVolume(volume / 100.0f);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void handleVisualizerMenu(Scanner scanner) {
        boolean inVisualizerMenu = true;
        
        while (inVisualizerMenu) {
            System.out.println("\n🎨 === Music Visualizer Menu ===");
            System.out.println("Status: " + (visualizer.isEnabled() ? "ON" : "OFF"));
            System.out.println("Mode: " + visualizer.getCurrentMode());
            System.out.println();
            System.out.println("1. Toggle On/Off         2. Change Mode       3. Next Mode");
            System.out.println("4. View Modes Help       0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    visualizer.toggle();
                    break;
                case "2":
                    handleVisualizerModeSelection(scanner);
                    break;
                case "3":
                    visualizer.nextMode();
                    break;
                case "4":
                    visualizer.displayModeHelp();
                    break;
                case "0":
                    inVisualizerMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void handleVisualizerModeSelection(Scanner scanner) {
        System.out.println("\n[VIZ] Select Visualizer Mode:");
        MusicVisualizer.VisualizerMode[] modes = MusicVisualizer.VisualizerMode.values();
        for (int i = 0; i < modes.length; i++) {
            System.out.printf("%d. %s\n", i + 1, modes[i].name());
        }
        System.out.print("Select mode (1-" + modes.length + "): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= modes.length) {
                visualizer.setMode(modes[choice - 1]);
            } else {
                System.out.println("Invalid mode number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void handleSleepTimerMenu(Scanner scanner) {
        boolean inTimerMenu = true;
        
        while (inTimerMenu) {
            System.out.println("\n[TIMER] === Sleep Timer Menu ===");
            sleepTimer.displayStatus();
            System.out.println();
            System.out.println("1. Set Timer             2. Cancel Timer      3. Extend Timer");
            System.out.println("4. Set Action            5. Set Fade Duration 0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleSetSleepTimer(scanner);
                    break;
                case "2":
                    sleepTimer.cancelTimer();
                    break;
                case "3":
                    handleExtendTimer(scanner);
                    break;
                case "4":
                    handleSetSleepAction(scanner);
                    break;
                case "5":
                    handleSetFadeDuration(scanner);
                    break;
                case "0":
                    inTimerMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void handleSetSleepTimer(Scanner scanner) {
        System.out.print("Enter timer duration in minutes: ");
        try {
            int minutes = Integer.parseInt(scanner.nextLine().trim());
            sleepTimer.setTimer(minutes);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void handleExtendTimer(Scanner scanner) {
        System.out.print("Extend timer by how many minutes: ");
        try {
            int minutes = Integer.parseInt(scanner.nextLine().trim());
            sleepTimer.extendTimer(minutes);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void handleSetSleepAction(Scanner scanner) {
        System.out.println("Sleep Actions:");
        System.out.println("1. STOP - Stop playback");
        System.out.println("2. PAUSE - Pause playback");
        System.out.println("3. FADE_OUT - Gradually fade out");
        System.out.print("Select action (1-3): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            SleepTimer.SleepAction[] actions = SleepTimer.SleepAction.values();
            if (choice >= 1 && choice <= actions.length) {
                sleepTimer.setSleepAction(actions[choice - 1]);
            } else {
                System.out.println("Invalid action number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void handleSetFadeDuration(Scanner scanner) {
        System.out.print("Enter fade out duration in seconds (1-60): ");
        try {
            int seconds = Integer.parseInt(scanner.nextLine().trim());
            sleepTimer.setFadeOutDuration(seconds);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
    
    private void handleStatisticsMenu(Scanner scanner) {
        boolean inStatsMenu = true;
        
        while (inStatsMenu) {
            System.out.println("\n📊 === Music Statistics Menu ===");
            System.out.println("1. Overall Stats         2. Top Songs         3. Top Artists");
            System.out.println("4. Listening History     5. Recent Sessions   6. Insights");
            System.out.println("7. Today's Stats         8. Reset Stats       0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    statistics.displayOverallStats();
                    break;
                case "2":
                    System.out.print("Show top how many songs (default 10): ");
                    String input = scanner.nextLine().trim();
                    try {
                        int limit = input.isEmpty() ? 10 : Integer.parseInt(input);
                        statistics.displayTopSongs(limit);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number, using default (10)");
                        statistics.displayTopSongs(10);
                    }
                    break;
                case "3":
                    System.out.print("Show top how many artists (default 10): ");
                    input = scanner.nextLine().trim();
                    try {
                        int limit = input.isEmpty() ? 10 : Integer.parseInt(input);
                        statistics.displayTopArtists(limit);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number, using default (10)");
                        statistics.displayTopArtists(10);
                    }
                    break;
                case "4":
                    System.out.print("Show history for how many days (default 7): ");
                    input = scanner.nextLine().trim();
                    try {
                        int days = input.isEmpty() ? 7 : Integer.parseInt(input);
                        statistics.displayListeningHistory(days);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number, using default (7)");
                        statistics.displayListeningHistory(7);
                    }
                    break;
                case "5":
                    statistics.displayRecentSessions();
                    break;
                case "6":
                    statistics.generateInsights();
                    break;
                case "7":
                    List<Song> todaySongs = statistics.getMostPlayedToday();
                    System.out.println("\n📅 Today's Most Played:");
                    for (int i = 0; i < todaySongs.size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, todaySongs.get(i));
                    }
                    break;
                case "8":
                    System.out.print("Are you sure you want to reset all statistics? (y/n): ");
                    if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
                        statistics.resetStatistics();
                    }
                    break;
                case "0":
                    inStatsMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void handleSmartPlaylistMenu(Scanner scanner) {
        boolean inSmartMenu = true;
        
        while (inSmartMenu) {
            System.out.println("\n🎯 === Smart Playlists Menu ===");
            System.out.println("1. Generate Smart Playlist  2. Personalized Mix     3. Available Types");
            System.out.println("4. Quick Workout Mix        5. Quick Chill Mix      0. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleGenerateSmartPlaylist(scanner);
                    break;
                case "2":
                    generatePersonalizedMix(scanner);
                    break;
                case "3":
                    smartPlaylistGenerator.displayAvailableSmartPlaylists();
                    break;
                case "4":
                    generateQuickPlaylist(SmartPlaylistGenerator.PlaylistType.WORKOUT, "Workout Mix");
                    break;
                case "5":
                    generateQuickPlaylist(SmartPlaylistGenerator.PlaylistType.CHILL, "Chill Mix");
                    break;
                case "0":
                    inSmartMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private void handleGenerateSmartPlaylist(Scanner scanner) {
        smartPlaylistGenerator.displayAvailableSmartPlaylists();
        System.out.print("\nEnter playlist type: ");
        String typeStr = scanner.nextLine().trim().toLowerCase().replace("-", "_");
        
        System.out.print("Enter max songs (default 25): ");
        String sizeStr = scanner.nextLine().trim();
        
        System.out.print("Enter parameter (optional, for artist-focus/mood/decade): ");
        String parameter = scanner.nextLine().trim();
        if (parameter.isEmpty()) parameter = null;
        
        try {
            int maxSongs = sizeStr.isEmpty() ? 25 : Integer.parseInt(sizeStr);
            
            try {
                SmartPlaylistGenerator.PlaylistType type = SmartPlaylistGenerator.PlaylistType.valueOf(typeStr.toUpperCase());
                Playlist smartPlaylist = smartPlaylistGenerator.generateSmartPlaylist(type, maxSongs, parameter);
                
                // Add to playlist manager
                String playlistName = smartPlaylist.getName();
                playlistManager.createPlaylist(playlistName);
                for (Song song : smartPlaylist.getSongs()) {
                    playlistManager.addSongToPlaylist(playlistName, song);
                }
                
                System.out.print("Load this playlist now? (y/n): ");
                if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
                    musicPlayer.loadPlaylist(smartPlaylist);
                    System.out.println("✅ Smart playlist loaded and ready to play!");
                }
                
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] Invalid playlist type. Use 'Available Types' to see options.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid number for max songs.");
        }
    }
    
    private void generatePersonalizedMix(Scanner scanner) {
        System.out.print("Enter max songs for personalized mix (default 30): ");
        String sizeStr = scanner.nextLine().trim();
        
        try {
            int maxSongs = sizeStr.isEmpty() ? 30 : Integer.parseInt(sizeStr);
            
            Playlist personalizedMix = smartPlaylistGenerator.generatePersonalizedMix(maxSongs);
            
            // Add to playlist manager
            String playlistName = personalizedMix.getName();
            playlistManager.createPlaylist(playlistName);
            for (Song song : personalizedMix.getSongs()) {
                playlistManager.addSongToPlaylist(playlistName, song);
            }
            
            System.out.print("Load personalized mix now? (y/n): ");
            if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
                musicPlayer.loadPlaylist(personalizedMix);
                System.out.println("✅ Personalized mix loaded and ready to play!");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid number for max songs.");
        }
    }
    
    private void generateQuickPlaylist(SmartPlaylistGenerator.PlaylistType type, String name) {
        Playlist quickPlaylist = smartPlaylistGenerator.generateSmartPlaylist(type, 20);
        
        // Add to playlist manager
        playlistManager.createPlaylist(name);
        for (Song song : quickPlaylist.getSongs()) {
            playlistManager.addSongToPlaylist(name, song);
        }
        
        musicPlayer.loadPlaylist(quickPlaylist);
        System.out.println("✅ " + name + " generated and loaded!");
    }
    
    private void displayHelpMenu() {
        System.out.println("\n🎵 === Enhanced Music Player Help ===");
        System.out.println();
        System.out.println("🎵 BASIC PLAYBACK:");
        System.out.println("play, pause, stop, next, prev, shuffle, repeat");
        System.out.println();
        System.out.println("[EQ] AUDIO EFFECTS:");
        System.out.println("effects - Access equalizer, bass boost, reverb, volume");
        System.out.println("vol+/vol- - Quick volume adjustment");
        System.out.println();
        System.out.println("🎨 VISUALIZER:");
        System.out.println("viz - Toggle and configure music visualizer");
        System.out.println("Modes: BARS, WAVE, SPECTRUM, PULSE, MATRIX");
        System.out.println();
        System.out.println("[TIMER] SLEEP TIMER:");
        System.out.println("timer - Set automatic stop/pause/fade-out");
        System.out.println();
        System.out.println("📊 STATISTICS:");
        System.out.println("stats - View listening habits and insights");
        System.out.println();
        System.out.println("🎯 SMART PLAYLISTS:");
        System.out.println("smart - AI-generated playlists based on your taste");
        System.out.println("Types: workout, chill, discovery, personalized, etc.");
        System.out.println();
        System.out.println("📚 LIBRARY & PLAYLISTS:");
        System.out.println("library - Manage your music collection");
        System.out.println("playlist - Create and manage playlists");
        System.out.println();
        System.out.println("Type any menu number or command name to access features!");
    }
    
    public static void main(String[] args) {
        new MusicPlayerApp().start();
    }
}
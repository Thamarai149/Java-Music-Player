# Enhanced Java Music Player 🎵

A feature-rich console-based music player with advanced audio effects, visualizations, and AI-powered recommendations.

## 🎛️ New Features Added

### 1. Audio Effects System
- **10-Band Equalizer** with visual frequency bars
- **EQ Presets**: Rock, Pop, Classical, Jazz, Electronic
- **Bass Boost** with adjustable levels (0-10)
- **Reverb Effects** for spatial audio enhancement
- **Virtual Surround Sound** simulation
- **Master Volume Control** with quick vol+/vol- commands

### 2. Music Visualizer 🎨
- **5 Visualization Modes**:
  - BARS: Real-time frequency bars
  - WAVE: Animated waveform display
  - SPECTRUM: Spectrum analyzer view
  - PULSE: Pulsing circle effects
  - MATRIX: Matrix-style digital rain
- **Real-time Animation** at 10 FPS
- **Toggle On/Off** during playback

### 3. Sleep Timer ⏰
- **Flexible Timer Settings** (1-999 minutes)
- **Multiple Actions**:
  - STOP: Immediately stop playback
  - PAUSE: Pause at timer end
  - FADE_OUT: Gradually fade volume over 1-60 seconds
- **Timer Extension** without losing current settings
- **Visual Countdown** with remaining time display

### 4. Music Statistics & Analytics 📊
- **Play Count Tracking** for every song
- **Listening Time Statistics** (total hours/minutes)
- **Top Songs & Artists** rankings
- **Daily Listening History** with visual bars
- **Session Tracking** with duration and song counts
- **Music Insights**: Peak listening hours, diversity scores
- **Recently Played** smart tracking

### 5. Smart Playlist Generator 🎯
- **12 Smart Playlist Types**:
  - Recently Added, Most Played, Never Played
  - Artist Focus, Mood-Based (energetic/chill/sad/happy)
  - Decade Mix (80s/90s/2000s/2010s)
  - Workout Mix, Chill Vibes, Discovery Mix
  - **Personalized Mix**: AI-curated based on your listening habits
- **Dynamic Generation** based on listening patterns
- **Customizable Size** (10-100 songs)

### 6. Enhanced User Interface
- **Colorful Status Display** with emojis and visual indicators
- **Quick Commands**: vol+, vol-, help
- **Context-Aware Menus** with current status
- **Real-time Updates** for timer, effects, and visualizer status
- **Comprehensive Help System**

## 🎵 Audio Format Support

### Full Playback Support
- **MP3** - Full playback with JLayer library
- **WAV** - Native Java audio support
- **AIFF** - Native Java audio support  
- **AU** - Native Java audio support

### Simulation Mode
- **FLAC, M4A, AAC, OGG** - Metadata parsing with playback simulation

## 🚀 Quick Start Guide

### 1. Load Your Music
```
Main Menu → 9. Library → 7. Load Music Folder
Enter your music folder path (e.g., C:\Music)
```

### 2. Try the New Features
```
12. Audio Effects → 2. EQ Presets → Select "rock" or "electronic"
13. Visualizer → 1. Toggle On/Off → 2. Change Mode
14. Sleep Timer → 1. Set Timer → Enter 30 minutes
15. Statistics → 1. Overall Stats (after playing some songs)
16. Smart Playlists → 4. Quick Workout Mix
```

### 3. Quick Commands
```
vol+ / vol-  - Adjust volume quickly
help         - Show all available commands
smart        - Quick access to smart playlists
effects      - Quick access to audio effects
```

## 🎛️ Audio Effects Usage

### Equalizer Presets
- **Rock**: Enhanced bass and treble for rock music
- **Pop**: Balanced with vocal emphasis
- **Classical**: Natural frequency response
- **Jazz**: Warm mids with clear highs
- **Electronic**: Deep bass with crisp highs

### Manual EQ Control
Set individual frequency bands from -12dB to +12dB:
- 60Hz, 170Hz, 310Hz, 600Hz, 1kHz, 3kHz, 6kHz, 12kHz, 14kHz, 16kHz

## 🎨 Visualizer Modes

- **BARS**: Classic frequency analyzer bars
- **WAVE**: Animated sine wave patterns
- **SPECTRUM**: Detailed frequency spectrum
- **PULSE**: Rhythmic pulsing circles
- **MATRIX**: Digital rain effect

## 📊 Statistics Features

### Tracking
- Individual song play counts
- Total listening time per song
- Daily listening patterns
- Session duration and song counts

### Insights
- Most played songs and artists
- Peak listening hours
- Music diversity score
- Listening habit analysis

## 🎯 Smart Playlist Types

### Behavior-Based
- **Most Played**: Your favorite tracks
- **Recently Played**: What you've been listening to
- **Never Played**: Discover forgotten music
- **Discovery Mix**: Rarely played gems

### Content-Based
- **Artist Focus**: Deep dive into specific artists
- **Mood-Based**: Energetic, chill, sad, or happy vibes
- **Decade Mix**: Music from specific time periods
- **Workout/Chill**: Activity-specific playlists

### AI-Powered
- **Personalized Mix**: 40% favorites + 30% recent + 20% discovery + 10% random

## 🔧 Technical Requirements

- Java 8 or higher
- JLayer library (included in lib/ folder)
- Windows/Linux/macOS compatible
- Console/Terminal environment

## 📁 Project Structure

```
MUSIC/
├── src/main/java/com/musicplayer/
│   ├── MusicPlayerApp.java           # Main application with new features
│   ├── model/
│   │   ├── Song.java                 # Song data model
│   │   └── Playlist.java             # Playlist data model
│   └── service/
│       ├── MusicPlayer.java          # Core playback engine
│       ├── MusicLibrary.java         # Music collection management
│       ├── PlaylistManager.java      # Playlist operations
│       ├── AudioEffects.java         # 🆕 EQ, effects, volume control
│       ├── MusicVisualizer.java      # 🆕 ASCII visualizations
│       ├── SleepTimer.java           # 🆕 Auto-stop functionality
│       ├── MusicStatistics.java     # 🆕 Analytics and tracking
│       └── SmartPlaylistGenerator.java # 🆕 AI playlist creation
├── lib/
│   └── jlayer-1.0.1.4.jar          # MP3 playback support
└── README.md                         # This file
```

## 🎵 Usage Examples

### Create a Workout Playlist
```
16. Smart Playlists → 4. Quick Workout Mix
```

### Set Up Audio for Rock Music
```
12. Audio Effects → 2. EQ Presets → Select "rock"
12. Audio Effects → 4. Toggle Bass Boost
```

### Enable Sleep Timer with Fade Out
```
14. Sleep Timer → 1. Set Timer → 45 minutes
14. Sleep Timer → 4. Set Action → 3. FADE_OUT
```

### View Your Music Statistics
```
15. Statistics → 1. Overall Stats
15. Statistics → 6. Insights
```

## 🎵 Enhanced Menu System

### Main Menu (Updated)
```
🎵 PLAYBACK:
1. Play          2. Pause         3. Stop
4. Next          5. Previous      6. Toggle Shuffle
7. Toggle Repeat 8. Playlists     9. Library
10. Recently Played  11. Status

🎛️ NEW FEATURES:
12. Audio Effects    13. Visualizer   14. Sleep Timer
15. Statistics       16. Smart Playlists

⚡ QUICK: vol+/vol- | help | 0. Quit
```

### Audio Effects Menu
```
🎛️ === Audio Effects Menu ===
1. Toggle Equalizer      2. EQ Presets        3. Manual EQ
4. Toggle Bass Boost     5. Toggle Reverb     6. Virtual Surround
7. Master Volume         8. View Settings     9. Reset All
```

### Smart Playlists Menu
```
🎯 === Smart Playlists Menu ===
1. Generate Smart Playlist  2. Personalized Mix     3. Available Types
4. Quick Workout Mix        5. Quick Chill Mix
```

## 🔮 Future Enhancements

- **Crossfade Transitions**: Smooth song transitions
- **Lyrics Display**: Show synchronized lyrics
- **Last.fm Integration**: Scrobbling support
- **Hotkey Support**: Global keyboard shortcuts
- **Themes**: Customizable color schemes
- **Plugin System**: Extensible architecture

## 🎵 Enjoy Your Enhanced Music Experience!

The Enhanced Java Music Player transforms your console into a powerful music workstation with professional-grade features. Explore the audio effects, discover new music with smart playlists, and track your listening habits with detailed statistics.

**Happy Listening! 🎧**
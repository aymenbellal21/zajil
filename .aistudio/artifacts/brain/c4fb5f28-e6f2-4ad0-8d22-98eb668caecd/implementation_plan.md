# Alzajel Social Expansion & Comprehensive Screen Suite

A comprehensive architectural and UX roadmap to enrich the Alzajel social platform with four major missing screens (Notifications & Activity Center, Profile & Bio Editor, Saved Collections, and Live Audio Rooms), interactive story stickers, and an enhanced voice player with animated waveforms and speed toggles, while preserving full multilingual and privacy-focused design.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following directions have been confirmed based on your preferences:
> - **Prioritized Screens**: Dedicated Notifications & Activity Center, User Profile & Bio Customization, Saved Posts & Organized Collections, and Direct Audio Room & Live Broadcast.
> - **Story Interactivity**: Rich story stickers including interactive live polls (tap to vote), ask-a-question prompts, and quick emoji reaction stickers.
> - **Voice Chat Upgrades**: Full voice message audio player featuring visual audio waveforms, progress scrubbing, and speed toggle playback (1.0x / 1.5x / 2.0x).
> - **System Integrity**: All new modules integrate into the existing state architecture and navigation backstack without altering database contracts or breaking camera, reel, or authentication workflows.

---

## 1. Overview & Core Concept

**Alzajel (الزاجل)** is a next-generation social media ecosystem tailored for vibrant media sharing, creative reels, encrypted communications, and live community broadcasting, styled in a signature violet/cyber-purple aesthetic inspired by the courier pigeon emblem. 

This phase fills in the missing functional hubs of the social experience:
- **Activity & Notifications Hub**: Keeping creators informed of likes, mentions, comments, and community broadcasts in real-time.
- **Identity & Bio Studio**: Empowering creators to customize their avatar, bio, links, and privacy settings.
- **Bookmarks & Curated Collections**: Giving users a dedicated space to save, categorize, and revisit their favorite media and reels.
- **Live Audio Rooms**: An interactive, low-latency-style audio stage where hosts and listeners interact with voice, emoji showers, and raised hands.
- **Expressive Stories**: Elevating daily stories with interactive voting polls, question boxes, and dynamic reaction stickers.
- **High-Fidelity Audio Messages**: Transforming direct chat voice notes into engaging audio capsules with dynamic visual waveforms and speed adjustments.

---

## 2. User Experience & Visual Design

### Key User Flows

```
[Main Feed / Home] ──────► [Top Bar Notification Bell] ──► [Notifications & Activity Screen]
        │                                                              │
        ├────────► [Story Avatar Bar] ────► [Story Viewer with Polls/Questions/Stickers]
        │                                                              │
        ├────────► [Direct Chat] ─────────► [Waveform Voice Player (1x/1.5x/2x)]
        │                                                              │
        ├────────► [Bottom Bar: Profile] ─► [Profile Screen]
        │                                       ├─► [Edit Profile & Bio Customizer]
        │                                       └─► [Saved Posts & Collections Grid]
        │
        └────────► [Top Bar / Explore] ───► [Live Audio Room (Host / Listener Stage)]
```

1. **Activity Center Flow**:
   - Tapping the notification bell in the top navigation opens the Activity Center.
   - Filter chips allow instant sorting: `All`, `Likes`, `Comments`, `Mentions`, and `System Announcements`.
   - Swiping or tapping an item navigates directly to the relevant content, with a "Mark all as read" button.

2. **Profile & Bio Customizer Flow**:
   - From the Profile screen, tapping "Edit Profile" transitions smoothly into the editor.
   - Users can update their avatar, banner accent, display name, handle, multi-line bio with character counter, website link, and toggle privacy options (Hide online status, Private account).

3. **Collections & Bookmarked Posts Flow**:
   - From Profile or the Post bookmark icon, users access Saved Collections.
   - Users can view "All Saved" or create custom themed folders (e.g., "Photography", "Gaming Highlights", "Design Ideas").
   - A modal allows quick creation of new collections with custom cover icons.

4. **Live Audio Room Flow**:
   - Users enter a live room featuring a homing-pigeon broadcast banner.
   - The stage displays active speaker circles with pulsing audio glow rings.
   - Listeners can tap "Raise Hand" to request the mic, tap reaction emojis (🕊️, 🔥, 💜, 👏) that float up the screen, or participate in the live room chat bar.

5. **Interactive Story Stickers**:
   - In story view, stickers are active: tapping a poll option updates vote counts with an instant visual percentage bar.
   - Question stickers open a responsive text response sheet.
   - Reaction sliders/stickers deliver haptic-style visual pulse feedback.

6. **Waveform Voice Player**:
   - Audio notes in Direct Chat render a stylized multi-bar equalizer waveform matching the violet theme.
   - Users can scrub the progress bar and tap the speed chip (`1x` ➔ `1.5x` ➔ `2x`) for fast listening.

### Visual Identity & Theme
- **Dominant Palette**: `#802FFF` (Neon Purple), `#703CFF` (Deep Violet), `#0D0D0D` (Obsidian Dark Background), `#1A1A1A` (Card Surface), and `#00F5D4` (Cyber Cyan accent).
- **Surface Elevation**: Layered semi-transparent cards (`Color(0x22FFFFFF)`) with subtle hairline violet borders (`BorderStroke(1.dp, Color(0x33802FFF))`).
- **Interactive Feedback**: Spring animations on sticker voting, floating emoji physics on live audio stages, and animated wave amplitudes for voice playback.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Navigation Architecture & Sub-Screen Routing
- **Chosen Approach**: Extend the existing `Screen` enum and `SocialViewModel` state router (`Screen.NOTIFICATIONS`, `Screen.EDIT_PROFILE`, `Screen.SAVED_POSTS`, `Screen.AUDIO_ROOM`) backed by Compose `BackHandler`.
- **Why**: Keeps the application lightweight, maintains instant state preservation, and avoids breaking existing transitions and authentication flows.
- **Alternatives Considered**: Full Navigation Compose migration was considered, but modifying the working activity orchestration poses high regression risk for active dialogs (comments, stories, camera).

### Decision 2: In-Memory Interactive State for Polls & Live Rooms
- **Chosen Approach**: Store poll votes, sticker responses, and live room participants directly in `SocialRepository` state flows.
- **Why**: Guarantees zero backend configuration dependency while providing 100% interactive fidelity (tappable votes, dynamic percentage updates, live participant toggle) immediately in the preview.

### Decision 3: Audio Waveform Simulation & Playback Engine
- **Chosen Approach**: Implement an animated Compose waveform canvas with real-time playback tick timer, scrubbing drag gesture, and playback rate state machine (`1.0f`, `1.5f`, `2.0f`).
- **Why**: Provides an engaging visual audio experience across all device emulators without requiring external audio hardware codecs.

---

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                               MainActivity                                  │
│  ┌───────────────────────┐  ┌────────────────────────────────────────────┐  │
│  │     PulseTopBar       │  │               PulseBottomBar               │  │
│  │ (Ghost/Dark/Lang/Bell)│  │        (Home/Explore/Create/Reels/Profile) │  │
│  └───────────┬───────────┘  └─────────────────────┬──────────────────────┘  │
│              │                                    │                         │
│              ▼                                    ▼                         │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                             Screen Router                             │  │
│  │  ├─ HomeScreen (Feed, Scheduled, Stories)                             │  │
│  │  ├─ NotificationsScreen (Activity Hub, Filter Tabs)                   │  │
│  │  ├─ EditProfileScreen (Avatar, Bio, Privacy)                          │  │
│  │  ├─ SavedPostsScreen (Folder Collections, Bookmarked Media)           │  │
│  │  ├─ LiveAudioRoomScreen (Speaker Stage, Hand Raising, Floating Emojis)│  │
│  │  ├─ ExploreSearchScreen / CreatePostScreen / ReelsScreen              │  │
│  │  └─ MessagesScreen (Encrypted DM + AudioWaveformPlayer)               │  │
│  └───────────────────────────────────┬───────────────────────────────────┘  │
│                                      │                                      │
│                                      ▼                                      │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                    Interactive Overlay Dialogs                        │  │
│  │  ├─ StoryViewerDialog (Poll Stickers, Question Box, Reactions)        │  │
│  │  ├─ CommentsBottomSheet                                               │  │
│  │  ├─ CreateCollectionDialog                                            │  │
│  │  └─ SupportDialog                                                     │  │
│  └───────────────────────────────────┬───────────────────────────────────┘  │
└──────────────────────────────────────┼──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              SocialViewModel                                │
│   - notifications: StateFlow<List<NotificationItem>>                        │
│   - savedCollections: StateFlow<List<SavedCollection>>                      │
│   - liveAudioRooms: StateFlow<List<LiveAudioRoom>>                          │
│   - activeAudioRoom: StateFlow<LiveAudioRoom?>                              │
│   - audioPlaybackState: StateFlow<AudioPlaybackState>                       │
│   - votePoll(storyId, optionIndex)                                          │
│   - updateProfile(name, bio, handle, link)                                  │
│   - toggleSavePost(postId, collectionId)                                    │
│   - toggleRoomMic(), raiseHand(), sendLiveReaction()                        │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              SocialRepository                               │
│   - Mock & Seeded Entities: Notifications, Collections, Audio Stages        │
│   - Multilingual Text Provider (Arabic, English, French, Spanish, etc.)     │
│   - Real-time Flow emitters for instant UI updates                          │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Key Data Entities & Models

1. **`NotificationItem`**:
   - `id`, `type` (`LIKE`, `COMMENT`, `MENTION`, `SYSTEM`, `LIVE_ROOM`), `actor` (`User`), `targetPostId`, `previewText`, `timestamp`, `isRead`.

2. **`SavedCollection`**:
   - `id`, `title`, `iconName`, `postIds: List<String>`, `coverMediaUrl`, `isPrivate`.

3. **`LiveAudioRoom`**:
   - `id`, `title`, `topic`, `host: User`, `speakers: List<User>`, `listeners: List<User>`, `isSpeaking: Set<String>`, `raisedHands: Set<String>`, `isLive`.

4. **`StorySticker`**:
   - Sealed class hierarchy:
     - `PollSticker(question, options, votesMap)`
     - `QuestionSticker(prompt, responseCount)`
     - `ReactionSliderSticker(emoji, averageScore)`

5. **`AudioPlaybackState`**:
   - `messageId`, `isPlaying`, `progress: Float`, `currentPositionMs`, `totalDurationMs`, `speedMultiplier: Float` (`1.0f`, `1.5f`, `2.0f`), `waveformAmplitudes: List<Float>`.

---

## 5. Execution Steps & Verification Plan

1. **Model & State Layer Expansion**:
   - Add new data classes in `Models.kt` (`NotificationItem`, `SavedCollection`, `LiveAudioRoom`, `StorySticker`, `AudioPlaybackState`).
   - Add new `Screen` enum values: `NOTIFICATIONS`, `EDIT_PROFILE`, `SAVED_POSTS`, `AUDIO_ROOM`.
   - Implement corresponding state flows and mutation methods in `SocialRepository` and `SocialViewModel`.

2. **Components & Interactive Elements**:
   - Build `AudioWaveformPlayer` with interactive scrubber and speed toggle.
   - Enhance `StoryViewerDialog` with interactive poll voting and question response sheet.
   - Update `PulseTopBar` with an active notification badge indicator and navigation action.

3. **New Screen Implementations**:
   - `NotificationsScreen.kt`: Filterable notification list with read/unread statuses.
   - `EditProfileScreen.kt`: Comprehensive profile editing, bio field, and privacy switches.
   - `SavedPostsScreen.kt`: Collections gallery with custom folders and media view.
   - `LiveAudioRoomScreen.kt`: Interactive audio room with speaker pulses, hand raising, and floating emojis.

4. **MainActivity Integration**:
   - Connect all new screens into the `MainActivity` screen switcher and `BackHandler` stack.
   - Verify localization coverage across Arabic and English.

5. **Compilation & Stability Verification**:
   - Run `compile_applet` to ensure zero compilation or syntax errors.

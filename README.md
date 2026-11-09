# SnapPulse 👻 — Modern Snapchat Clone with Snap Map & Multimedia Chat

**SnapPulse** is a production-grade, deployable Android application inspired by Snapchat. It features real-time **Snap Map** live location tracking using the **Google Maps SDK for Android**, a rich **Multimedia Chat** supporting plain text, captured/picked photos, recorded videos, and live GPS pins, with an interactive **Permissions Onboarding** flow adhering to Android 13/14/15 standards.

---

## 🌟 Key Features

### 1. 🗺️ Snap Map (Google Maps Integration)
- **Signature Dark Theme**: Custom Midnight JSON map styling mirroring Snapchat's Snap Map aesthetic.
- **Bitmoji / Avatar Markers**: Live markers with custom status emojis (☕, 🚗, 🎧, 💻, 🏋️, 😎), friend name pills, and real-time battery indicators (`⚡85%`).
- **Ghost Mode**: One-tap privacy toggle to freeze or hide your location on the map, with duration options (`3 Hours`, `24 Hours`, `Until turned off`).
- **Horizontal Friend Carousel**: Swipeable bottom cards showing distance to friend (`350 m away`), speed (`Traveling 48 km/h`), and one-tap camera zoom & chat launcher.
- **Recenter & Live Updates**: Continuous location tracking via Google Play Services Fused Location Provider.
- **Graceful Fallback Mode**: Works out of the box even before configuring an active Google Cloud key, with interactive simulation and guidance banners.

### 2. 💬 Multimedia Chat
- **Plain Text Messaging**: Real-time message exchange with responsive speech bubbles.
- **Photo Snaps (Images)**:
  - Capture photos directly using device camera via `FileProvider`.
  - Pick images from device media gallery.
  - Fullscreen snap viewer with tap/dismiss gestures.
- **Video Snaps (Videos)**:
  - Record videos with camera or select video files from gallery.
  - Video message bubbles with duration indicator and play overlay.
  - Integrated high-performance video player powered by **AndroidX Media3 (ExoPlayer)**.
- **Live Location Sharing**: Share current GPS location directly inside chat; friends can tap the pin to jump straight to Snap Map!
- **Snapchat Visuals**: Solid red/purple/blue status badges, streak counts (`🔥 42`), and interactive simulated friend replies to test live receiving.

### 3. 📷 Snap Camera
- Integrated **CameraX** viewfinder for taking instant snaps and video clips.
- Front/Back camera switcher and high-resolution JPEG capture pipeline.

### 4. 🔒 Permission Handling (Android 13/14/15 Ready)
- **Onboarding Flow**: Beautiful first-launch permissions screen explaining the exact necessity of:
  - **Precise & Coarse Location**: For Snap Map positioning.
  - **Camera & Microphone**: For capturing snaps and recording videos.
  - **Photos & Media (`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`)**: For chat attachments.
  - **Push Notifications (`POST_NOTIFICATIONS`)**: For chat alerts and streak reminders.
- **One-Tap Enable**: Grants all required permissions smoothly or routes to App Settings if permanently denied.
- **Foreground Service**: Background tracking service with persistent notification declaring `foregroundServiceType="location"`.

---

## 🚀 Getting Started & Setup

### Prerequisites
- **Android Studio Jellyfish / Koala or newer** (or command-line Gradle)
- **JDK 17**
- **Android SDK Platform 35** (compileSdk 35, minSdk 26)

### Google Maps API Key Setup
To view live Google Maps tiles on your device or emulator:
1. Visit the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a project and enable **Maps SDK for Android**.
3. Generate an API Key under **Credentials**.
4. Configure the key in `SnapPulse/local.properties` (or via gradle command line):
   ```properties
   sdk.dir=C\:\\Users\\Prem\\AppData\\Local\\Android\\Sdk
   MAPS_API_KEY=AIzaSyYourActualGoogleMapsApiKeyHere
   ```
   *Note: If no key is supplied, the app launches in demo mode with UI fallback banners without crashing!*

---

## 🛠️ Building & Deploying the App

### Via Gradle Command Line
Navigate to the `SnapPulse` directory:
```bash
cd SnapPulse
```

#### Build Debug APK:
```bash
./gradlew assembleDebug
```
The output APK will be generated at:
`SnapPulse/app/build/outputs/apk/debug/app-debug.apk`

#### Install onto Connected Device / Emulator:
```bash
./gradlew installDebug
```

### Via Android Studio
1. Open Android Studio.
2. Select **File -> Open...** and choose the `SnapPulse` folder.
3. Allow Gradle to sync.
4. Select your target device or emulator and press **Run (Shift + F10)**.

---

## 📂 Project Architecture

```
com.snappulse.app/
├── SnapPulseApp.kt                     # Application class & Notification Channels
├── MainActivity.kt                     # Single-activity container with Jetpack Navigation
├── navigation/
│   └── Screen.kt                       # Sealed class of app routes
├── data/
│   ├── model/
│   │   ├── FriendLocation.kt           # Friend location, distance calculation, BitmojiMood
│   │   ├── UserProfile.kt              # User profile, Ghost Mode state
│   │   ├── Message.kt                  # Message model (TEXT, IMAGE, VIDEO, LOCATION_PIN)
│   │   └── ChatConversation.kt         # Conversation summary & streak tracker
│   └── repository/
│       ├── LocationRepository.kt       # FusedLocationProviderClient & SnapMap friends state
│       ├── ChatRepository.kt           # Reactive message store & simulated friend replies
│       └── PermissionsManager.kt       # Permission checks & intent dispatchers
├── service/
│   └── LocationTrackingService.kt      # Foreground service for continuous location tracking
└── ui/
    ├── theme/                          # Snapchat signature yellow/black styling & typography
    ├── components/
    │   ├── SnapBottomBar.kt            # Snapchat styled bottom navigation
    │   ├── MediaViewerDialog.kt        # Fullscreen photo snap viewer
    │   └── VideoPlayerView.kt          # ExoPlayer video playback modal
    ├── permissions/
    │   └── PermissionOnboardingScreen.kt # First-launch permissions walkthrough
    ├── map/
    │   ├── SnapMapScreen.kt            # Google Map with dark styling, markers & friend carousel
    │   ├── SnapMapViewModel.kt         # SnapMap state & Ghost Mode coordinator
    │   ├── SnapMapStyle.kt             # Dark midnight Google Maps JSON style
    │   └── GhostModeSheet.kt           # Ghost Mode duration & toggle bottom sheet
    ├── chat/
    │   ├── ChatListScreen.kt           # Conversation list with snap badges & streak counters
    │   ├── ChatDetailScreen.kt         # Active chat thread (text, images, videos, pins)
    │   ├── ChatViewModel.kt            # Chat reactive stream & message sender
    │   └── MediaPickerSheet.kt         # Bottom sheet to capture or pick photo/video
    ├── camera/
    │   └── SnapCameraScreen.kt         # CameraX live viewfinder & instant snap capture
    └── profile/
        └── ProfileScreen.kt            # Bitmoji card, Ghost Mode switcher & app diagnostics
```

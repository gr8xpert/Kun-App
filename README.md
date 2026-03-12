# KunWorld - Personal Development & Islamic Learning App

<p align="center">
  <img src="app/src/main/res/drawable/logo.png" alt="KunWorld Logo" width="120"/>
</p>

<p align="center">
  <b>Your companion for personal growth, Islamic guidance, and continuous learning</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform"/>
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue.svg" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/Target%20SDK-34-blue.svg" alt="Target SDK"/>
  <img src="https://img.shields.io/badge/Language-Java-orange.svg" alt="Language"/>
</p>

---

## Overview

KunWorld is a comprehensive Android application designed to help users on their journey of personal development and Islamic learning. The app combines modern technology with traditional wisdom, offering courses, books, consultancy services, and AI-powered assistance.

---

## Features

### Core Features

| Feature | Description |
|---------|-------------|
| **Courses** | Structured learning programs covering personality development, communication skills, entrepreneurship, health awareness, and more |
| **Books Library** | Digital collection of books on various topics including personal growth, Islamic guidance, and life skills |
| **AI Chat Assistant** | Powered by Groq API for intelligent conversations and guidance |
| **Istikhara Calculator** | Traditional Abjad-based calculations for marriage compatibility, child naming, personality matching, and more |
| **Consultancy Services** | Connect with experts in personality development, family counseling, business, health, and education |

### User Experience

| Feature | Description |
|---------|-------------|
| **User Authentication** | Secure signup/login with profile management |
| **Bookmarks** | Save favorite content for quick access |
| **Search** | Find courses, books, and content with search history |
| **Progress Tracking** | Track reading progress and course completion |
| **Pull-to-Refresh** | Smooth content refreshing on all screens |

### Recent Enhancements

| Feature | Description |
|---------|-------------|
| **Notes & Highlights** | Add personal notes while reading books with color-coded highlights |
| **Course Completion Tracking** | Mark modules as complete with visual progress indicators |
| **Quote of the Day Widget** | Home screen widget displaying daily motivational quotes |
| **Continue Reading** | Quick access to resume where you left off |
| **Search History** | Recently searched terms saved as chips for quick access |
| **Shimmer Loading** | Smooth loading animations for better UX |

---

## Screenshots

<p align="center">
  <i>Screenshots coming soon</i>
</p>

<!--
| Home | Courses | Books | AI Chat |
|------|---------|-------|---------|
| <img src="screenshots/home.png" width="200"/> | <img src="screenshots/courses.png" width="200"/> | <img src="screenshots/books.png" width="200"/> | <img src="screenshots/chat.png" width="200"/> |
-->

---

## Tech Stack

### Architecture & Design
- **Architecture Pattern:** MVVM (Model-View-ViewModel)
- **Navigation:** Android Navigation Component with SafeArgs
- **UI Framework:** Material Design 3

### Libraries & Dependencies

| Category | Library |
|----------|---------|
| **UI Components** | Material Components, SwipeRefreshLayout, ViewPager2 |
| **Database** | Room Persistence Library |
| **Networking** | Retrofit 2, OkHttp, Gson |
| **Image Loading** | Glide |
| **PDF Viewer** | AndroidPdfViewer |
| **Animations** | Facebook Shimmer |
| **Background Work** | WorkManager |
| **AI Integration** | Groq API (Llama 3) |

---

## Project Structure

```
app/src/main/java/com/example/kunworld/
├── api/                    # API services and helpers
│   ├── GroqApiService.java
│   ├── GroqChatHelper.java
│   └── ...
├── data/
│   ├── database/           # Room database, DAOs, converters
│   ├── models/             # Data models and entities
│   └── repository/         # Data repositories
├── notifications/          # Push notifications and workers
├── ui/
│   ├── activity/           # Activity tracking screens
│   ├── auth/               # Login and signup
│   ├── books/              # Books listing
│   ├── chat/               # AI chat interface
│   ├── consultancy/        # Consultancy services
│   ├── courses/            # Courses listing and details
│   ├── home/               # Home screen
│   ├── istikhara/          # Istikhara calculator tabs
│   ├── profile/            # User profile
│   ├── reader/             # Book and content reader
│   ├── search/             # Search functionality
│   └── settings/           # App settings
├── utils/                  # Utility classes
│   ├── SessionManager.java
│   ├── LastReadManager.java
│   ├── NotesManager.java
│   └── ...
└── widget/                 # Home screen widgets
    └── QuoteWidgetProvider.java
```

---

## Setup & Installation

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Min SDK 24 (Android 7.0)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/gr8xpert/Kun-App.git
   cd Kun-App
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned folder
   - Wait for Gradle sync to complete

3. **Configure API Key** (Optional - for AI Chat)
   - Get an API key from [Groq](https://console.groq.com/)
   - Add to `app/build.gradle`:
     ```gradle
     buildConfigField "String", "GROQ_API_KEY", "\"your-api-key\""
     ```

4. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" or press `Shift + F10`

---

## Database Schema

The app uses Room database with the following entities:

| Entity | Description |
|--------|-------------|
| `User` | User profile and authentication data |
| `Book` | Book metadata and content |
| `Bookmark` | User bookmarks for books and courses |
| `Note` | User notes and highlights |
| `CourseProgress` | Course completion tracking |

---

## Key Features Implementation

### Quote of the Day Widget
A home screen widget that displays daily motivational quotes with a refresh button.

```xml
<!-- Add widget to home screen -->
Long press home screen → Widgets → Quote of the Day
```

### Notes & Highlights
While reading books, users can:
- Select text and add notes
- Choose from 4 highlight colors (Yellow, Green, Blue, Pink)
- View all notes in a dedicated section

### Istikhara Calculator
Traditional Abjad numerology calculations for:
- Marriage compatibility matching
- Child name suggestions
- Personality analysis
- Lost item guidance

---

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## Future Roadmap

- [ ] Dark mode improvements
- [ ] Offline content download
- [ ] Multi-language support (Urdu translation)
- [ ] Video courses integration
- [ ] Push notifications for daily reminders
- [ ] Social sharing features
- [ ] Reading streaks and achievements

---

## License

This project is proprietary software. All rights reserved.

---

## Contact

For questions or support, please open an issue on GitHub.

---

<p align="center">
  Made with dedication for personal growth and Islamic learning
</p>

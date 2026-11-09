# NoteVault – Android Notes App with SQLite

Mobile application project.

**Authors:**
- Taleed Hamadneh
- Qasim Batrawi

An Android notes app where each user has their own account and private notes,
all stored on the device in a SQLite database.

## Features
- **Accounts** – sign up, log in, "remember me" and logout
- **Profile** – edit your first name, last name and password
- **Notes** – create, view, edit and delete notes, each with a creation date
- **Favourites** – mark notes with a heart and show only your favourites
- **Tags** – give each note a tag and filter notes by tag
- **Search** – search in the title, the content or both
- **Sorting** – newest or oldest first, or title A–Z / Z–A (your choice is saved for each user)
- **Navigation drawer** – All Notes, Favourites, Sort, Profile and Logout

## Tech stack
- Java, Android SDK (min SDK 26)
- SQLite (`SQLiteOpenHelper`) with three tables: `USER`, `NOTE` and `TAG`, linked by foreign keys
- SharedPreferences (singleton manager) for "remember me" and the sort choice
- Material Design components, fragments and XML layouts
- Gradle (Kotlin DSL)

## Project structure
| File | Role |
|---|---|
| `MainActivity` | Login screen |
| `SignUpActivity` | Account creation |
| `WelcomeActivity` | Main notes screen: drawer, search, filters, sorting |
| `AddNoteActivity` / `EditNoteActivity` | Create and edit notes |
| `NoteContentFragment` | Shows a note's content |
| `ProfileActivity` | Edit user details |
| `DataBaseHelper` | All SQLite tables and queries |
| `SharedPrefManager` | Saved preferences |

## Run
Open the project in Android Studio, let Gradle sync, then run it on an emulator or a phone with API 26 or newer.

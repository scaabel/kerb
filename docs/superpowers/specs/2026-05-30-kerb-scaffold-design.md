# Kerb — Scaffold Design (Revised)

**Date:** 2026-05-30
**Scope:** MVP skeleton (no business logic)
**Status:** Approved, implementation-ready

---

## Decisions

| Decision             | Choice                 | Rationale                                                                   |
| -------------------- | ---------------------- | --------------------------------------------------------------------------- |
| Module structure     | Single module (`:app`) | Avoid unnecessary Gradle overhead during MVP                                |
| Package name         | `com.arievscalia.kerb` | Personal/indie app naming                                                   |
| App model            | Companion phone app    | Monitors connected-device/car state from phone, not native Android Auto app |
| Theming              | Dynamic Material 3     | Native Android feel with API <31 fallback                                   |
| Scaffold depth       | Skeleton only          | Compileable structure with no business logic                                |
| Package organisation | Layer-first            | Clear separation of UI, data, service, widget, DI                           |

---

## Project layout

```text
kerb/
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── res/
        │   ├── values/
        │   │   ├── strings.xml
        │   │   └── themes.xml
        │   └── xml/
        │       └── kerb_widget_info.xml
        └── kotlin/com/arievscalia/kerb/
            ├── KerbApplication.kt
            ├── MainActivity.kt
            ├── ui/
            │   ├── feature/
            │   │   ├── home/
            │   │   │   ├── HomeScreen.kt
            │   │   │   └── HomeViewModel.kt
            │   │   ├── history/
            │   │   │   ├── HistoryScreen.kt
            │   │   │   └── HistoryViewModel.kt
            │   │   └── reminder/
            │   │       └── AddReminderSheet.kt
            │   ├── navigation/
            │   │   └── NavGraph.kt
            │   └── theme/
            │       ├── Theme.kt
            │       └── Type.kt
            ├── data/
            │   ├── db/
            │   │   ├── KerbDatabase.kt
            │   │   ├── TripDao.kt
            │   │   ├── ReminderDao.kt
            │   │   ├── TripEntity.kt
            │   │   └── ReminderEntity.kt
            │   ├── datastore/
            │   │   └── KerbPreferences.kt
            │   └── repository/
            │       ├── TripRepository.kt
            │       └── TripRepositoryImpl.kt
            ├── service/
            │   ├── CarConnectionService.kt
            │   └── BootReceiver.kt
            ├── widget/
            │   ├── KerbWidget.kt
            │   └── KerbWidgetReceiver.kt
            └── di/
                ├── DatabaseModule.kt
                └── RepositoryModule.kt
```

---

## Key architecture clarifications

### Companion app model

Kerb is **not** a native Android Auto surface.

It does **not** use:

* `CarAppService`
* automotive templates
* `automotive_app_desc.xml`

It **does** use:

* phone foreground service
* connected-device monitoring
* boot receiver
* widget surface
* local notification pipeline

---

## AndroidManifest updates

### Remove

```xml
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
```

```text
res/xml/automotive_app_desc.xml
```

---

### Keep

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

---

## Gradle improvements

### `libs.versions.toml`

Add:

```toml
compose-compiler = "1.5.11"
```

Update:

```toml
glance = "1.1.1"
```

---

### `app/build.gradle.kts`

Add Room schema generation:

```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

Use version catalog:

```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
}
```

Add:

```kotlin
kotlin {
    jvmToolchain(17)
}
```

---

## Required XML theme

### `res/values/themes.xml`

```xml
<resources>
    <style name="Theme.Kerb" parent="Theme.Material3.DayNight.NoActionBar" />
</resources>
```

---

## Room scaffold requirements

### Entity

```kotlin
@Entity
data class TripEntity(
    @PrimaryKey val id: String
)
```

---

### DAO

```kotlin
@Dao
interface TripDao
```

---

### Database

```kotlin
@Database(
    entities = [TripEntity::class, ReminderEntity::class],
    version = 1
)
abstract class KerbDatabase : RoomDatabase()
```

---

## Navigation routes

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object AddReminder : Screen("add_reminder")
}
```

---

## DI requirements

### Repository module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule
```

---

## Stub conventions

All stubs must compile.

### Screen

```kotlin
@Composable
fun HomeScreen(...) {
    Box(Modifier.fillMaxSize())
}
```

### ViewModel

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel()
```

### Repository impl

```kotlin
throw NotImplementedError("TODO: Implement repository logic")
```

### Service

```kotlin
class CarConnectionService : Service() {
    override fun onBind(intent: Intent?) = null
}
```

---

## Explicitly out of scope

* Bluetooth/car detection logic
* foreground notification implementation
* notification channels
* Room queries
* DataStore persistence
* widget rendering state
* battery optimization prompts
* reminder scheduling logic
* business logic
* UI implementation beyond placeholders

---

## Scaffold readiness checklist

* Compileable
* Hilt graph resolves
* Room codegen succeeds
* Navigation compiles
* Widget receiver registered
* Service registered
* Theme resolves
* No Android Auto assumptions
* Ready for feature implementation

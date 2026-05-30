# Kerb — Scaffold Implementation Plan

**Date:** 2026-05-30
**Spec:** `2026-05-30-kerb-scaffold-design.md`
**Goal:** A compileable, Hilt-wired skeleton with no business logic

---

## Phase 1 — Gradle & project setup

**Goal:** Project builds with all dependencies resolved before a single Kotlin file is written.

1. Create `settings.gradle.kts` with plugin management and single `:app` include
2. Create root `build.gradle.kts` (plugins block only, no apply)
3. Create `gradle/libs.versions.toml` with all versions, libraries, and plugins from spec
4. Create `app/build.gradle.kts` with:
   - `namespace`, `compileSdk`, `minSdk = 26`, `targetSdk = 34`
   - Compose build feature + `composeOptions` using version catalog
   - KSP arg for Room schema location
   - `kotlin { jvmToolchain(17) }`
   - All dependency declarations from spec
5. Create `gradle.properties` with `android.useAndroidX=true`, `kotlin.code.style=official`

**Done when:** `./gradlew assembleDebug` succeeds (no source files yet, just configuration).

---

## Phase 2 — Manifest & resources

**Goal:** Android tooling can resolve all declared components and themes.

1. Create `AndroidManifest.xml` with:
   - Four permissions from spec (POST_NOTIFICATIONS, FOREGROUND_SERVICE, FOREGROUND_SERVICE_CONNECTED_DEVICE, RECEIVE_BOOT_COMPLETED)
   - `<application>` pointing to `KerbApplication`, theme `@style/Theme.Kerb`
   - `MainActivity` with MAIN/LAUNCHER intent filter
   - `CarConnectionService` with `foregroundServiceType="connectedDevice"`
   - `KerbWidgetReceiver` with APPWIDGET_UPDATE filter + meta-data reference
   - `BootReceiver` with BOOT_COMPLETED filter
2. Create `res/values/strings.xml` — `app_name`, notification channel name strings
3. Create `res/values/themes.xml` — `Theme.Kerb` extending `Theme.Material3.DayNight.NoActionBar`
4. Create `res/xml/kerb_widget_info.xml` — AppWidget provider metadata (2×2 min size)

**Done when:** Manifest merges without errors; all resource references resolve.

---

## Phase 3 — Application entry point & DI scaffold

**Goal:** Hilt graph is wired; app launches without crashing.

1. Create `KerbApplication.kt` — `@HiltAndroidApp class KerbApplication : Application()`
2. Create `di/DatabaseModule.kt` — `@Module @InstallIn(SingletonComponent::class)`, provides `KerbDatabase` and both DAOs (stubs — no implementations yet)
3. Create `di/RepositoryModule.kt` — `@Module @InstallIn(SingletonComponent::class) abstract class RepositoryModule` with `@Binds` for `TripRepositoryImpl`

**Done when:** Hilt code generation succeeds; app process starts.

---

## Phase 4 — Data layer stubs

**Goal:** Room codegen succeeds; repository interface is defined.

1. Create `data/db/TripEntity.kt` — `@Entity data class TripEntity(@PrimaryKey val id: String)`
2. Create `data/db/ReminderEntity.kt` — same minimal pattern
3. Create `data/db/TripDao.kt` — `@Dao interface TripDao` (empty)
4. Create `data/db/ReminderDao.kt` — `@Dao interface ReminderDao` (empty)
5. Create `data/db/KerbDatabase.kt` — `@Database(entities=[TripEntity::class, ReminderEntity::class], version=1) abstract class KerbDatabase : RoomDatabase()` with abstract DAO accessors
6. Create `data/datastore/KerbPreferences.kt` — DataStore keys defined as `Preferences.Key` constants, no read/write logic
7. Create `data/repository/TripRepository.kt` — empty interface
8. Create `data/repository/TripRepositoryImpl.kt` — implements interface, all methods throw `NotImplementedError`

**Done when:** Room annotation processing completes; generated code compiles.

---

## Phase 5 — Service & widget stubs

**Goal:** Declared components exist as compileable classes.

1. Create `service/CarConnectionService.kt` — extends `Service()`, `onBind` returns null
2. Create `service/BootReceiver.kt` — extends `BroadcastReceiver()`, `onReceive` is empty
3. Create `widget/KerbWidget.kt` — extends `GlanceAppWidget()`, `provideGlance` returns empty `Box`
4. Create `widget/KerbWidgetReceiver.kt` — extends `GlanceAppWidgetReceiver()`, references `KerbWidget`

**Done when:** All four classes compile without errors.

---

## Phase 6 — UI scaffold

**Goal:** App launches to a blank Home screen with working navigation wiring.

1. Create `ui/theme/Type.kt` — default `KerbTypography` using M3 defaults
2. Create `ui/theme/Theme.kt` — dynamic color implementation from spec
3. Create `ui/navigation/NavGraph.kt` — `Screen` sealed class (Home, History, AddReminder) + `KerbNavGraph` NavHost
4. Create `ui/feature/home/HomeViewModel.kt` — `@HiltViewModel` stub
5. Create `ui/feature/home/HomeScreen.kt` — `@Composable` with empty `Box(Modifier.fillMaxSize())`
6. Create `ui/feature/history/HistoryViewModel.kt` — same pattern
7. Create `ui/feature/history/HistoryScreen.kt` — same pattern
8. Create `ui/feature/reminder/AddReminderSheet.kt` — empty composable
9. Create `MainActivity.kt` — `@AndroidEntryPoint`, sets content to `KerbTheme { KerbNavGraph(...) }`

**Done when:** App launches on a device/emulator and shows a blank screen without crashing.

---

## Phase 7 — Verification

1. Run `./gradlew assembleDebug` — must complete with 0 errors
2. Run `./gradlew kspDebugKotlin` — confirm Room and Hilt codegen both succeed
3. Install APK on emulator (API 26+) — app must launch without crash
4. Install APK on emulator (API 31+) — confirm dynamic color applies
5. Confirm widget receiver appears in widget picker
6. Check `app/schemas/` directory exists and contains Room schema JSON

**Done when:** All six checks pass.

---

## Order summary

```
Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 → Phase 6 → Phase 7
Gradle     Manifest   DI         Data       Services   UI         Verify
```

Each phase must compile before moving to the next.

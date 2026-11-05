# Transient Launcher

Package: `myth.transient.source`  
Minimum Android: 5.0 Lollipop (API 21)  
Compile / target SDK: 35  
Language: Kotlin  
UI: Jetpack Compose

## Toolchain

- JDK 17
- Gradle 8.9
- Android SDK Platform 35
- Android Build Tools 35.x
- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21

Ubuntu 18.04 is an old host OS. A current Android Studio release may not run on it. IntelliJ IDEA can open the Gradle project, but Android device tooling and SDK management are best supported by Android Studio. If Android Studio is incompatible with the host OS, use a supported newer Linux environment or a compatible IntelliJ + command-line Android SDK setup.

This archive intentionally does not include a Gradle wrapper JAR. Install Gradle 8.9 and run:

```bash
gradle --version
gradle :app:assembleDebug
```

Open the folder containing `settings.gradle.kts` in IntelliJ IDEA and configure Gradle JVM to JDK 17. Set `ANDROID_HOME` to your Android SDK location if IntelliJ does not detect it.

## Current skeleton flow

WelcomeActivity -> FeaturesActivity -> PermissionsActivity -> MigrationActivity -> MainActivity

The welcome screen is the app launcher entry point. MainActivity is registered as a HOME activity and is the destination launcher screen.

## Permission design notes

- Screenshot selection uses Android Photo Picker. Broad storage permission is deliberately not requested.
- Install unknown apps, display-over-other-apps, and modify-system-settings are special settings accesses and are optional.
- POST_NOTIFICATIONS is requested only on Android 13+ and only grants permission to post this app's notifications.
- Notification-listener access is a separate, sensitive capability and is not implemented.
- Becoming the default Home app requires explicit user confirmation through the system role/settings UI; it is not a runtime permission.
- Do not request permissions that the implemented feature does not need.

## Screenshot migration

- The UI allows up to three drawer screenshots and three home screenshots.
- The same URI cannot be accepted into both groups.
- LocalScreenshotOcr uses the bundled ML Kit text-recognition model locally. It extracts text blocks and normalized bounding boxes only.
- OCR does not identify app package names, reliably detect every icon, infer all grid structure, or recover invisible/off-screen content.
- The actual classification, app matching, layout reconstruction, and persistence stages are TODOs.
- The first demo should support only a conventional grid drawer and left-to-right home grid. Unsupported styles should use the empty-home and alphabetical-drawer fallback.

## Planned architecture

- `data/model`: normalized layout and item models
- `migration`: screenshot OCR, classifier, app matcher, and migration orchestration
- `data/repository`: persistent home/drawer layout storage
- `ui/home`: home screen, tabs, folders, widgets, drag/drop
- `ui/drawer`: searchable app grid, folder tab, app context menu
- `ui/settings`: icon packs and launcher preferences

## Not implemented yet

Widgets/AppWidgetHost, home folders, drawer folder tab, icon-pack loading, app renaming, hide/unhide, APK backup/export, uninstall, app info, drag/drop, app install/update/remove observation, wallpaper picker, tab persistence, actual OCR-to-grid inference, app matching, and default-home prompt timing refinement.

The skeleton is a starting point, not a finished launcher or a tested APK.

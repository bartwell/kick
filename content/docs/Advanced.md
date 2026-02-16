## Table of Contents

- [Usage](#usage)
- [Modules](#modules)
    - [Ktor3](#ktor3)
    - [SQLite](#sqlite)
    - [Logging](#logging)
    - [Firebase Cloud Messaging](#firebase-cloud-messaging)
    - [Firebase Analytics](#firebase-analytics)
    - [Multiplatform Settings](#multiplatform-settings)
    - [Control Panel](#control-panel)
    - [File Explorer](#file-explorer)
    - [Layout (Beta)](#layout)
    - [Overlay](#overlay)
    - [Runner](#runner)
- [Advanced Module Configuration](#advanced-module-configuration)
- [Shortcuts](#shortcuts)
- [Launching Kick](#launching-kick)

## Usage

### Gradle plugin (recommended)

You can use the **Kick Gradle plugin** (`ru.bartwell.kick`) to add Kick dependencies and configure Kotlin/Native framework exports automatically:

```kotlin
plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.1.21"
    id("ru.bartwell.kick") version "1.0.0"
}

kick {
    enabledAuto() // or enabled() / disabled()
    modules {
        controlPanel()
        fileExplorer()
        firebaseAnalytics()
        firebaseCloudMessaging()
        ktor3()
        layout()
        logging()
        multiplatformSettings()
        overlay()
        room()
        runner()
        sqldelight()
    }
}
// Optional: enableKick(false) or -Pkick.enabled=true|false for override
```

The plugin adds `main-core`, `main-runtime`/`main-runtime-stub` and the chosen module artifacts to `commonMain`, and sets framework `export(...)` for all Kotlin/Native targets. Order of `plugins` does not matter; Kotlin Multiplatform is required.

### Manual setup

Alternatively, add every module dependency in `shared/build.gradle.kts` and choose real or stub implementations using the `isRelease` flag:

```kotlin
val isRelease = /* your logic to determine release vs. debug */

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            export("ru.bartwell.kick:main-core:1.0.0")
            if (isRelease) {
                export("ru.bartwell.kick:main-runtime-stub:1.0.0")
            } else {
                export("ru.bartwell.kick:main-runtime:1.0.0")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("ru.bartwell.kick:main-core:1.0.0")
            if (isRelease) {
                implementation("ru.bartwell.kick:main-runtime-stub:1.0.0")
                implementation("ru.bartwell.kick:ktor3-stub:1.0.0")
                implementation("ru.bartwell.kick:sqlite-runtime-stub:1.0.0")
                implementation("ru.bartwell.kick:sqlite-sqldelight-adapter-stub:1.0.0")
                implementation("ru.bartwell.kick:sqlite-room-adapter-stub:1.0.0")
                implementation("ru.bartwell.kick:logging-stub:1.0.0")
                implementation("ru.bartwell.kick:multiplatform-settings-stub:1.0.0")
                implementation("ru.bartwell.kick:file-explorer-stub:1.0.0")
                implementation("ru.bartwell.kick:layout-stub:1.0.0")
                implementation("ru.bartwell.kick:firebase-cloud-messaging-stub:1.0.0")
                implementation("ru.bartwell.kick:firebase-analytics-stub:1.0.0")
            } else {
                implementation("ru.bartwell.kick:main-runtime:1.0.0")
                implementation("ru.bartwell.kick:ktor3:1.0.0")
                implementation("ru.bartwell.kick:sqlite-core:1.0.0")
                implementation("ru.bartwell.kick:sqlite-runtime:1.0.0")
                implementation("ru.bartwell.kick:sqlite-sqldelight-adapter:1.0.0")
                implementation("ru.bartwell.kick:sqlite-room-adapter:1.0.0")
                implementation("ru.bartwell.kick:logging:1.0.0")
                implementation("ru.bartwell.kick:multiplatform-settings:1.0.0")
                implementation("ru.bartwell.kick:file-explorer:1.0.0")
                implementation("ru.bartwell.kick:layout:1.0.0")
                implementation("ru.bartwell.kick:firebase-cloud-messaging:1.0.0")
                implementation("ru.bartwell.kick:firebase-analytics:1.0.0")
            }
        }
    }
}
```

**Note:** stub modules provide no-op implementations instead of the full implementations so your release build stays lightweight.

Because many Android API calls require a Context, you need to wrap it using `PlatformContext`. Here is a sample of initialization:

```kotlin
// val context = androidContext.toPlatformContext() // For Android
// val context = getPlatformContext() // For iOS and desktop
// val context = platformContext() // In Compose
// let context: PlatformContext = PlatformContextKt.getPlatformContext() // For Swift
Kick.init(context) {
    module(SqliteModule(SqlDelightWrapper(sqlDelightDriver)))
    module(SqliteModule(RoomWrapper(roomDatabase)))
    module(LoggingModule(context))
    module(Ktor3Module(context))
    module(MultiplatformSettingsModule(listOf("MySettings1" to settings1, "MySettings2" to settings2)))
    module(FileExplorerModule())
    module(LayoutModule(context))
    module(FirebaseCloudMessagingModule(context))
    module(FirebaseAnalyticsModule(context))
}
```

## Modules

### Ktor3

<a href="content/screenshots/ktor.jpg" target="_blank" rel="noopener noreferrer">
  <img src="content/screenshots/ktor.jpg" alt="" height="120">
</a>

Monitor HTTP traffic performed with Ktor3. Just install the provided plugin:

```kotlin
val client = HttpClient(getEngineFactory()) {
    install(KickKtor3Plugin)
}
```

### SQLite

<a href="content/screenshots/sqlite.jpg" target="_blank" rel="noopener noreferrer">
  <img src="content/screenshots/sqlite.jpg" alt="" height="120">
</a>

View and edit SQLite databases. Use one of the provided adapters (or both if you are really using Room and SqlDelight in one application) for your favorite library.

```kotlin
// SqlDelight
module(SqliteModule(SqlDelightWrapper(sqlDelightDriver)))

// Room
module(SqliteModule(RoomWrapper(roomDatabase)))
```

### Logging

<a href="content/screenshots/logging.jpg" target="_blank" rel="noopener noreferrer">
  <img src="content/screenshots/logging.jpg" alt="" height="120">
</a>

Gather and review log messages right from the viewer. Add logs with a simple call:

```kotlin
Kick.log(LogLevel.INFO, "message")
```

You can also pipe existing [Napier](https://github.com/AAkira/Napier) logs into Kick so you only configure logging only once:

```kotlin
Napier.base(object : Antilog() {
    override fun performLog(priority: NapierLogLevel, tag: String?, throwable: Throwable?, message: String?) {
        val level = when (priority) {
            NapierLogLevel.VERBOSE -> LogLevel.VERBOSE
            NapierLogLevel.DEBUG -> LogLevel.DEBUG
            NapierLogLevel.INFO -> LogLevel.INFO
            NapierLogLevel.WARNING -> LogLevel.WARNING
            NapierLogLevel.ERROR -> LogLevel.ERROR
            NapierLogLevel.ASSERT -> LogLevel.ASSERT
        }
        Kick.log(level, message)
    }
})
```

#### Labels and Filtering

The log viewer supports two kinds of filtering:

- Message filter - click the filter icon to filter by a text query contained in the message.
- Label filter - when a label extractor is provided, the viewer shows label chips above the list. Clicking chips toggles selected labels. Multiple selected labels are combined with AND. Label chips reflect the current text filter, so you can combine both.

Provide a label extractor via the `LoggingModule` constructor. A ready‑to‑use `BracketLabelExtractor` is available; it extracts labels from square brackets like `[UI]`, `[Network]` in the beginning or anywhere in the message:

```kotlin
import ru.bartwell.kick.module.logging.LoggingModule
import ru.bartwell.kick.module.logging.feature.table.util.BracketLabelExtractor

Kick.init(context) {
    module(LoggingModule(context, BracketLabelExtractor()))
}
```

You can also implement a custom extractor by providing your own `LabelExtractor`:

```kotlin
import ru.bartwell.kick.module.logging.feature.table.util.LabelExtractor

class HashLabelExtractor : LabelExtractor {
    private val regex = Regex("#(\\w+)")
    override fun extract(message: String?): Set<String> =
        if (message.isNullOrEmpty()) emptySet()
        else regex.findAll(message).map { it.groupValues[1] }.toSet()
}

Kick.init(context) {
    module(LoggingModule(context, HashLabelExtractor()))
}
```

If no extractor is provided, label chips are hidden and only text filtering is available.

### Firebase Cloud Messaging

Capture and inspect push notifications (FCM on Android, APNS on iOS) inside Kick.

**Enable the module:** add `FirebaseCloudMessagingModule(context)` to your `Kick.init { ... }` module list.

**Platforms:** supported on Android and iOS. Not supported on JVM and Web.

**Android (FCM):** call `Kick.firebaseCloudMessaging.handleFcm(message)` from your `FirebaseMessagingService.onMessageReceived`.

```kotlin
class MyMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        // your app logic...
        Kick.firebaseCloudMessaging.handleFcm(message)
    }
}
```

**iOS (APNS):** call the appropriate handler when a push is received.
Use one of the two `handleApnsPayload` overloads depending on the payload type you have (`NSDictionary` or Swift `[AnyHashable: Any]`),
or call `handleApnsNotification` if you already have a `UNNotification`.

```swift
import UserNotifications
import shared

final class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        KickCompanion.shared.firebaseCloudMessaging.handleApnsPayload(userInfo: userInfo)
        completionHandler(.noData)
    }

    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: NSDictionary,
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        KickCompanion.shared.firebaseCloudMessaging.handleApnsPayload(userInfo: userInfo)
        completionHandler(.noData)
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        KickCompanion.shared.firebaseCloudMessaging.handleApnsNotification(notification: response.notification)
        completionHandler()
    }
}
```

**iOS (Token/FID):** the module does not link Firebase SDK on iOS.  
Pass values from your existing Firebase integration (CocoaPods, SPM, manual).

```swift
import FirebaseMessaging
import FirebaseInstallations
import shared

// FCM token
Messaging.messaging().token { token, _ in
    KickCompanion.shared.firebaseCloudMessaging.setFcmToken(token: token)
}

// Firebase Installation ID
Installations.installations().installationID { id, _ in
    KickCompanion.shared.firebaseCloudMessaging.setFirebaseInstallationId(id: id)
}
```

If the token or installation ID changes, call the same setters again — Kick updates the UI automatically.

### Firebase Analytics

Capture analytics calls made by your app and inspect them inside Kick (events, user id, user properties).

**Enable the module:** add `FirebaseAnalyticsModule(context)` to your `Kick.init { ... }` module list.

**Platforms:** supported on Android and iOS. Not supported on JVM and Web.

**Important:** this module does not auto-hook Firebase SDK calls.  
Call `Kick.firebaseAnalytics.*` in the same places where your app already sends analytics to Firebase.

#### Where to call it

Use a single analytics wrapper/service in your app and call both:
- Firebase SDK (`FirebaseAnalytics` / `Analytics`)
- Kick accessor (`Kick.firebaseAnalytics`)

This keeps instrumentation in one place and prevents missing events.

#### Methods reference

`Kick.firebaseAnalytics.logEvent(name, params)`
- Logs an event for the Kick viewer.
- Android signature: `logEvent(name: String, params: Bundle?)`
- iOS signatures:
  - `logEvent(name: String, params: NSDictionary?)`
  - `logEvent(name: String, params: Map<Any?, *>?)`

`Kick.firebaseAnalytics.setUserId(id)`
- Sets or clears current user id in Kick viewer (`null` clears).
- Android/iOS signature: `setUserId(id: String?)`

`Kick.firebaseAnalytics.setUserProperty(name, value)`
- Logs user property update in Kick viewer.
- Android/iOS signature: `setUserProperty(name: String, value: String)`

#### Android integration example

```kotlin
class AnalyticsReporter(
    private val firebaseAnalytics: FirebaseAnalytics,
) {
    fun logEvent(name: String, params: Bundle?) {
        firebaseAnalytics.logEvent(name, params)
        Kick.firebaseAnalytics.logEvent(name, params)
    }

    fun setUserId(id: String?) {
        firebaseAnalytics.setUserId(id)
        Kick.firebaseAnalytics.setUserId(id)
    }

    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
        Kick.firebaseAnalytics.setUserProperty(name, value)
    }
}
```

#### iOS integration example (Swift)

```swift
import FirebaseAnalytics
import shared

final class AnalyticsReporter {
    func logEvent(name: String, params: [String: Any]?) {
        Analytics.logEvent(name, parameters: params)
        KickCompanion.shared.firebaseAnalytics.logEvent(name: name, params: params)
    }

    func setUserId(_ id: String?) {
        Analytics.setUserID(id)
        KickCompanion.shared.firebaseAnalytics.setUserId(id: id)
    }

    func setUserProperty(name: String, value: String) {
        Analytics.setUserProperty(value, forName: name)
        KickCompanion.shared.firebaseAnalytics.setUserProperty(name: name, value: value)
    }
}
```

### Multiplatform Settings

<a href="content/screenshots/settings.jpg" target="_blank" rel="noopener noreferrer">
  <img src="content/screenshots/settings.jpg" alt="" height="120">
</a>

Edit values stored with [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings). Register as many storages as you need and switch between them at runtime.
**Note:** Multiplatform Settings doesn’t expose metadata about field types, so Kick can only display and edit values as plain text. When type information becomes available, it will be possible to implement type‑specific views — for example, a switch for Boolean or a numeric input for Int, Long, Double, or Float.

### Control Panel

<a href="content/screenshots/configuration.jpg" target="_blank" rel="noopener noreferrer">
  <img src="content/screenshots/configuration.jpg" alt="" height="120">
</a>

Create configuration options, such as an endpoint URL or debug flags, available during app runtime.
Provide a list of `ControlPanelItem` objects to `ControlPanelModule`. Each item is either an input (`InputType`) or an action (`ActionType`).
You can optionally group items by `category` to keep long lists organized. Categories are collapsible; items without a category are shown first and are always visible.
Inputs can optionally include an editor UI:

```
ControlPanelModule(
    context = context,
    items = listOf(
        ControlPanelItem(
            name = "featureEnabled",
            type = InputType.Boolean(true),
            category = "General",
        ),
        ControlPanelItem(
            name = "maxItems",
            type = InputType.Int(DEFAULT_MAX_ITEMS),
            editor = Editor.InputNumber(min = 1.0, max = 10.0),
            category = "General",
        ),
        ControlPanelItem(
            name = "endpoint",
            type = InputType.String("https://example.com"),
            editor = Editor.InputString(singleLine = true),
            category = "Network",
        ),
        ControlPanelItem(
            name = "list",
            type = InputType.String("Item 2"),
            editor = Editor.List(
                listOf(
                    InputType.String("Item 1"),
                    InputType.String("Item 2"),
                    InputType.String("Item 3"),
                )
            ),
            category = "General",
        ),
        ControlPanelItem(
            name = "Refresh Cache",
            type = ActionType.Button(id = "refresh_cache"),
            category = "Actions",
        ),
    )
)
```

Access these values anywhere using the convenient `Kick.controlPanel.get*()` methods:

```
Kick.controlPanel.getBoolean("featureEnabled")
Kick.controlPanel.getInt("maxItems")
Kick.controlPanel.getString("endpoint")
Kick.controlPanel.getString("list")
```

You can also set values programmatically via `Kick.controlPanel.set*()` methods:

```kotlin
Kick.controlPanel.setBoolean("featureEnabled", true)
Kick.controlPanel.setInt("maxItems", 8)
Kick.controlPanel.setLong("timeoutMs", 15_000L)
Kick.controlPanel.setFloat("ratio", 0.75f)
Kick.controlPanel.setDouble("threshold", 0.95)
Kick.controlPanel.setString("endpoint", "https://staging.example.com")
```

#### Actions

You can also add action buttons to trigger code in your app. Collect control panel events and handle button IDs you defined in `ControlPanelItem(type = ActionType.Button("id"))`:

```
Kick.controlPanel.events.collect { event ->
    when (event) {
        is ControlPanelEvent.ButtonClicked -> when (event.id) {
            "refresh_cache" -> refreshCache()
        }
        else -> Unit
    }
}
```

### File Explorer

<a href="content/screenshots/files.jpg" target="_blank" rel="noopener noreferrer">
  <img src="content/screenshots/files.jpg" alt="" height="120">
</a>

Browse the file system directly within the viewer—handy for quick checks of generated files or cached data.

### Layout

<a href="content/screenshots/layout.jpg" target="_blank" rel="noopener noreferrer">
  <img src="content/screenshots/layout.jpg" alt="" height="120">
</a>

Inspect the current screen’s UI hierarchy without touching code. See a tree of views and key details like bounds, visibility, text, etc.

Trigger it by shaking on Android and iOS or pressing ⌘⌥⇧K on macOS / Ctrl+Alt+Shift+K on Windows and Linux. Triggers work only while the module is enabled.

### Overlay

<a href="content/screenshots/overlay.jpg" target="_blank" rel="noopener noreferrer">
  <img src="content/screenshots/overlay.jpg" alt="" height="120">
</a>

A small floating panel that shows live debug values over your app and updates in real time. You can drag it around or hide it at any moment. Ideal for tracking states or any quick metric while testing a scenario.

Enable the module and update values from anywhere:

```kotlin
Kick.init(context) {
    module(OverlayModule(context))
}

// Update live values
Kick.overlay.set("fps", 42)
Kick.overlay.set("isWsConnected", true)
```

You can also show/hide the panel programmatically if needed:

```kotlin
Kick.overlay.show()         // show floating panel
Kick.overlay.hide()         // hide it
```

#### Categories

Group values by categories and switch them in the Overlay settings screen (default category is "Default"). The floating window shows only the values of the currently selected category. The selection is persisted across app restarts.

```kotlin
// Write values into a specific category
Kick.overlay.set("fps", 42, "Performance")
Kick.overlay.set("isWsConnected", true, "Network")
```

#### Providers

Overlay modules can populate categories automatically through `OverlayProvider`s. By default `OverlayModule` registers the built-in `PerformanceOverlayProvider`, which exposes CPU and memory usage in the "Performance" category whenever the floating panel is visible.

Pass custom providers to `OverlayModule` to emit additional metrics:

```kotlin
Kick.init(context) {
    module(
        OverlayModule(
            context = context,
            providers = listOf(
                PerformanceOverlayProvider(),
                MyCustomOverlayProvider(), // implements OverlayProvider
            ),
        ),
    )
}
```

Implement `OverlayProvider` to decide when your provider should run, which categories it contributes to, and how it updates values via `Kick.overlay.set` inside the supplied coroutine scope.

### Runner

Run ad‑hoc debug actions from inside Kick and render their results with pluggable renderers.

Built-in renderers:
- `JsonRunnerRenderer` — pretty-prints `String?` JSON (lenient, indented).
- `ImageRunnerRenderer` — shows `PlatformImage?` (Bitmap/UIImage/BufferedImage/ImageBitmap wrapper).
- `ObjectRunnerRenderer` — displays `Any?` via `toString()`.
You can plug in your own renderer by implementing `RunnerRenderer<T>` (with `setResult(T)` and `@Composable fun RenderContent(...)`) and passing it to `addCall` with the matching `T`.

Add dependencies:
```kotlin
// debug
implementation("ru.bartwell.kick:runner:1.0.0")
// release (no-op)
implementation("ru.bartwell.kick:runner-stub:1.0.0")
```

Initialize:
```kotlin
Kick.init(context) {
    module(RunnerModule())
}
```

Register actions:
```kotlin
Kick.runner.addCall(
    title = "Show JSON",
    description = "Pretty print payload",
    renderer = JsonRunnerRenderer()
) {
    """{"status":"ok","ts":${System.currentTimeMillis()}}"""
}
```

Platform images:
- Create with `PlatformImage.fromImageBitmap(imageBitmap)` or `PlatformImage.fromNative(native)` (Bitmap/UIImage/BufferedImage).
- Render via `ImageRunnerRenderer`.

### Advanced Module Configuration

You don't need to add all the available modules. Just include the ones you need. Here only logging and network inspection are enabled:

```kotlin
val isRelease = /* your logic to determine release vs. debug */

    if (isRelease) {
        implementation("ru.bartwell.kick:logging-stub:1.0.0")
        implementation("ru.bartwell.kick:ktor3-stub:1.0.0")
    } else {
        implementation("ru.bartwell.kick:logging:1.0.0")
        implementation("ru.bartwell.kick:ktor3:1.0.0")
    }
```

```kotlin
Kick.init(context) {
    module(LoggingModule(context))
    module(Ktor3Module(context))
}
```

### Launching Kick

Call `Kick.launch(context)` whenever you want to open the viewer:

In Kotlin:

```kotlin
val context = platformContext()
Button(
    onClick = { Kick.launch(context) },
    content = { Text(text = "Kick") }
)
```

In Swift:

```swift
Button("Kick") {
    KickKt.shared.launch(context: PlatformContextKt.getPlatformContext())
}
```

To close the viewer programmatically, call `Kick.close()`:

In Kotlin:

```kotlin
Kick.close()
```

In Swift:

```swift
KickKt.shared.close()
```

## Shortcuts

By default, Kick adds a shortcut to your app’s launcher icon (accessible via long-press). To disable it, pass `enableShortcut = false` during initialization:

```kotlin
Kick.init(context) {
    enableShortcut = false
    // modules...
}
```

On iOS you need to configure your `AppDelegate` or `UISceneDelegate` as follows:

```swift
class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        return ShortcutActionHandler.shared.getConfiguration(session: connectingSceneSession)
    }
}
```

Desktop (Windows/macOS/Linux): when supported by the OS, Kick also adds a System Tray icon with the label "Inspect with Kick". Clicking the tray icon launches the viewer. The icon is removed automatically when the host app exits. This tray shortcut respects the same `enableShortcut` flag — set it to `false` to disable the icon.

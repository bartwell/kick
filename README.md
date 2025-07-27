# Kick: Kotlin Inspection & Control Kit

Debugging doesn't have to be a headache.

Kick simplifies inspection and control across Android, iOS and desktop with a unified, modular toolkit built on Compose Multiplatform. Replace scattered utilities with a single customizable solution embedded right into your app—no more juggling separate tools, no more command-line headaches.

Effortlessly manage logs, network traffic, SQLite databases, file systems and multiplatform settings directly from within your application. Enable only what you need, easily extend functionality with custom modules and keep your app lean and efficient.

Less complexity, faster development, total visibility. That's Kick.

## Table of Contents

- [Features](#features)
- [Usage](#usage)
- [Modules](#modules)
    - [Ktor3](#ktor3)
    - [SQLite](#sqlite)
    - [Logging](#logging)
    - [Multiplatform Settings](#multiplatform-settings)
    - [Configuration](#configuration)
    - [File Explorer](#file-explorer)
- [Advanced Module Configuration](#advanced-module-configuration)
- [Shortcuts](#shortcuts)
- [Launching Kick](#launching-kick)
- [Contributing](#contributing)
- [License](#license)

## Features

- Cross-platform, unified toolkit — inspect and debug Android, iOS and desktop apps with one seamless solution built on Compose Multiplatform
- Fully modular — activate only what you need, or extend effortlessly with custom modules
- Embedded viewer — inspect logs, network traffic, SQLite databases, file systems and multiplatform settings directly from within your app
- Easy shortcuts — launch inspection tools quickly via shortcuts with a single click
- Simple integration — just initialize with `Kick.init`

## Usage

Kick is initialized once with a platform context and a list of modules. Add every module dependency in `shared/build.gradle.kts` and choose real or stub implementations using the `isRelease` flag:

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
}
```

## Modules

### Ktor3

Monitor HTTP traffic performed with Ktor3. Just install the provided plugin:

```kotlin
val client = HttpClient(getEngineFactory()) {
    install(KickKtor3Plugin)
}
```

### SQLite

View and edit SQLite databases. Use one of the provided adapters (or both if you are really using Room and SqlDelight in one application) for your favorite library.

```kotlin
// SqlDelight
module(SqliteModule(SqlDelightWrapper(sqlDelightDriver)))

// Room
module(SqliteModule(RoomWrapper(roomDatabase)))
```

### Logging

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

### Multiplatform Settings

Edit values stored with [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings). Register as many storages as you need and switch between them at runtime.
**Note:** Multiplatform Settings doesn’t expose metadata about field types, so Kick can only display and edit values as plain text. When type information becomes available, it will be possible to implement type‑specific views — for example, a switch for Boolean or a numeric input for Int, Long, Double, or Float.

### Configuration

Create configuration options, such as an endpoint URL or debug flags, available during app runtime.
Provide a list of `ConfigurationItem` objects to `ConfigurationModule`. Each item defines its default `ValueType` and can optionally include an editor UI:

```
ConfigurationModule(
    context = context,
    items = listOf(
        ConfigurationItem(
            name = "featureEnabled",
            default = ValueType.Boolean(true),
        ),
        ConfigurationItem(
            name = "maxItems",
            default = ValueType.Int(DEFAULT_MAX_ITEMS),
            editor = Editor.InputNumber(min = 1.0, max = 10.0),
        ),
        ConfigurationItem(
            name = "endpoint",
            default = ValueType.String("https://example.com"),
            editor = Editor.InputString(singleLine = true),
        ),
        ConfigurationItem(
            name = "list",
            default = ValueType.String("Item 2"),
            editor = Editor.List(
                listOf(
                    ValueType.String("Item 1"),
                    ValueType.String("Item 2"),
                    ValueType.String("Item 3"),
                )
            ),
        ),
    )
)
```

Access these values anywhere using the convenient `Kick.configuration.get*()` methods:

```
Kick.configuration.getBoolean("featureEnabled")
Kick.configuration.getInt("maxItems")
Kick.configuration.getString("endpoint")
Kick.configuration.getString("list")
```

### File Explorer

Browse the file system directly within the viewer—handy for quick checks of generated files or cached data.

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


## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests with improvements and new modules.

## License

```
Copyright 2025 Artem Bazhanov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
```

Kick is distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

---

**Happy debugging!** If you have any questions or need further assistance, feel free to open an issue.

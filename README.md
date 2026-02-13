# Kick: Kotlin Inspection & Control Kit

Debugging doesn't have to be a headache.

Kick simplifies inspection and control across Android, iOS and desktop with a unified, modular toolkit built on Compose Multiplatform. Replace scattered utilities with a single customizable solution embedded right into your app—no more juggling separate tools, no more command-line headaches.

Effortlessly manage logs, network traffic, SQLite databases, file systems and multiplatform settings directly from within your application. Enable only what you need, easily extend functionality with custom modules and keep your app lean and efficient.

Less complexity, faster development, total visibility. That's Kick.

## Table of Contents

- [Features](#features)
- [Modules](#modules)
- [Integration](#integration)
    - [Example](#example)
    - [Wizard](#wizard)
    - [Advanced](content/docs/Advanced.md)
- [Contributing](#contributing)
- [License](#license)

## Features

- Cross-platform, unified toolkit — inspect and debug Android, iOS and desktop apps with one seamless solution built on Compose Multiplatform
- Fully modular — activate only what you need, or extend effortlessly with custom modules
- Embedded viewer — inspect logs, network traffic, SQLite databases, file systems and multiplatform settings directly from within your app
- Easy shortcuts — launch inspection tools quickly via shortcuts with a single click
- Simple integration — just initialize with `Kick.init`

## Modules

| Module | Description | Platforms |
| --- | --- | --- |
| [Ktor3](content/docs/Advanced.md#ktor3) | Inspect HTTP traffic performed with Ktor3. | All |
| [SQLDelight](content/docs/Advanced.md#sqlite) | View and edit SQLite databases via the SQLDelight adapter. | All |
| [Room](content/docs/Advanced.md#sqlite) | View and edit SQLite databases via the Room adapter. | Android/iOS/JVM |
| [Logging](content/docs/Advanced.md#logging) | Capture and filter logs directly in the viewer. | All |
| [Firebase Cloud Messaging](content/docs/Advanced.md#firebase-cloud-messaging) | Inspect push payloads from FCM and APNS. | Android/iOS |
| [Firebase Analytics](content/docs/Advanced.md#firebase-analytics) | Track analytics events and user properties in Kick. | Android/iOS |
| [Multiplatform Settings](content/docs/Advanced.md#multiplatform-settings) | Browse and edit registered settings storages. | All |
| [Control Panel](content/docs/Advanced.md#control-panel) | Add runtime inputs and actions for debug configuration. | All |
| [File Explorer](content/docs/Advanced.md#file-explorer) | Explore app files and cached data from Kick UI. | All |
| [Layout](content/docs/Advanced.md#layout) | Inspect UI hierarchy, bounds, and view properties. | Android/iOS/JVM |
| [Overlay](content/docs/Advanced.md#overlay) | Show live debug metrics in a floating panel. | All |
| [Runner](content/docs/Advanced.md#runner) | Run ad-hoc debug calls with pluggable renderers. | All |

## Integration

### Example

Add the plugin and enable one module (`FileExplorer`):

```kotlin
plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.1.21"
    id("ru.bartwell.kick") version "1.0.0"
}

kick {
    enabled = KickEnabled.Auto
    modules(KickModule.FileExplorer)
}
```

Then initialize Kick with one module:

```kotlin
// val context = androidContext.toPlatformContext() // For Android
// val context = getPlatformContext() // For iOS and desktop
// val context = platformContext() // In Compose
Kick.init(context) {
    module(FileExplorerModule())
}
```

`enableKick(false)` forces stub artifacts in the current Gradle build script (equivalent to disabled mode):

```kotlin
enableKick(false)
```

`-Pkick.enabled=true|false` has highest priority and overrides both `enableKick(...)` and `kick { enabled = ... }`.

### Wizard

Use [Integration Wizard](content/wizard/index.html) to generate ready-to-paste plugin configuration and initialization snippets for selected modules and platforms.

For full setup details (manual integration, advanced configuration, shortcuts, launching, and full module docs), see [Advanced](content/docs/Advanced.md).

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

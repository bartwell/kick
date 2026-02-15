const PLATFORM_ORDER = ["android", "ios", "jvm", "wasm"];

const PLATFORM_FILE_PATHS = {
  android: "shared/src/androidMain/kotlin/KickBootstrap.kt",
  ios: "shared/src/iosMain/kotlin/KickBootstrap.kt",
  jvm: "shared/src/jvmMain/kotlin/KickBootstrap.kt",
  wasm: "shared/src/wasmJsMain/kotlin/KickBootstrap.kt",
};

function unique(values) {
  const seen = new Set();
  const ordered = [];
  values.forEach((value) => {
    if (!seen.has(value)) {
      seen.add(value);
      ordered.push(value);
    }
  });
  return ordered;
}

function escapeKotlinString(value) {
  return String(value)
    .replace(/\\/g, "\\\\")
    .replace(/"/g, "\\\"")
    .replace(/\n/g, "\\n");
}

function normalizeTextList(value) {
  if (Array.isArray(value)) {
    return value.map((item) => String(item).trim()).filter(Boolean);
  }
  if (typeof value === "string") {
    return value
      .split(/[\n,]/)
      .map((item) => item.trim())
      .filter(Boolean);
  }
  return [];
}

function normalizeSettingsConfig(config) {
  const fromArray = Array.isArray(config?.storages)
    ? config.storages
      .map((item) => (typeof item?.displayName === "string" ? item.displayName.trim() : ""))
      .filter(Boolean)
    : [];

  if (fromArray.length > 0) {
    return fromArray;
  }
  return ["Default"];
}

function normalizeControlPanelConfig(config) {
  const rows = Array.isArray(config?.items) ? config.items : [];
  return rows
    .map((item, index) => {
      const name = typeof item?.name === "string" && item.name.trim()
        ? item.name.trim()
        : `item_${index + 1}`;
      const type = typeof item?.type === "string" ? item.type : "string";
      const category = typeof item?.category === "string" ? item.category.trim() : "";
      const editor = typeof item?.editor === "string" ? item.editor : "none";
      const listValues = normalizeTextList(item?.listValues);
      return {
        name,
        type,
        category,
        editor,
        listValues,
      };
    });
}

function buildControlPanelTypeLine(item) {
  switch (item.type) {
    case "bool":
      return "InputType.Boolean(true)";
    case "int":
      return "InputType.Int(0)";
    case "list":
      return "InputType.String(\"Option 1\")";
    case "button": {
      const safeId = escapeKotlinString(item.name.toLowerCase().replace(/\s+/g, "_"));
      return `ActionType.Button(\"${safeId}\")`;
    }
    case "string":
    default:
      return "InputType.String(\"Value\")";
  }
}

function buildControlPanelEditorLine(item) {
  if (item.editor === "none") {
    return null;
  }

  if (item.editor === "input_number") {
    return "Editor.InputNumber()";
  }

  if (item.editor === "input_string") {
    return "Editor.InputString(singleLine = true)";
  }

  if (item.editor === "list") {
    const options = item.listValues.length > 0 ? item.listValues : ["Option 1", "Option 2"];
    const optionsCode = options
      .map((value) => `InputType.String(\"${escapeKotlinString(value)}\")`)
      .join(", ");
    return `Editor.List(listOf(${optionsCode}))`;
  }

  return null;
}

function buildControlPanelItemsFunction(config) {
  const rows = normalizeControlPanelConfig(config);
  const lines = [];
  lines.push("    private fun buildControlPanelItems(): List<ControlPanelItem> = listOf(");

  if (rows.length === 0) {
    lines.push("        // Add items in wizard config or keep list empty.");
  }

  rows.forEach((item) => {
    const typeLine = buildControlPanelTypeLine(item);
    const editorLine = buildControlPanelEditorLine(item);
    lines.push("        ControlPanelItem(");
    lines.push(`            name = \"${escapeKotlinString(item.name)}\",
            type = ${typeLine},`);
    if (editorLine) {
      lines.push(`            editor = ${editorLine},`);
    }
    if (item.category) {
      lines.push(`            category = \"${escapeKotlinString(item.category)}\",`);
    }
    lines.push("        ),");
  });

  lines.push("    )");
  return lines;
}

function getSelectedModules(state, modulesById) {
  return state.selectedModules
    .map((id) => modulesById[id])
    .filter(Boolean);
}

function requiresPlatformBridgeModule(module, selectedPlatforms) {
  const activePlatforms = (selectedPlatforms || []).filter((platform) => PLATFORM_ORDER.includes(platform));
  if (activePlatforms.length === 0) {
    return false;
  }
  return activePlatforms.some((platform) => !module.supportedPlatforms.includes(platform));
}

function isPlatformSupported(module, platform) {
  return module.supportedPlatforms.includes(platform);
}

export function getUnsupportedPlatforms(moduleDescription, selectedPlatforms) {
  return selectedPlatforms.filter((platform) => !moduleDescription.supportedPlatforms.includes(platform));
}

function collectKickModuleMethods(selectedModules) {
  const methods = [];
  selectedModules.forEach((module) => {
    if (module.kickModuleMethod) {
      methods.push(module.kickModuleMethod);
    }
    if (Array.isArray(module.extraKickModuleMethods)) {
      module.extraKickModuleMethods.forEach((entry) => {
        if (entry) {
          methods.push(entry);
        }
      });
    }
  });
  return unique(methods);
}

function buildGradleSnippet(selectedModules, kickVersion) {
  const methods = collectKickModuleMethods(selectedModules);

  const lines = [];
  lines.push("plugins {");
  lines.push(`    id(\"ru.bartwell.kick\") version \"${kickVersion}\"`);
  lines.push("}");
  lines.push("");
  lines.push("kick {");
  lines.push("    enabledAuto() // or enabled() / disabled()");
  lines.push("    modules {");
  if (methods.length > 0) {
    methods.forEach((method) => {
      lines.push(`        ${method}();`);
    });
  } else {
    lines.push("        // Select at least one module, e.g. fileExplorer(), ktor3()");
  }
  lines.push("    }");
  lines.push("}");
  lines.push("");
  lines.push("// Enable/disable strategy:");
  lines.push("// Use your own build logic and call enableKick(false) for release variants if needed.");
  lines.push("// In CI, force behavior per job with -Pkick.enabled=true or -Pkick.enabled=false.");

  return lines.join("\n");
}

function buildCommonSnippet(state, selectedModules, hasPlatformBridge) {
  const imports = new Set([
    "ru.bartwell.kick.Kick",
    "ru.bartwell.kick.core.data.PlatformContext",
    "ru.bartwell.kick.runtime.init",
  ]);

  const depLines = [];
  const preInitLines = [];
  const moduleLines = [];
  const postInitLines = [];
  const helperFunctions = [];

  let hasNapierBridge = false;
  let hasRunnerSamples = false;
  const hasSqlDelightBridge = selectedModules.some((module) => module.id === "sqldelight");
  const hasRoomBridge = selectedModules.some((module) => module.id === "room");

  if (hasSqlDelightBridge) {
    imports.add("ru.bartwell.kick.module.sqlite.adapter.sqldelight.SqlDelightWrapper");
    depLines.push("    // Build SqlDelightWrapper in your app code and pass it via KickDeps.");
    depLines.push("    val sqlDelightWrapper: SqlDelightWrapper? = null,");
  }

  if (hasRoomBridge) {
    imports.add("ru.bartwell.kick.module.sqlite.adapter.room.RoomWrapper");
    depLines.push("    // Build RoomWrapper in your app code and pass it via KickDeps.");
    depLines.push("    // Keep roomWrapper = null on platforms where Room is not supported.");
    depLines.push("    val roomWrapper: RoomWrapper? = null,");
  }

  selectedModules.forEach((module) => {
    if (requiresPlatformBridgeModule(module, state.platforms)) {
      return;
    }

    const config = state.moduleConfigs[module.id] || {};

    if (module.id === "logging") {
      imports.add("ru.bartwell.kick.module.logging.LoggingModule");
      const useCustomExtractor = config.labelExtractor === "custom";
      if (useCustomExtractor) {
        imports.add("ru.bartwell.kick.module.logging.feature.table.util.LabelExtractor");
        helperFunctions.push(
          "    private fun customLoggingLabelExtractor(): LabelExtractor = object : LabelExtractor {",
          "        override fun extract(message: String?): Set<String> {",
          "            return emptySet()",
          "        }",
          "    }"
        );
        moduleLines.push("            module(LoggingModule(context, customLoggingLabelExtractor()))");
      } else {
        imports.add("ru.bartwell.kick.module.logging.feature.table.util.BracketLabelExtractor");
        moduleLines.push("            module(LoggingModule(context, BracketLabelExtractor()))");
      }
      if (config.integrateNapier === true) {
        hasNapierBridge = true;
      }
      return;
    }

    if (module.id === "ktor3") {
      imports.add("ru.bartwell.kick.module.ktor3.Ktor3Module");
      moduleLines.push("            module(Ktor3Module(context))");
      moduleLines.push("            // Ktor client integration (outside Kick.init):");
      moduleLines.push("            // install(KickKtor3Plugin)");
      return;
    }

    if (module.id === "control_panel") {
      imports.add("ru.bartwell.kick.module.controlpanel.ControlPanelModule");
      imports.add("ru.bartwell.kick.module.controlpanel.data.ControlPanelItem");
      imports.add("ru.bartwell.kick.module.controlpanel.data.InputType");
      imports.add("ru.bartwell.kick.module.controlpanel.data.ActionType");
      imports.add("ru.bartwell.kick.module.controlpanel.data.Editor");
      moduleLines.push("            module(ControlPanelModule(context = context, items = buildControlPanelItems()))");
      helperFunctions.push(...buildControlPanelItemsFunction(config));
      return;
    }

    if (module.id === "sqldelight") {
      imports.add("ru.bartwell.kick.module.sqlite.runtime.SqliteModule");
      moduleLines.push("            deps.sqlDelightWrapper?.let { wrapper ->");
      moduleLines.push("                module(SqliteModule(wrapper))");
      moduleLines.push("            }");
      return;
    }

    if (module.id === "room") {
      imports.add("ru.bartwell.kick.module.sqlite.runtime.SqliteModule");
      moduleLines.push("            deps.roomWrapper?.let { wrapper ->");
      moduleLines.push("                module(SqliteModule(wrapper))");
      moduleLines.push("            }");
      return;
    }

    if (module.id === "multiplatform_settings") {
      const storageNames = normalizeSettingsConfig(config);
      imports.add("com.russhwolf.settings.Settings");
      imports.add("ru.bartwell.kick.module.multiplatformsettings.MultiplatformSettingsModule");
      depLines.push("    // Provide Settings instances for storages listed below (leave null to skip).");
      storageNames.forEach((storageName, index) => {
        depLines.push(`    val settingsStorage${index + 1}: Settings? = null, // ${escapeKotlinString(storageName)}`);
      });
      preInitLines.push("        val settingsStorages = buildList<Pair<String, Settings>> {");
      storageNames.forEach((storageName, index) => {
        preInitLines.push(`            deps.settingsStorage${index + 1}?.let { add(\"${escapeKotlinString(storageName)}\" to it) }`);
      });
      preInitLines.push("        }");
      moduleLines.push("            module(MultiplatformSettingsModule(settingsStorages))");
      return;
    }

    if (module.id === "file_explorer") {
      imports.add("ru.bartwell.kick.module.explorer.FileExplorerModule");
      moduleLines.push("            module(FileExplorerModule())");
      return;
    }

    if (module.id === "layout") {
      imports.add("ru.bartwell.kick.module.layout.LayoutModule");
      moduleLines.push("            module(LayoutModule(context))");
      return;
    }

    if (module.id === "overlay") {
      imports.add("ru.bartwell.kick.module.overlay.OverlayModule");
      const performance = config.enablePerformanceProvider !== false;
      if (performance) {
        moduleLines.push("            module(OverlayModule(context))");
      } else {
        moduleLines.push("            module(OverlayModule(context = context, providers = emptyList()))");
      }
      return;
    }

    if (module.id === "runner") {
      imports.add("ru.bartwell.kick.module.runner.RunnerModule");
      moduleLines.push("            module(RunnerModule())");
      if (config.generateSampleCalls === true) {
        hasRunnerSamples = true;
        imports.add("ru.bartwell.kick.module.runner.runner");
        imports.add("ru.bartwell.kick.module.runner.core.renderer.JsonRunnerRenderer");
      }
      return;
    }

    if (module.id === "firebase_cloud_messaging") {
      imports.add("ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingModule");
      moduleLines.push("            module(FirebaseCloudMessagingModule(context))");
      return;
    }

    if (module.id === "firebase_analytics") {
      imports.add("ru.bartwell.kick.module.firebase.analytics.FirebaseAnalyticsModule");
      moduleLines.push("            module(FirebaseAnalyticsModule(context))");
    }
  });

  if (hasNapierBridge) {
    imports.add("io.github.aakira.napier.Antilog");
    imports.add("io.github.aakira.napier.Napier");
    imports.add("ru.bartwell.kick.module.logging.core.data.LogLevel");
    imports.add("ru.bartwell.kick.module.logging.log");
    imports.add("io.github.aakira.napier.LogLevel as NapierLogLevel");

    postInitLines.unshift("        installNapierBridge()");
    helperFunctions.push(
      "    private fun installNapierBridge() {",
      "        Napier.base(object : Antilog() {",
      "            override fun performLog(priority: NapierLogLevel, tag: String?, throwable: Throwable?, message: String?) {",
      "                val level = when (priority) {",
      "                    NapierLogLevel.VERBOSE -> LogLevel.VERBOSE",
      "                    NapierLogLevel.DEBUG -> LogLevel.DEBUG",
      "                    NapierLogLevel.INFO -> LogLevel.INFO",
      "                    NapierLogLevel.WARNING -> LogLevel.WARNING",
      "                    NapierLogLevel.ERROR -> LogLevel.ERROR",
      "                    NapierLogLevel.ASSERT -> LogLevel.ASSERT",
      "                }",
      "                Kick.log(level, message)",
      "            }",
      "        })",
      "    }"
    );
  }

  if (hasRunnerSamples) {
    postInitLines.push("        registerRunnerSamples()");
    helperFunctions.push(
      "    private fun registerRunnerSamples() {",
      "        Kick.runner.addCall(",
      "            title = \"Sample JSON\",",
      "            description = \"Generated by Kick Wizard\",",
      "            renderer = JsonRunnerRenderer(),",
      "        ) {",
      "            \"{\\\"status\\\":\\\"ok\\\",\\\"source\\\":\\\"kick-wizard\\\"}\"",
      "        }",
      "    }"
    );
  }

  if (hasPlatformBridge) {
    moduleLines.push("            installPlatformKickModules(context, deps)");
  }

  const dedupDepLines = unique(depLines);
  const sortedImports = Array.from(imports).sort();

  const code = [];
  code.push(sortedImports.map((entry) => `import ${entry}`).join("\n"));
  code.push("");

  if (dedupDepLines.length > 0) {
    code.push("data class KickDeps(");
    dedupDepLines.forEach((line) => code.push(line));
    code.push(")");
  } else {
    code.push("data class KickDeps()");
  }

  code.push("");
  code.push("object KickBootstrap {");
  code.push("    fun init(context: PlatformContext, deps: KickDeps) {");

  if (preInitLines.length > 0) {
    preInitLines.forEach((line) => code.push(line));
  }

  code.push("        Kick.init(context) {");
  code.push("            enableShortcut = true");

  if (moduleLines.length === 0) {
    code.push("            // Select at least one module in wizard.");
  } else {
    moduleLines.forEach((line) => code.push(line));
  }

  code.push("        }");

  if (postInitLines.length > 0) {
    postInitLines.forEach((line) => code.push(line));
  }

  code.push("    }");
  code.push("");
  code.push("    fun launch(context: PlatformContext) {");
  code.push("        Kick.launch(context)");
  code.push("    }");

  if (helperFunctions.length > 0) {
    code.push("");
    helperFunctions.forEach((line) => code.push(line));
  }

  code.push("}");

  if (hasPlatformBridge) {
    code.push("");
    code.push("internal expect fun Kick.Configuration.installPlatformKickModules(");
    code.push("    context: PlatformContext,");
    code.push("    deps: KickDeps,");
    code.push(")");
  }

  return code.join("\n");
}

function buildPlatformBridgeActualFile(platform, bridgeModules) {
  const imports = new Set([
    "ru.bartwell.kick.Kick",
    "ru.bartwell.kick.core.data.PlatformContext",
  ]);

  const bodyLines = [];

  bridgeModules.forEach((module) => {
    if (!isPlatformSupported(module, platform)) {
      return;
    }

    if (module.id === "room") {
      imports.add("ru.bartwell.kick.module.sqlite.runtime.SqliteModule");
      bodyLines.push("    deps.roomWrapper?.let { wrapper ->");
      bodyLines.push("        module(SqliteModule(wrapper))");
      bodyLines.push("    }");
      return;
    }

    if (module.id === "layout") {
      imports.add("ru.bartwell.kick.module.layout.LayoutModule");
      bodyLines.push("    module(LayoutModule(context))");
      return;
    }

    if (module.id === "firebase_cloud_messaging") {
      imports.add("ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingModule");
      bodyLines.push("    module(FirebaseCloudMessagingModule(context))");
      return;
    }

    if (module.id === "firebase_analytics") {
      imports.add("ru.bartwell.kick.module.firebase.analytics.FirebaseAnalyticsModule");
      bodyLines.push("    module(FirebaseAnalyticsModule(context))");
    }
  });

  if (bodyLines.length === 0) {
    bodyLines.push("    // No platform-specific Kick modules for this platform.");
  }

  const lines = [];
  lines.push(Array.from(imports).sort().map((entry) => `import ${entry}`).join("\n"));
  lines.push("");
  lines.push("internal actual fun Kick.Configuration.installPlatformKickModules(context: PlatformContext, deps: KickDeps) {");
  bodyLines.forEach((line) => lines.push(line));
  lines.push("}");

  const path = PLATFORM_FILE_PATHS[platform];
  const fileName = path.split("/").pop() || path;

  return {
    path,
    title: `${fileName} (${platform})`,
    code: lines.join("\n"),
  };
}

function buildPlatformBridgeFiles(state, selectedModules) {
  const bridgeModules = selectedModules.filter((module) => requiresPlatformBridgeModule(module, state.platforms));
  if (bridgeModules.length === 0) {
    return [];
  }

  const files = [];
  state.platforms
    .filter((platform) => PLATFORM_ORDER.includes(platform))
    .forEach((platform) => {
      files.push(buildPlatformBridgeActualFile(platform, bridgeModules));
    });

  return files;
}

function buildGlueGuideItems(state, selectedModules) {
  const selectedIds = new Set(selectedModules.map((module) => module.id));
  const items = [];

  if (selectedIds.has("logging")) {
    const loggingConfig = (state.moduleConfigs && state.moduleConfigs.logging) || {};
    if (loggingConfig.integrateNapier !== true) {
      items.push({
        type: "logging",
        integrateNapier: false,
      });
    }
  }

  if (selectedIds.has("ktor3")) {
    items.push({ type: "ktor3" });
  }

  if (selectedIds.has("firebase_cloud_messaging")) {
    items.push({
      type: "firebase_cloud_messaging",
      includeAndroid: state.platforms.includes("android"),
      includeIos: state.platforms.includes("ios"),
    });
  }

  if (selectedIds.has("firebase_analytics")) {
    items.push({ type: "firebase_analytics" });
  }

  if (selectedIds.has("control_panel")) {
    items.push({ type: "control_panel" });
  }

  if (selectedIds.has("overlay")) {
    items.push({ type: "overlay" });
  }

  if (selectedIds.has("runner")) {
    items.push({ type: "runner" });
  }

  return items;
}

export function shouldShowPlatformGlue(state, modulesById) {
  const selectedModules = getSelectedModules(state, modulesById);
  const files = buildPlatformBridgeFiles(state, selectedModules);
  const guideItems = buildGlueGuideItems(state, selectedModules);
  return files.length > 0 || guideItems.length > 0;
}

export function buildOutput(state, modulesById, kickVersion) {
  const selectedModules = getSelectedModules(state, modulesById);
  const glueFiles = buildPlatformBridgeFiles(state, selectedModules);
  const glueGuideItems = buildGlueGuideItems(state, selectedModules);

  return {
    gradle: buildGradleSnippet(selectedModules, kickVersion),
    commonKotlin: buildCommonSnippet(state, selectedModules, glueFiles.length > 0),
    glue: {
      required: glueFiles.length > 0 || glueGuideItems.length > 0,
      files: glueFiles,
      guideItems: glueGuideItems,
    },
  };
}

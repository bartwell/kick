import { I18n, DEFAULT_LANGUAGE } from "./app/i18n.js";
import {
  clearStateStorage,
  clearUrlState,
  createDefaultState,
  getLanguageFromUrl,
  hydrateState,
  loadStateFromStorage,
  loadStateFromUrl,
  saveStateToStorage,
  saveUrlState,
} from "./app/state.js";
import {
  ensureRouteAllowed,
  getAdjacentRoutes,
  getStepPosition,
  navigateTo,
  parseCurrentRoute,
} from "./app/router.js";
import { loadModules, loadVersion, mapModulesById } from "./app/data.js";
import { buildOutput, getUnsupportedPlatforms } from "./app/output.js";
import { copyText } from "./app/clipboard.js";

const SUPPORTED_LANGUAGES = ["en", "ru", "es", "pt", "zh-Hans", "ja"];
const PLATFORMS = ["android", "ios", "jvm", "wasm"];

const PLATFORM_TITLE_KEYS = {
  android: "platform.android",
  ios: "platform.ios",
  jvm: "platform.jvm",
  wasm: "platform.wasm",
};

const PLATFORM_DESCRIPTION_KEYS = {
  android: "platform.androidDescription",
  ios: "platform.iosDescription",
  jvm: "platform.jvmDescription",
  wasm: "platform.wasmDescription",
};

const CONTROL_PANEL_ITEM_TYPES = ["bool", "int", "string", "list", "button"];
const CONTROL_PANEL_EDITORS = ["none", "input_number", "input_string", "list"];

let modules = [];
let modulesById = {};
let kickVersion = "1.0.0";
let state = createDefaultState();
let outputCache = null;
let i18n = new I18n({ languages: SUPPORTED_LANGUAGES });
let toastTimer = null;

const appElement = document.getElementById("app");
const backButton = document.getElementById("back-button");
const nextButton = document.getElementById("next-button");
const skipModuleButton = document.getElementById("skip-module-button");
const resetButton = document.getElementById("reset-button");
const languageSelect = document.getElementById("language-select");
const progressElement = document.getElementById("step-progress");
const toastElement = document.getElementById("toast");

function t(key, params) {
  return i18n.t(key, params);
}

function escapeHtml(value) {
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/\"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function uniqueArray(values) {
  return values.filter((value, index) => values.indexOf(value) === index);
}

function getPlatformLabel(platformId) {
  return t(PLATFORM_TITLE_KEYS[platformId]);
}

function moduleHasConfigPages(moduleId) {
  const module = modulesById[moduleId];
  return Boolean(module && Array.isArray(module.configPages) && module.configPages.length > 0);
}

function syncModuleFlow() {
  state.moduleFlowIds = state.selectedModules.filter((moduleId) => moduleHasConfigPages(moduleId));
}

function getDefaultModuleConfig(moduleId) {
  switch (moduleId) {
    case "logging":
      return {
        integrateNapier: false,
        labelExtractor: "bracket",
      };
    case "control_panel":
      return {
        items: [],
      };
    case "multiplatform_settings":
      return {
        storages: [{ displayName: "Default" }],
      };
    case "overlay":
      return {
        enablePerformanceProvider: true,
      };
    case "runner":
      return {
        generateSampleCalls: false,
      };
    default:
      return {};
  }
}

function ensureModuleConfig(moduleId) {
  if (!state.moduleConfigs[moduleId] || typeof state.moduleConfigs[moduleId] !== "object") {
    state.moduleConfigs[moduleId] = getDefaultModuleConfig(moduleId);
    persistState();
  }
  return state.moduleConfigs[moduleId];
}

function sanitizeState() {
  const validModuleIds = new Set(modules.map((module) => module.id));
  state.platforms = uniqueArray(state.platforms.filter((platform) => PLATFORMS.includes(platform)));
  state.selectedModules = uniqueArray(state.selectedModules.filter((moduleId) => validModuleIds.has(moduleId)));

  Object.keys(state.moduleConfigs).forEach((moduleId) => {
    if (!validModuleIds.has(moduleId)) {
      delete state.moduleConfigs[moduleId];
    }
  });

  if (!SUPPORTED_LANGUAGES.includes(state.lang)) {
    state.lang = DEFAULT_LANGUAGE;
  }
}

function persistState() {
  saveStateToStorage(state);
  saveUrlState(state);
}

function showToast(message) {
  clearTimeout(toastTimer);
  toastElement.textContent = message;
  toastElement.classList.add("visible");
  toastTimer = setTimeout(() => {
    toastElement.classList.remove("visible");
  }, 1600);
}

function getUnsupportedPlatformIds(moduleDescription) {
  return getUnsupportedPlatforms(moduleDescription, state.platforms);
}

function getUnsupportedPlatformLabels(moduleDescription) {
  return getUnsupportedPlatformIds(moduleDescription).map((platform) => getPlatformLabel(platform));
}

function buildModuleWarning(moduleDescription) {
  const unsupportedLabels = getUnsupportedPlatformLabels(moduleDescription);
  if (unsupportedLabels.length === 0) {
    return "";
  }

  return `
    <div class="warning-box">
      <div>${escapeHtml(t("modules.unsupportedWarning", { platforms: unsupportedLabels.join(", ") }))}</div>
    </div>
  `;
}

function renderWelcomePage() {
  const selectedPlatforms = state.platforms.length;
  const selectedModules = state.selectedModules.length;

  return `
    <section>
      <h1 class="page-title">${escapeHtml(t("step.welcome.title"))}</h1>
      <p class="page-subtitle">${escapeHtml(t("step.welcome.subtitle"))}</p>

      <div class="panel" style="margin-top: 16px;">
        <h2 class="section-title">${escapeHtml(t("step.welcome.checklistTitle"))}</h2>
        <p class="section-subtitle">${escapeHtml(t("step.welcome.checklistSubtitle"))}</p>
        <ul>
          <li>${escapeHtml(t("step.welcome.itemLanguage"))}</li>
          <li>${escapeHtml(t("step.welcome.itemPlatforms"))}</li>
          <li>${escapeHtml(t("step.welcome.itemModules"))}</li>
          <li>${escapeHtml(t("step.welcome.itemOutput"))}</li>
        </ul>
      </div>

      <div class="card-grid">
        <article class="option-card">
          <h3>${escapeHtml(t("summary.platforms"))}</h3>
          <p>${escapeHtml(t("summary.count", { count: selectedPlatforms }))}</p>
        </article>
        <article class="option-card">
          <h3>${escapeHtml(t("summary.modules"))}</h3>
          <p>${escapeHtml(t("summary.count", { count: selectedModules }))}</p>
        </article>
        <article class="option-card">
          <h3>${escapeHtml(t("summary.version"))}</h3>
          <p>${escapeHtml(kickVersion)}</p>
        </article>
      </div>
    </section>
  `;
}

function renderPlatformsPage() {
  const cards = PLATFORMS.map((platform) => {
    const selected = state.platforms.includes(platform);
    return `
      <button type="button" class="option-card ${selected ? "selected" : ""}" data-action="toggle-platform" data-platform-id="${platform}">
        <div class="form-inline">
          <input type="checkbox" ${selected ? "checked" : ""} tabindex="-1" aria-hidden="true">
          <h3>${escapeHtml(t(PLATFORM_TITLE_KEYS[platform]))}</h3>
        </div>
        <p>${escapeHtml(t(PLATFORM_DESCRIPTION_KEYS[platform]))}</p>
      </button>
    `;
  }).join("\n");

  const warning = state.platforms.length === 0
    ? `<p class="warning-inline">${escapeHtml(t("validation.platformRequired"))}</p>`
    : "";

  return `
    <section>
      <h1 class="page-title">${escapeHtml(t("step.platforms.title"))}</h1>
      <p class="page-subtitle">${escapeHtml(t("step.platforms.subtitle"))}</p>
      ${warning}
      <div class="card-grid">${cards}</div>
    </section>
  `;
}

function renderModulesPage() {
  const cards = modules.map((moduleDescription) => {
    const selected = state.selectedModules.includes(moduleDescription.id);
    const warnings = buildModuleWarning(moduleDescription);

    const highBadge = moduleDescription.configComplexity === "high" && moduleDescription.id !== "control_panel"
      ? `<span class="badge high">${escapeHtml(t("badge.highConfiguration"))}</span>`
      : "";

    return `
      <button type="button" class="option-card ${selected ? "selected" : ""}" data-action="toggle-module" data-module-id="${moduleDescription.id}">
        <div class="form-inline">
          <input type="checkbox" ${selected ? "checked" : ""} tabindex="-1" aria-hidden="true">
          <h3>${escapeHtml(t(moduleDescription.titleKey))}</h3>
        </div>
        <p>${escapeHtml(t(moduleDescription.descriptionKey))}</p>
        <div class="badges">${highBadge}</div>
        ${warnings}
      </button>
    `;
  }).join("\n");

  const warning = state.selectedModules.length === 0
    ? `<p class="warning-inline">${escapeHtml(t("validation.moduleRequired"))}</p>`
    : "";

  return `
    <section>
      <h1 class="page-title">${escapeHtml(t("step.modules.title"))}</h1>
      <p class="page-subtitle">${escapeHtml(t("step.modules.subtitle"))}</p>
      <p class="helper-text">${escapeHtml(t("step.modules.selectedCount", { count: state.selectedModules.length }))}</p>
      ${warning}
      <div class="card-grid">${cards}</div>
    </section>
  `;
}

function renderLoggingConfig(config) {
  const integrateNapier = config.integrateNapier === true;
  const labelExtractor = config.labelExtractor === "custom" ? "custom" : "bracket";

  return `
    <div class="form-grid">
      <label class="form-inline">
        <input type="checkbox" ${integrateNapier ? "checked" : ""} data-action="set-logging-napier">
        <span>${escapeHtml(t("config.logging.integrateNapier"))}</span>
      </label>

      <details class="advanced">
        <summary>${escapeHtml(t("config.advanced"))}</summary>
        <div class="form-row" style="margin-top: 10px;">
          <label class="form-inline">
            <input type="radio" name="logging-label" value="bracket" ${labelExtractor === "bracket" ? "checked" : ""} data-action="set-logging-extractor">
            <span>${escapeHtml(t("config.logging.labelExtractorBracket"))}</span>
          </label>
          <label class="form-inline">
            <input type="radio" name="logging-label" value="custom" ${labelExtractor === "custom" ? "checked" : ""} data-action="set-logging-extractor">
            <span>${escapeHtml(t("config.logging.labelExtractorCustom"))}</span>
          </label>
        </div>
      </details>
    </div>
  `;
}

function renderSettingsConfig(config) {
  const storages = Array.isArray(config.storages) && config.storages.length > 0
    ? config.storages
    : [{ displayName: "Default" }];

  const rows = storages.map((storage, index) => `
    <article class="dynamic-item">
      <div class="dynamic-item-head">
        <div class="dynamic-item-title">${escapeHtml(t("config.storageItem", { index: index + 1 }))}</div>
        <button class="remove-link" type="button" data-action="remove-storage" data-index="${index}">${escapeHtml(t("actions.remove"))}</button>
      </div>
      <div class="form-row">
        <label>${escapeHtml(t("config.displayName"))}</label>
        <input type="text" value="${escapeHtml(storage.displayName || "")}" data-action="set-storage-name" data-index="${index}">
      </div>
    </article>
  `).join("\n");

  return `
    <div class="form-grid">
      <button type="button" class="ghost-button" data-action="add-storage">${escapeHtml(t("actions.addStorage"))}</button>
      <div class="dynamic-list">${rows}</div>
    </div>
  `;
}

function renderOverlayConfig(config) {
  const enablePerformanceProvider = config.enablePerformanceProvider !== false;

  return `
    <div class="form-grid">
      <label class="form-inline">
        <input type="checkbox" ${enablePerformanceProvider ? "checked" : ""} data-action="set-overlay-performance">
        <span>${escapeHtml(t("config.overlay.enablePerformanceProvider"))}</span>
      </label>
    </div>
  `;
}

function renderRunnerConfig(config) {
  const generateSampleCalls = config.generateSampleCalls === true;

  return `
    <div class="form-grid">
      <label class="form-inline">
        <input type="checkbox" ${generateSampleCalls ? "checked" : ""} data-action="set-runner-samples">
        <span>${escapeHtml(t("config.runner.generateSampleCalls"))}</span>
      </label>
    </div>
  `;
}

function renderControlPanelConfig(config) {
  const items = Array.isArray(config.items) ? config.items : [];

  const rows = items.map((item, index) => {
    const typeValue = CONTROL_PANEL_ITEM_TYPES.includes(item.type) ? item.type : "string";
    const editorValue = CONTROL_PANEL_EDITORS.includes(item.editor) ? item.editor : "none";
    const listVisible = editorValue === "list" ? "" : "hidden";

    const typeOptions = CONTROL_PANEL_ITEM_TYPES.map((option) => `
      <option value="${option}" ${typeValue === option ? "selected" : ""}>${escapeHtml(t(`config.controlPanel.type.${option}`))}</option>
    `).join("");

    const editorOptions = CONTROL_PANEL_EDITORS.map((option) => `
      <option value="${option}" ${editorValue === option ? "selected" : ""}>${escapeHtml(t(`config.controlPanel.editor.${option}`))}</option>
    `).join("");

    return `
      <article class="dynamic-item">
        <div class="dynamic-item-head">
          <div class="dynamic-item-title">${escapeHtml(t("config.controlPanel.item", { index: index + 1 }))}</div>
          <button class="remove-link" type="button" data-action="remove-control-item" data-index="${index}">${escapeHtml(t("actions.remove"))}</button>
        </div>

        <div class="form-row">
          <label>${escapeHtml(t("config.controlPanel.name"))}</label>
          <input type="text" value="${escapeHtml(item.name || "")}" data-action="set-control-name" data-index="${index}">
        </div>

        <div class="form-row">
          <label>${escapeHtml(t("config.controlPanel.typeLabel"))}</label>
          <select data-action="set-control-type" data-index="${index}">${typeOptions}</select>
        </div>

        <div class="form-row">
          <label>${escapeHtml(t("config.controlPanel.category"))}</label>
          <input type="text" value="${escapeHtml(item.category || "")}" data-action="set-control-category" data-index="${index}">
        </div>

        <details class="advanced">
          <summary>${escapeHtml(t("config.advanced"))}</summary>
          <div class="form-row" style="margin-top: 10px;">
            <label>${escapeHtml(t("config.controlPanel.editorLabel"))}</label>
            <select data-action="set-control-editor" data-index="${index}">${editorOptions}</select>
          </div>

          <div class="form-row ${listVisible}" data-control-list-row="${index}">
            <label>${escapeHtml(t("config.controlPanel.listValues"))}</label>
            <input type="text" value="${escapeHtml(item.listValues || "")}" data-action="set-control-list-values" data-index="${index}">
          </div>
        </details>
      </article>
    `;
  }).join("\n");

  return `
    <div class="form-grid">
      <div class="form-inline">
        <button class="ghost-button" type="button" data-action="add-control-item">${escapeHtml(t("actions.addItem"))}</button>
        <button class="ghost-button" type="button" data-action="add-control-examples">${escapeHtml(t("actions.addExampleItems"))}</button>
      </div>
      <div class="dynamic-list">${rows}</div>
    </div>
  `;
}

function renderGenericConfig() {
  return `
    <div class="panel" style="margin-top: 14px;">
      <p class="helper-text">${escapeHtml(t("config.noQuestions"))}</p>
    </div>
  `;
}

function renderModuleConfigPage(moduleId) {
  const moduleDescription = modulesById[moduleId];
  if (!moduleDescription || !moduleHasConfigPages(moduleId)) {
    return renderModulesPage();
  }

  const config = ensureModuleConfig(moduleId);
  const highBadge = moduleDescription.configComplexity === "high" && moduleDescription.id !== "control_panel"
    ? `<span class="badge high">${escapeHtml(t("badge.highConfiguration"))}</span>`
    : "";

  let form = renderGenericConfig();

  switch (moduleId) {
    case "logging":
      form = renderLoggingConfig(config);
      break;
    case "control_panel":
      form = renderControlPanelConfig(config);
      break;
    case "multiplatform_settings":
      form = renderSettingsConfig(config);
      break;
    case "overlay":
      form = renderOverlayConfig(config);
      break;
    case "runner":
      form = renderRunnerConfig(config);
      break;
    default:
      form = renderGenericConfig();
      break;
  }

  return `
    <section>
      <h1 class="page-title">${escapeHtml(t("step.moduleConfig.title", { module: t(moduleDescription.titleKey) }))}</h1>
      <p class="page-subtitle">${escapeHtml(t(moduleDescription.descriptionKey))}</p>
      <div class="badges" style="margin-top: 8px;">${highBadge}</div>
      ${buildModuleWarning(moduleDescription)}
      ${form}
    </section>
  `;
}

function getGlueGuideText(item) {
  if (item.type === "logging") {
    return item.integrateNapier === true
      ? t("glue.guide.loggingWithNapier")
      : t("glue.guide.logging");
  }
  if (item.type === "ktor3") {
    return t("glue.guide.ktor3");
  }
  if (item.type === "firebase_cloud_messaging") {
    if (item.includeAndroid && item.includeIos) {
      return t("glue.guide.firebaseCloudMessagingBoth");
    }
    if (item.includeAndroid) {
      return t("glue.guide.firebaseCloudMessagingAndroid");
    }
    if (item.includeIos) {
      return t("glue.guide.firebaseCloudMessagingIos");
    }
    return t("glue.guide.firebaseCloudMessagingGeneric");
  }
  if (item.type === "firebase_analytics") {
    return t("glue.guide.firebaseAnalytics");
  }
  if (item.type === "control_panel") {
    return t("glue.guide.controlPanel");
  }
  if (item.type === "overlay") {
    return t("glue.guide.overlay");
  }
  if (item.type === "runner") {
    return t("glue.guide.runner");
  }
  return "";
}

function buildKtor3ExampleSnippet() {
  return [
    "val httpClient = HttpClient {",
    "    install(KickKtor3Plugin) {",
    "        maxBodySizeBytes = 1024 * 1024L",
    "    }",
    "}",
  ].join("\n");
}

function buildFirebaseCloudMessagingExampleSnippet(item) {
  const parts = [];

  if (item.includeAndroid) {
    parts.push(
      [
        "// Android",
        "class MyMessagingService : FirebaseMessagingService() {",
        "    override fun onMessageReceived(message: RemoteMessage) {",
        "        // your app logic...",
        "        Kick.firebaseCloudMessaging.handleFcm(message)",
        "    }",
        "}",
      ].join("\n")
    );
  }

  if (item.includeIos) {
    parts.push(
      [
        "// Shared Kotlin bridge for iOS push callbacks",
        "object IosPushBridge {",
        "    fun onApnsPayload(userInfo: Map<Any?, *>) {",
        "        Kick.firebaseCloudMessaging.handleApnsPayload(userInfo)",
        "    }",
        "}",
      ].join("\n")
    );
  }

  if (parts.length === 0) {
    return [
      "object PushBridge {",
      "    fun onPushPayload(payload: Map<Any?, *>) {",
      "        Kick.firebaseCloudMessaging.handleApnsPayload(payload)",
      "    }",
      "}",
    ].join("\n");
  }

  return parts.join("\n\n");
}

function buildFirebaseAnalyticsExampleSnippet() {
  return [
    "class AnalyticsReporter(",
    "    private val firebaseAnalytics: FirebaseAnalytics,",
    ") {",
    "    fun logEvent(name: String, params: Bundle?) {",
    "        firebaseAnalytics.logEvent(name, params)",
    "        Kick.firebaseAnalytics.logEvent(name, params)",
    "    }",
    "",
    "    fun setUserId(id: String?) {",
    "        firebaseAnalytics.setUserId(id)",
    "        Kick.firebaseAnalytics.setUserId(id)",
    "    }",
    "",
    "    fun setUserProperty(name: String, value: String) {",
    "        firebaseAnalytics.setUserProperty(name, value)",
    "        Kick.firebaseAnalytics.setUserProperty(name, value)",
    "    }",
    "}",
  ].join("\n");
}

function buildControlPanelExampleSnippet() {
  return [
    "if (Kick.controlPanel.getBoolean(\"enableSomeRequest\")) {",
    "    makeRequest(Kick.controlPanel.getString(\"someRequestUrl\"))",
    "}",
  ].join("\n");
}

function buildOverlayExampleSnippet() {
  return [
    "Kick.overlay.set(\"fps\", 58)",
    "Kick.overlay.set(\"isWsConnected\", true)",
    "Kick.overlay.set(\"requestsInFlight\", 3, \"Network\")",
    "Kick.overlay.set(\"screen\", \"Home\", \"UI\")",
  ].join("\n");
}

function buildRunnerExampleSnippet() {
  return [
    "Kick.runner.addCall(",
    "    title = \"JSON sample\",",
    "    description = \"Pretty-printed JSON output\",",
    "    renderer = JsonRunnerRenderer(),",
    ") {",
    "    \"\"\"{\\\"message\\\":\\\"Hello, Runner!\\\",\\\"timestamp\\\":${DateUtils.currentTimeMillis()}}\"\"\"",
    "}",
  ].join("\n");
}

function buildGuideExample(item) {
  if (item.type === "ktor3") {
    return {
      titleKey: "output.steps.ktorExampleTitle",
      descriptionKey: "output.steps.ktorExampleDescription",
      code: buildKtor3ExampleSnippet(),
    };
  }

  if (item.type === "firebase_cloud_messaging") {
    return {
      titleKey: "output.steps.fcmExampleTitle",
      descriptionKey: "output.steps.fcmExampleDescription",
      code: buildFirebaseCloudMessagingExampleSnippet(item),
    };
  }

  if (item.type === "firebase_analytics") {
    return {
      titleKey: "output.steps.analyticsExampleTitle",
      descriptionKey: "output.steps.analyticsExampleDescription",
      code: buildFirebaseAnalyticsExampleSnippet(),
    };
  }

  if (item.type === "control_panel") {
    return {
      titleKey: "output.steps.controlPanelExampleTitle",
      descriptionKey: "output.steps.controlPanelExampleDescription",
      code: buildControlPanelExampleSnippet(),
    };
  }

  if (item.type === "overlay") {
    return {
      titleKey: "output.steps.overlayExampleTitle",
      descriptionKey: "output.steps.overlayExampleDescription",
      code: buildOverlayExampleSnippet(),
    };
  }

  if (item.type === "runner") {
    return {
      titleKey: "output.steps.runnerExampleTitle",
      descriptionKey: "output.steps.runnerExampleDescription",
      code: buildRunnerExampleSnippet(),
    };
  }

  return null;
}

function renderOutputCodeBlock({ title, description, copyKey, code }) {
  const descriptionLine = description
    ? `<p>${escapeHtml(description)}</p>`
    : "";

  return `
    <section class="output-block">
      <div class="output-head">
        <div>
          <h3>${escapeHtml(title)}</h3>
          ${descriptionLine}
        </div>
        <button class="copy-button" type="button" data-action="copy-block" data-copy-key="${escapeHtml(copyKey)}">${escapeHtml(t("actions.copy"))}</button>
      </div>
      <pre class="code-view"><code>${escapeHtml(code)}</code></pre>
    </section>
  `;
}

function renderOutputPage() {
  outputCache = buildOutput(state, modulesById, kickVersion);
  const copyMap = {
    gradle: outputCache.gradle,
    common: outputCache.commonKotlin,
  };
  const steps = [];

  steps.push(`
    <li class="output-step-item">
      <p class="output-step-title">${escapeHtml(t("output.steps.gradle"))}</p>
      ${renderOutputCodeBlock({
    title: t("output.gradle.title"),
    description: t("output.gradle.description"),
    copyKey: "gradle",
    code: outputCache.gradle,
  })}
    </li>
  `);

  const commonPath = "shared/src/commonMain/kotlin/KickBootstrap.kt";
  steps.push(`
    <li class="output-step-item">
      <p class="output-step-title">${escapeHtml(t("output.steps.file", { path: commonPath }))}</p>
      ${renderOutputCodeBlock({
    title: t("output.common.title"),
    description: t("output.common.description"),
    copyKey: "common",
    code: outputCache.commonKotlin,
  })}
    </li>
  `);

  outputCache.glue.files.forEach((file, index) => {
    const key = `glue-file-${index}`;
    copyMap[key] = file.code;
    steps.push(`
      <li class="output-step-item">
        <p class="output-step-title">${escapeHtml(t("output.steps.file", { path: file.path }))}</p>
        ${renderOutputCodeBlock({
      title: file.title,
      description: `${t("output.platformGlue.pathLabel")} ${file.path}`,
      copyKey: key,
      code: file.code,
    })}
      </li>
    `);
  });

  (outputCache.glue.guideItems || []).forEach((item, index) => {
    const text = getGlueGuideText(item);
    if (!text) {
      return;
    }

    let extraCodeBlock = "";
    const example = buildGuideExample(item);
    if (example) {
      const key = `guide-${item.type}-${index}`;
      const snippet = example.code;
      copyMap[key] = snippet;
      extraCodeBlock = renderOutputCodeBlock({
        title: t(example.titleKey),
        description: t(example.descriptionKey),
        copyKey: key,
        code: snippet,
      });
    }

    steps.push(`
      <li class="output-step-item">
        <p class="output-step-note">${escapeHtml(text)}</p>
        ${extraCodeBlock}
      </li>
    `);
  });

  outputCache.copyMap = copyMap;

  return `
    <section>
      <h1 class="page-title">${escapeHtml(t("step.output.title"))}</h1>
      <p class="page-subtitle">${escapeHtml(t("step.output.subtitle"))}</p>
      <ol class="output-steps">
        ${steps.join("\n")}
      </ol>
    </section>
  `;
}

function renderPage(routeInfo) {
  if (routeInfo.route === "/") {
    return renderWelcomePage();
  }
  if (routeInfo.route === "/platforms") {
    return renderPlatformsPage();
  }
  if (routeInfo.route === "/modules") {
    return renderModulesPage();
  }
  if (routeInfo.route === "/output") {
    return renderOutputPage();
  }
  if (routeInfo.type === "module") {
    return renderModuleConfigPage(routeInfo.moduleId);
  }
  return renderWelcomePage();
}

function updateFooter(routeInfo) {
  const safeRoute = ensureRouteAllowed(routeInfo, state);
  const { index, total } = getStepPosition(safeRoute, state);
  const adjacent = getAdjacentRoutes(safeRoute, state);
  const canSkipModule = routeInfo.type === "module" && safeRoute === routeInfo.route;

  progressElement.textContent = t("progress.step", {
    current: index + 1,
    total,
  });

  skipModuleButton.textContent = t("actions.skipModule");
  skipModuleButton.classList.toggle("hidden", !canSkipModule);
  skipModuleButton.disabled = !canSkipModule;

  backButton.disabled = !adjacent.prev;
  nextButton.disabled = safeRoute !== "/output" && !adjacent.next;

  if (!adjacent.next) {
    nextButton.textContent = t("nav.done");
  } else if (safeRoute === "/modules") {
    nextButton.textContent = t("nav.nextModule");
  } else {
    nextButton.textContent = t("nav.next");
  }
}

function validateBeforeNext(routeInfo) {
  if (routeInfo.route === "/platforms" && state.platforms.length === 0) {
    showToast(t("validation.platformRequired"));
    return false;
  }

  if (routeInfo.route === "/modules" && state.selectedModules.length === 0) {
    showToast(t("validation.moduleRequired"));
    return false;
  }

  return true;
}

function renderLanguageOptions() {
  languageSelect.innerHTML = SUPPORTED_LANGUAGES
    .map((lang) => `<option value="${lang}">${escapeHtml(t(`language.${lang}`))}</option>`)
    .join("");
  languageSelect.value = state.lang;
}

function render() {
  syncModuleFlow();
  const parsedRoute = parseCurrentRoute();
  const safeRoute = ensureRouteAllowed(parsedRoute, state);

  if (safeRoute !== parsedRoute.route) {
    navigateTo(safeRoute, { replace: true });
    return;
  }

  document.documentElement.lang = state.lang;
  document.title = t("app.pageTitle");

  renderLanguageOptions();
  i18n.applyToDocument(document.body);

  appElement.innerHTML = renderPage(parsedRoute);
  updateFooter(parsedRoute);
  persistState();
}

function togglePlatform(platformId) {
  if (!PLATFORMS.includes(platformId)) {
    return;
  }
  if (state.platforms.includes(platformId)) {
    state.platforms = state.platforms.filter((entry) => entry !== platformId);
  } else {
    state.platforms = [...state.platforms, platformId];
  }
  render();
}

function toggleModule(moduleId) {
  if (!modulesById[moduleId]) {
    return;
  }
  const selected = state.selectedModules.includes(moduleId);
  if (selected) {
    state.selectedModules = state.selectedModules.filter((entry) => entry !== moduleId);
  } else {
    state.selectedModules = [...state.selectedModules, moduleId];
    if (moduleHasConfigPages(moduleId)) {
      ensureModuleConfig(moduleId);
    }
  }
  render();
}

function skipCurrentModule() {
  syncModuleFlow();
  const routeInfo = parseCurrentRoute();
  const safeRoute = ensureRouteAllowed(routeInfo, state);
  if (routeInfo.type !== "module" || safeRoute !== routeInfo.route || !routeInfo.moduleId) {
    return;
  }

  const adjacent = getAdjacentRoutes(safeRoute, state);
  const nextRoute = adjacent.next || "/output";
  state.selectedModules = state.selectedModules.filter((entry) => entry !== routeInfo.moduleId);
  syncModuleFlow();
  persistState();
  navigateTo(nextRoute);
}

function mutateModuleConfig(moduleId, mutator, rerender = false) {
  const config = ensureModuleConfig(moduleId);
  mutator(config);
  persistState();
  if (rerender) {
    render();
  }
}

function addStorage() {
  mutateModuleConfig("multiplatform_settings", (config) => {
    const storages = Array.isArray(config.storages) ? config.storages : [];
    storages.push({ displayName: `${t("config.storageDefaultName")} ${storages.length + 1}` });
    config.storages = storages;
  }, true);
}

function removeStorage(index) {
  mutateModuleConfig("multiplatform_settings", (config) => {
    const storages = Array.isArray(config.storages) ? config.storages : [];
    storages.splice(index, 1);
    if (storages.length === 0) {
      storages.push({ displayName: "Default" });
    }
    config.storages = storages;
  }, true);
}

function setStorageName(index, value) {
  mutateModuleConfig("multiplatform_settings", (config) => {
    const storages = Array.isArray(config.storages) ? config.storages : [];
    if (!storages[index]) {
      storages[index] = { displayName: "" };
    }
    storages[index].displayName = value;
    config.storages = storages;
  });
}

function addControlItem() {
  mutateModuleConfig("control_panel", (config) => {
    const items = Array.isArray(config.items) ? config.items : [];
    items.push({
      name: "",
      type: "string",
      category: "",
      editor: "none",
      listValues: "",
    });
    config.items = items;
  }, true);
}

function addControlExamples() {
  mutateModuleConfig("control_panel", (config) => {
    config.items = [
      {
        name: "featureEnabled",
        type: "bool",
        category: "General",
        editor: "none",
        listValues: "",
      },
      {
        name: "maxItems",
        type: "int",
        category: "General",
        editor: "input_number",
        listValues: "",
      },
      {
        name: "endpoint",
        type: "string",
        category: "Network",
        editor: "input_string",
        listValues: "",
      },
      {
        name: "environment",
        type: "list",
        category: "Network",
        editor: "list",
        listValues: "dev, stage, prod",
      },
      {
        name: "refresh_cache",
        type: "button",
        category: "Actions",
        editor: "none",
        listValues: "",
      },
    ];
  }, true);
}

function removeControlItem(index) {
  mutateModuleConfig("control_panel", (config) => {
    const items = Array.isArray(config.items) ? config.items : [];
    items.splice(index, 1);
    config.items = items;
  }, true);
}

function setControlItemField(index, key, value, rerender = false) {
  mutateModuleConfig("control_panel", (config) => {
    const items = Array.isArray(config.items) ? config.items : [];
    if (!items[index]) {
      items[index] = {
        name: "",
        type: "string",
        category: "",
        editor: "none",
        listValues: "",
      };
    }
    items[index][key] = value;
    config.items = items;
  }, rerender);
}

async function onLanguageChange(event) {
  const lang = event.target.value;
  if (!SUPPORTED_LANGUAGES.includes(lang)) {
    return;
  }
  state.lang = lang;
  await i18n.loadLanguage(lang);
  render();
}

function onAppClick(event) {
  const actionNode = event.target.closest("[data-action]");
  if (!actionNode) {
    return;
  }

  const action = actionNode.dataset.action;

  if (action === "toggle-platform") {
    togglePlatform(actionNode.dataset.platformId);
  }

  if (action === "toggle-module") {
    toggleModule(actionNode.dataset.moduleId);
  }

  if (action === "add-storage") {
    addStorage();
  }

  if (action === "remove-storage") {
    removeStorage(Number(actionNode.dataset.index));
  }

  if (action === "add-control-item") {
    addControlItem();
  }

  if (action === "add-control-examples") {
    addControlExamples();
  }

  if (action === "remove-control-item") {
    removeControlItem(Number(actionNode.dataset.index));
  }

  if (action === "copy-block") {
    const key = actionNode.dataset.copyKey;
    const map = outputCache?.copyMap || {};
    const value = map[key] || "";
    if (value) {
      copyText(value)
        .then(() => showToast(t("toast.copied")))
        .catch(() => showToast(t("toast.copyFailed")));
    }
  }
}

function onAppInput(event) {
  const action = event.target.dataset.action;

  if (action === "set-storage-name") {
    setStorageName(Number(event.target.dataset.index), event.target.value);
  }

  if (action === "set-control-name") {
    setControlItemField(Number(event.target.dataset.index), "name", event.target.value);
  }

  if (action === "set-control-category") {
    setControlItemField(Number(event.target.dataset.index), "category", event.target.value);
  }

  if (action === "set-control-list-values") {
    setControlItemField(Number(event.target.dataset.index), "listValues", event.target.value);
  }
}

function onAppChange(event) {
  const action = event.target.dataset.action;

  if (action === "set-logging-napier") {
    mutateModuleConfig("logging", (config) => {
      config.integrateNapier = event.target.checked;
    });
  }

  if (action === "set-logging-extractor") {
    mutateModuleConfig("logging", (config) => {
      config.labelExtractor = event.target.value;
    });
  }

  if (action === "set-overlay-performance") {
    mutateModuleConfig("overlay", (config) => {
      config.enablePerformanceProvider = event.target.checked;
    });
  }

  if (action === "set-runner-samples") {
    mutateModuleConfig("runner", (config) => {
      config.generateSampleCalls = event.target.checked;
    });
  }

  if (action === "set-control-type") {
    setControlItemField(Number(event.target.dataset.index), "type", event.target.value, true);
  }

  if (action === "set-control-editor") {
    setControlItemField(Number(event.target.dataset.index), "editor", event.target.value, true);
  }
}

function goBack() {
  syncModuleFlow();
  const routeInfo = parseCurrentRoute();
  const safeRoute = ensureRouteAllowed(routeInfo, state);
  const adjacent = getAdjacentRoutes(safeRoute, state);
  if (adjacent.prev) {
    navigateTo(adjacent.prev);
  }
}

function goNext() {
  syncModuleFlow();
  const routeInfo = parseCurrentRoute();
  const safeRoute = ensureRouteAllowed(routeInfo, state);
  if (!validateBeforeNext({ ...routeInfo, route: safeRoute })) {
    return;
  }
  const adjacent = getAdjacentRoutes(safeRoute, state);
  if (adjacent.next) {
    navigateTo(adjacent.next);
    return;
  }
  if (safeRoute === "/output") {
    navigateTo("/");
  }
}

function resetWizard() {
  const allowReset = window.confirm(t("nav.resetConfirm"));
  if (!allowReset) {
    return;
  }

  const lang = state.lang;
  state = createDefaultState();
  state.lang = lang;
  clearStateStorage();
  clearUrlState();
  persistState();
  navigateTo("/", { replace: true });
  render();
}

async function initialize() {
  try {
    const [loadedModules, loadedVersion] = await Promise.all([loadModules(), loadVersion()]);
    modules = loadedModules;
    modulesById = mapModulesById(modules);
    kickVersion = loadedVersion;

    const storageState = loadStateFromStorage();
    const urlState = loadStateFromUrl();
    const urlLanguage = getLanguageFromUrl();

    state = hydrateState({
      ...createDefaultState(),
      ...(storageState || {}),
      ...(urlState || {}),
    });

    if (urlLanguage) {
      state.lang = urlLanguage;
    }

    sanitizeState();

    await i18n.loadLanguage(DEFAULT_LANGUAGE);
    if (state.lang !== DEFAULT_LANGUAGE) {
      await i18n.loadLanguage(state.lang);
    }

    if (!window.location.hash) {
      navigateTo("/", { replace: true });
    }

    render();
  } catch (error) {
    console.error(error);
    appElement.innerHTML = `<p class="warning-inline">${escapeHtml(t("errors.loadFailed"))}</p>`;
  }
}

window.addEventListener("hashchange", render);
backButton.addEventListener("click", goBack);
nextButton.addEventListener("click", goNext);
skipModuleButton.addEventListener("click", skipCurrentModule);
resetButton.addEventListener("click", resetWizard);
languageSelect.addEventListener("change", onLanguageChange);
appElement.addEventListener("click", onAppClick);
appElement.addEventListener("input", onAppInput);
appElement.addEventListener("change", onAppChange);

initialize();

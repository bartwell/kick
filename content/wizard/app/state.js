export const STORAGE_KEY = "kick.wizard.state.v1";
export const URL_STATE_KEY = "state";

const EMPTY_OBJECT = Object.freeze({});

function clone(value) {
  return JSON.parse(JSON.stringify(value));
}

function safeArray(value) {
  return Array.isArray(value) ? value.filter((entry) => typeof entry === "string") : [];
}

function toUrlSafeBase64(input) {
  const raw = btoa(unescape(encodeURIComponent(input)));
  return raw.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "");
}

function fromUrlSafeBase64(input) {
  const normalized = input.replace(/-/g, "+").replace(/_/g, "/");
  const padding = normalized.length % 4 === 0 ? "" : "=".repeat(4 - (normalized.length % 4));
  const decoded = atob(`${normalized}${padding}`);
  return decodeURIComponent(escape(decoded));
}

export function createDefaultState() {
  return {
    lang: "en",
    platforms: [],
    selectedModules: [],
    moduleConfigs: {},
  };
}

export function hydrateState(rawState = EMPTY_OBJECT) {
  const base = createDefaultState();
  const state = {
    ...base,
    ...rawState,
    platforms: safeArray(rawState.platforms),
    selectedModules: safeArray(rawState.selectedModules),
    moduleConfigs: rawState.moduleConfigs && typeof rawState.moduleConfigs === "object"
      ? clone(rawState.moduleConfigs)
      : {},
  };
  if (typeof rawState.lang === "string") {
    state.lang = rawState.lang;
  }
  return state;
}

export function loadStateFromStorage() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    const parsed = JSON.parse(raw);
    return hydrateState(parsed);
  } catch (_error) {
    return null;
  }
}

export function saveStateToStorage(state) {
  const payload = {
    lang: state.lang,
    platforms: state.platforms,
    selectedModules: state.selectedModules,
    moduleConfigs: state.moduleConfigs,
  };
  localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
}

export function clearStateStorage() {
  localStorage.removeItem(STORAGE_KEY);
}

export function loadStateFromUrl() {
  const url = new URL(window.location.href);
  const encodedState = url.searchParams.get(URL_STATE_KEY);
  if (!encodedState) {
    return null;
  }
  try {
    const json = fromUrlSafeBase64(encodedState);
    const parsed = JSON.parse(json);
    return hydrateState(parsed);
  } catch (_error) {
    return null;
  }
}

export function getLanguageFromUrl() {
  const url = new URL(window.location.href);
  return url.searchParams.get("lang");
}

function buildShareState(state) {
  return {
    platforms: state.platforms,
    selectedModules: state.selectedModules,
    moduleConfigs: state.moduleConfigs,
  };
}

function isShareStateEmpty(state) {
  return state.platforms.length === 0 && state.selectedModules.length === 0;
}

export function saveUrlState(state) {
  const url = new URL(window.location.href);
  const shareState = buildShareState(state);

  if (isShareStateEmpty(shareState)) {
    url.searchParams.delete(URL_STATE_KEY);
  } else {
    url.searchParams.set(URL_STATE_KEY, toUrlSafeBase64(JSON.stringify(shareState)));
  }

  url.searchParams.set("lang", state.lang);
  window.history.replaceState({}, "", url.toString());
}

export function clearUrlState() {
  const url = new URL(window.location.href);
  url.searchParams.delete(URL_STATE_KEY);
  window.history.replaceState({}, "", url.toString());
}

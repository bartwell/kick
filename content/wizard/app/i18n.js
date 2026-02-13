const FALLBACK_LANGUAGE = "en";

function deepGet(source, key) {
  return key.split(".").reduce((acc, segment) => {
    if (acc && Object.prototype.hasOwnProperty.call(acc, segment)) {
      return acc[segment];
    }
    return undefined;
  }, source);
}

function applyParams(template, params = {}) {
  return template.replace(/\{(\w+)\}/g, (_, key) => {
    if (Object.prototype.hasOwnProperty.call(params, key)) {
      return String(params[key]);
    }
    return `{${key}}`;
  });
}

export class I18n {
  constructor(options) {
    this.languages = options.languages;
    this.translations = {};
    this.currentLanguage = FALLBACK_LANGUAGE;
  }

  async loadLanguage(language) {
    const safeLanguage = this.languages.includes(language) ? language : FALLBACK_LANGUAGE;
    if (!this.translations[safeLanguage]) {
      const response = await fetch(`./i18n/${safeLanguage}.json`);
      if (!response.ok) {
        throw new Error(`Failed to load locale: ${safeLanguage}`);
      }
      this.translations[safeLanguage] = await response.json();
    }
    this.currentLanguage = safeLanguage;
  }

  t(key, params = {}) {
    const current = this.translations[this.currentLanguage] || {};
    const fallback = this.translations[FALLBACK_LANGUAGE] || {};
    const value = deepGet(current, key) ?? deepGet(fallback, key) ?? key;
    if (typeof value !== "string") {
      return key;
    }
    return applyParams(value, params);
  }

  applyToDocument(root = document) {
    root.querySelectorAll("[data-i18n]").forEach((element) => {
      const key = element.getAttribute("data-i18n");
      if (!key) {
        return;
      }
      element.textContent = this.t(key);
    });

    root.querySelectorAll("[data-i18n-title]").forEach((element) => {
      const key = element.getAttribute("data-i18n-title");
      if (!key) {
        return;
      }
      element.title = this.t(key);
    });

    root.querySelectorAll("[data-i18n-placeholder]").forEach((element) => {
      const key = element.getAttribute("data-i18n-placeholder");
      if (!key) {
        return;
      }
      element.placeholder = this.t(key);
    });

    root.querySelectorAll("[data-i18n-aria-label]").forEach((element) => {
      const key = element.getAttribute("data-i18n-aria-label");
      if (!key) {
        return;
      }
      element.setAttribute("aria-label", this.t(key));
    });
  }
}

export const DEFAULT_LANGUAGE = FALLBACK_LANGUAGE;

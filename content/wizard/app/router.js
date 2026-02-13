const ROOT_ROUTE = "/";
const PLATFORMS_ROUTE = "/platforms";
const MODULES_ROUTE = "/modules";
const OUTPUT_ROUTE = "/output";
const MODULE_PREFIX = "/module/";

export function getRoutes() {
  return {
    root: ROOT_ROUTE,
    platforms: PLATFORMS_ROUTE,
    modules: MODULES_ROUTE,
    output: OUTPUT_ROUTE,
    modulePrefix: MODULE_PREFIX,
  };
}

export function toModuleRoute(moduleId) {
  return `${MODULE_PREFIX}${moduleId}`;
}

export function parseCurrentRoute() {
  const hash = window.location.hash.replace(/^#/, "") || ROOT_ROUTE;
  const normalized = hash.startsWith("/") ? hash : `/${hash}`;
  if (normalized.startsWith(MODULE_PREFIX)) {
    const moduleId = normalized.slice(MODULE_PREFIX.length).trim();
    return {
      route: toModuleRoute(moduleId),
      type: "module",
      moduleId,
    };
  }

  if ([ROOT_ROUTE, PLATFORMS_ROUTE, MODULES_ROUTE, OUTPUT_ROUTE].includes(normalized)) {
    return {
      route: normalized,
      type: "static",
      moduleId: null,
    };
  }

  return {
    route: ROOT_ROUTE,
    type: "static",
    moduleId: null,
  };
}

export function getWizardSteps(state) {
  const moduleFlowIds = Array.isArray(state.moduleFlowIds)
    ? state.moduleFlowIds
    : state.selectedModules;
  const moduleRoutes = moduleFlowIds.map((moduleId) => toModuleRoute(moduleId));
  return [ROOT_ROUTE, PLATFORMS_ROUTE, MODULES_ROUTE, ...moduleRoutes, OUTPUT_ROUTE];
}

export function getStepPosition(route, state) {
  const steps = getWizardSteps(state);
  const index = steps.indexOf(route);
  if (index < 0) {
    return {
      index: 0,
      total: steps.length,
      steps,
    };
  }
  return {
    index,
    total: steps.length,
    steps,
  };
}

export function getAdjacentRoutes(route, state) {
  const { index, steps } = getStepPosition(route, state);
  return {
    prev: index > 0 ? steps[index - 1] : null,
    next: index < steps.length - 1 ? steps[index + 1] : null,
  };
}

export function ensureRouteAllowed(routeInfo, state) {
  const hasPlatforms = state.platforms.length > 0;
  const hasModules = state.selectedModules.length > 0;
  const moduleFlowIds = Array.isArray(state.moduleFlowIds)
    ? state.moduleFlowIds
    : state.selectedModules;

  if (routeInfo.route === ROOT_ROUTE) {
    return ROOT_ROUTE;
  }

  if (!hasPlatforms) {
    return PLATFORMS_ROUTE;
  }

  if (routeInfo.route === PLATFORMS_ROUTE) {
    return PLATFORMS_ROUTE;
  }

  if (routeInfo.route === MODULES_ROUTE) {
    return MODULES_ROUTE;
  }

  if (!hasModules) {
    return MODULES_ROUTE;
  }

  if (routeInfo.type === "module") {
    if (routeInfo.moduleId && moduleFlowIds.includes(routeInfo.moduleId)) {
      return routeInfo.route;
    }
    if (moduleFlowIds.length > 0) {
      return toModuleRoute(moduleFlowIds[0]);
    }
    return OUTPUT_ROUTE;
  }

  if (routeInfo.route === OUTPUT_ROUTE) {
    return OUTPUT_ROUTE;
  }

  return ROOT_ROUTE;
}

export function navigateTo(route, { replace = false } = {}) {
  const target = `#${route}`;
  if (replace) {
    const url = new URL(window.location.href);
    url.hash = target;
    window.history.replaceState({}, "", url.toString());
    return;
  }
  window.location.hash = target;
}

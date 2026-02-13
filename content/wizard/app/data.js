export async function loadModules() {
  const response = await fetch("./data/modules.json");
  if (!response.ok) {
    throw new Error("Failed to load modules.json");
  }
  const modules = await response.json();
  if (!Array.isArray(modules)) {
    throw new Error("modules.json must contain an array");
  }
  return modules;
}

export async function loadVersion() {
  try {
    const response = await fetch("./data/version.json", { cache: "no-store" });
    if (!response.ok) {
      return "1.0.0";
    }
    const payload = await response.json();
    if (payload && typeof payload.kickVersion === "string" && payload.kickVersion.length > 0) {
      return payload.kickVersion;
    }
  } catch (_error) {
    // Ignore fetch errors in offline fallback mode.
  }
  return "1.0.0";
}

export function mapModulesById(modules) {
  return modules.reduce((acc, item) => {
    acc[item.id] = item;
    return acc;
  }, {});
}

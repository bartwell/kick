#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
cd "$ROOT"

echo "=== 1/3 Gradle plugin tests ==="
./gradlew :gradle-plugin:test

echo ""
echo "=== 2/3 Publish plugin and Kick artifacts to Maven local ==="
./gradlew :gradle-plugin:publishToMavenLocal :main-core:publishToMavenLocal :main-runtime:publishToMavenLocal :main-runtime-stub:publishToMavenLocal :file-explorer:publishToMavenLocal :file-explorer-stub:publishToMavenLocal

echo ""
echo "=== 3/3 Build plugin sample (JVM + iOS) ==="
./gradlew :samplePluginApp:compileKotlinJvm :samplePluginApp:compileKotlinIosSimulatorArm64 -PincludePluginSample=true

echo ""
echo "All plugin checks passed."

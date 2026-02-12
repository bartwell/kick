@echo off
setlocal

cd /d "%~dp0\..\.."

echo === 1/3 Gradle plugin tests ===
call gradlew.bat :gradle-plugin:test
if errorlevel 1 exit /b 1

echo.
echo === 2/3 Publish plugin and Kick artifacts to Maven local ===
call gradlew.bat :gradle-plugin:publishToMavenLocal :main-core:publishToMavenLocal :main-runtime:publishToMavenLocal :main-runtime-stub:publishToMavenLocal :file-explorer:publishToMavenLocal :file-explorer-stub:publishToMavenLocal
if errorlevel 1 exit /b 1

echo.
echo === 3/3 Build plugin sample (JVM + iOS) ===
call gradlew.bat :samplePluginApp:compileKotlinJvm :samplePluginApp:compileKotlinIosSimulatorArm64 -PincludePluginSample=true
if errorlevel 1 exit /b 1

echo.
echo All plugin checks passed.
exit /b 0

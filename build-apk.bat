@echo off
REM Ensure environment variables are local to this script
setlocal

REM filepath: d:\Projects\Media+\build-apk.bat
echo Building Media+ Debug APK...

REM Set JAVA_HOME_CUSTOM environment variable to the Windows short path (8.3 format)
set "JAVA_HOME_CUSTOM=C:\Progra~1\ECLIPS~1\JDK-21~1.6-H"

REM Store original JAVA_HOME if needed
set "JAVA_HOME_ORIG=%JAVA_HOME%"

REM Set JAVA_HOME to Java 21 installation
set "JAVA_HOME=%JAVA_HOME_CUSTOM%"
if "%JAVA_HOME%"=="" (
    echo JAVA_HOME_CUSTOM is not set. Please set it to your Java installation path.
    goto :end
)
echo Using Java from: %JAVA_HOME%

REM Set additional Gradle options (no extra quotes)
set "GRADLE_OPTS=-Dorg.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 -Dorg.gradle.java.home=%JAVA_HOME%"
echo Using Gradle options: %GRADLE_OPTS%

REM Check if gradlew.bat exists
if not exist gradlew.bat (
    echo gradlew.bat not found! Make sure you are in the project root directory.
    goto :end
)

REM Export JAVA_HOME and GRADLE_OPTS for the gradle subprocess
set JAVA_HOME=%JAVA_HOME%
set GRADLE_OPTS=%GRADLE_OPTS%

REM FAST BUILD: Skip clean, use build cache, parallel, configure-on-demand, and offline if possible
REM Remove 'clean' for faster incremental builds
REM Add --build-cache, --parallel, --configure-on-demand, and --offline (if dependencies are already downloaded)

REM echo Running Gradle clean...
REM call gradlew.bat clean --no-configuration-cache

REM echo Building Debug APK...
call gradlew.bat --no-daemon assembleDebug --stacktrace --build-cache --parallel --configure-on-demand --no-configuration-cache
REM To use offline mode, add --offline if you are sure all dependencies are present
REM call gradlew.bat --no-daemon assembleDebug --stacktrace --build-cache --parallel --configure-on-demand --no-configuration-cache --offline

REM Check if the build was successful
if %ERRORLEVEL% NEQ 0 (
    echo There was an error building the APK.
    echo Possible causes:
    echo - JAVA_HOME is not set correctly or points to an incompatible version.
    echo - Gradle dependencies failed to resolve.
    echo - Compilation errors in the project.
    echo - Insufficient memory for the build process.
    echo Logs:
    echo Check the Gradle build logs above for more details.
    goto :end
)

REM Find the generated APK
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo Build successful! APK is located at:
    echo app\build\outputs\apk\debug\app-debug.apk
) else (
    echo Build process completed but APK not found at the expected location.
    echo Searching for APK files...
    dir /s /b app\build\outputs\*.apk
)

:end
REM Restore original JAVA_HOME before endlocal so it persists if needed
set "JAVA_HOME=%JAVA_HOME_ORIG%"
endlocal
exit /b %ERRORLEVEL%
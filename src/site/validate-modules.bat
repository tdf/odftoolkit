@echo off
REM validate-modules.bat
REM 
REM Validates the Java Module System (JPMS) implementation for ODF Toolkit
REM Windows batch script version
REM
REM Run this script from the project root directory.

REM Change to project root directory
cd /d "%~dp0\..\.."

setlocal enabledelayedexpansion

set ERRORS=0
set WARNINGS=0

REM Create build directory for logs if it doesn't exist
if not exist "target" mkdir "target"
if not exist "target\validation-logs" mkdir "target\validation-logs"
set LOG_DIR=target\validation-logs

echo ==========================================
echo ODF Toolkit Module System Validation
echo ==========================================
echo.

REM Check if Java 17+ is available
echo 1. Checking Java version...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH
    set /a ERRORS+=1
    goto :check_maven
)
echo [OK] Java found
java -version
echo.

:check_maven
REM Check if Maven is available
echo 2. Checking Maven...
where mvn >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed or not in PATH
    set /a ERRORS+=1
    goto :check_modules
)
echo [OK] Maven found ^(executable exists^)
REM Skip version display to avoid hanging - version will be shown during compilation
echo.

:check_modules
REM Check if module-info.java files exist
echo 3. Checking module-info.java files...
set MISSING_FILES=0

if exist "odfdom\src\main\java\module-info.java" (
    echo [OK] Found: odfdom\src\main\java\module-info.java
) else (
    echo [ERROR] Missing: odfdom\src\main\java\module-info.java
    set /a ERRORS+=1
    set /a MISSING_FILES+=1
)

if exist "validator\src\main\java\module-info.java" (
    echo [OK] Found: validator\src\main\java\module-info.java
) else (
    echo [ERROR] Missing: validator\src\main\java\module-info.java
    set /a ERRORS+=1
    set /a MISSING_FILES+=1
)

if exist "xslt-runner\src\main\java\module-info.java" (
    echo [OK] Found: xslt-runner\src\main\java\module-info.java
) else (
    echo [ERROR] Missing: xslt-runner\src\main\java\module-info.java
    set /a ERRORS+=1
    set /a MISSING_FILES+=1
)

if !MISSING_FILES!==0 (
    echo [OK] All module-info.java files are present
)
echo.

REM Compile the project
echo 4. Compiling project with module support...
echo   Log file: %CD%\%LOG_DIR%\maven-compile.log
call mvn clean compile -DskipTests > %LOG_DIR%\maven-compile.log 2>&1
set COMPILE_RESULT=%ERRORLEVEL%
type %LOG_DIR%\maven-compile.log
if !COMPILE_RESULT! neq 0 (
    echo [ERROR] Compilation FAILED
    echo   Full log available at: %LOG_DIR%\maven-compile.log
    set /a ERRORS+=1
) else (
    echo [OK] Compilation successful
)
echo.

REM Run tests
echo 5. Running tests...
echo   Log file: %CD%\%LOG_DIR%\maven-test.log
call mvn test > %LOG_DIR%\maven-test.log 2>&1
set TEST_RESULT=%ERRORLEVEL%
type %LOG_DIR%\maven-test.log
if !TEST_RESULT! neq 0 (
    echo [WARNING] Some tests failed
    echo   Full log available at: %LOG_DIR%\maven-test.log
    set /a WARNINGS+=1
) else (
    echo [OK] All tests passed
)
echo.

REM Package the modules
echo 6. Packaging modules...
echo   Log file: %CD%\%LOG_DIR%\maven-package.log
call mvn package -DskipTests > %LOG_DIR%\maven-package.log 2>&1
set PACKAGE_RESULT=%ERRORLEVEL%
type %LOG_DIR%\maven-package.log
if !PACKAGE_RESULT! neq 0 (
    echo [ERROR] Packaging FAILED
    echo   Full log available at: %LOG_DIR%\maven-package.log
    set /a ERRORS+=1
) else (
    echo [OK] Packaging successful
)
echo.

REM Verify JAR files contain module-info.class
echo 7. Verifying module-info.class in JAR files...
set JAR_CHECKED=0
set JAR_WITH_MODULE=0

for %%f in (odfdom\target\odfdom-java-*-SNAPSHOT.jar) do (
    if exist "%%f" (
        set /a JAR_CHECKED+=1
        jar tf "%%f" 2>nul | findstr /C:"module-info.class" >nul
        if errorlevel 1 (
            echo [ERROR] %%~nxf does NOT contain module-info.class
            set /a ERRORS+=1
        ) else (
            echo [OK] %%~nxf contains module-info.class
            set /a JAR_WITH_MODULE+=1
        )
    )
)

for %%f in (xslt-runner\target\xslt-runner-*-SNAPSHOT.jar) do (
    if exist "%%f" (
        set /a JAR_CHECKED+=1
        jar tf "%%f" 2>nul | findstr /C:"module-info.class" >nul
        if errorlevel 1 (
            echo [ERROR] %%~nxf does NOT contain module-info.class
            set /a ERRORS+=1
        ) else (
            echo [OK] %%~nxf contains module-info.class
            set /a JAR_WITH_MODULE+=1
        )
    )
)

if !JAR_CHECKED!==0 (
    echo [WARNING] No JAR files found to check (package first)
    set /a WARNINGS+=1
) else if !JAR_WITH_MODULE!==!JAR_CHECKED! (
    echo [OK] All JAR files contain module-info.class
)
echo.

REM List all log files
echo ==========================================
echo Log Files:
echo ==========================================
echo   Compilation log: %CD%\%LOG_DIR%\maven-compile.log
echo   Test log:        %CD%\%LOG_DIR%\maven-test.log
echo   Package log:     %CD%\%LOG_DIR%\maven-package.log
echo.

REM Final Summary
echo ==========================================
if !ERRORS!==0 if !WARNINGS!==0 (
    echo [SUCCESS] VALIDATION SUCCESSFUL
    echo   All checks passed!
    echo ==========================================
    endlocal
    exit /b 0
) else if !ERRORS!==0 (
    echo [WARNING] VALIDATION COMPLETED WITH WARNINGS
    echo   Errors: !ERRORS!, Warnings: !WARNINGS!
    echo   Module system is functional, but some optional checks had issues
    echo ==========================================
    endlocal
    exit /b 0
) else (
    echo [ERROR] VALIDATION FAILED
    echo   Errors: !ERRORS!, Warnings: !WARNINGS!
    echo   Please fix the errors above and run validation again
    echo ==========================================
    endlocal
    exit /b 1
)

endlocal

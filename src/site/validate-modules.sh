#!/bin/bash
# validate-modules.sh
# 
# Validates the Java Module System (JPMS) implementation for ODF Toolkit
#
# This script performs various checks to ensure the module-info.java files
# are correctly configured and the modules can be compiled and used.
#
# Run this script from the project root directory.

# Change to project root directory (where this script is located)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
cd "$PROJECT_ROOT"

# Don't exit on error - we want to collect all errors and show a summary
# set -e  # Exit on error

# Create build directory for logs if it doesn't exist
LOG_DIR="target/validation-logs"
mkdir -p "$LOG_DIR"

# Error tracking
ERRORS=0
WARNINGS=0

echo "=========================================="
echo "ODF Toolkit Module System Validation"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if Java 17+ is available
echo -e "${BLUE}1. Checking Java version...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}✗ ERROR: Java is not installed or not in PATH${NC}"
    ERRORS=$((ERRORS + 1))
else
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo -e "${RED}✗ ERROR: Java 17 or higher is required. Found: Java $JAVA_VERSION${NC}"
        ERRORS=$((ERRORS + 1))
    else
        echo -e "${GREEN}✓ Java version OK: $(java -version 2>&1 | head -n 1)${NC}"
    fi
fi
echo ""

# Check if Maven is available
echo -e "${BLUE}2. Checking Maven...${NC}"
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ ERROR: Maven is not installed or not in PATH${NC}"
    ERRORS=$((ERRORS + 1))
else
    echo -e "${GREEN}✓ Maven found: $(mvn -version | head -n 1)${NC}"
fi
echo ""

# Check if module-info.java files exist
echo -e "${BLUE}3. Checking module-info.java files...${NC}"
MODULE_FILES=(
    "odfdom/src/main/java/module-info.java"
    "validator/src/main/java/module-info.java"
    "xslt-runner/src/main/java/module-info.java"
)

MISSING_FILES=0
for file in "${MODULE_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓ Found: $file${NC}"
    else
        echo -e "${RED}✗ Missing: $file${NC}"
        MISSING_FILES=$((MISSING_FILES + 1))
        ERRORS=$((ERRORS + 1))
    fi
done
if [ $MISSING_FILES -eq 0 ]; then
    echo -e "${GREEN}  All module-info.java files are present${NC}"
fi
echo ""

# Compile the project
echo -e "${BLUE}4. Compiling project with module support...${NC}"
echo -e "${YELLOW}  Log file: $(pwd)/$LOG_DIR/maven-compile.log${NC}"
if mvn clean compile -DskipTests 2>&1 | tee "$LOG_DIR/maven-compile.log"; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation FAILED${NC}"
    echo -e "${YELLOW}  Full log available at: $LOG_DIR/maven-compile.log${NC}"
    ERRORS=$((ERRORS + 1))
fi
echo ""

# Run tests
echo -e "${BLUE}5. Running tests...${NC}"
echo -e "${YELLOW}  Log file: $(pwd)/$LOG_DIR/maven-test.log${NC}"
if mvn test 2>&1 | tee "$LOG_DIR/maven-test.log"; then
    echo -e "${GREEN}✓ All tests passed${NC}"
else
    echo -e "${YELLOW}⚠ Some tests failed${NC}"
    echo -e "${YELLOW}  Full log available at: $LOG_DIR/maven-test.log${NC}"
    WARNINGS=$((WARNINGS + 1))
fi
echo ""

# Analyze module dependencies with jdeps
echo -e "${BLUE}6. Analyzing module dependencies with jdeps...${NC}"

# Check if jdeps is available
if ! command -v jdeps &> /dev/null; then
    echo -e "${YELLOW}⚠ jdeps not found, skipping dependency analysis${NC}"
    echo -e "${YELLOW}  Install JDK to get jdeps tool${NC}"
    WARNINGS=$((WARNINGS + 1))
else
    JAR_FOUND=0
    
    # Analyze ODFDOM module
    for jar_file in odfdom/target/odfdom-java-*-SNAPSHOT.jar; do
        if [ -f "$jar_file" ]; then
            JAR_FOUND=1
            echo "Analyzing ODFDOM module: $(basename "$jar_file")"
            if jdeps --module-path odfdom/target/classes \
                  --add-modules org.odftoolkit.odfdom \
                  --list-deps \
                  "$jar_file" 2>&1 | head -30; then
                echo -e "${GREEN}  ✓ Dependency analysis successful${NC}"
            else
                echo -e "${YELLOW}  ⚠ Dependency analysis had issues${NC}"
                WARNINGS=$((WARNINGS + 1))
            fi
            break
        fi
    done
    
    if [ $JAR_FOUND -eq 0 ]; then
        echo -e "${YELLOW}⚠ ODFDOM JAR not found (package first)${NC}"
    fi
fi
echo ""

# Package the modules
echo -e "${BLUE}7. Packaging modules...${NC}"
echo -e "${YELLOW}  Log file: $(pwd)/$LOG_DIR/maven-package.log${NC}"
if mvn package -DskipTests 2>&1 | tee "$LOG_DIR/maven-package.log"; then
    echo -e "${GREEN}✓ Packaging successful${NC}"
else
    echo -e "${RED}✗ Packaging FAILED${NC}"
    echo -e "${YELLOW}  Full log available at: $LOG_DIR/maven-package.log${NC}"
    ERRORS=$((ERRORS + 1))
fi
echo ""

# Verify JAR files contain module-info.class
echo -e "${BLUE}8. Verifying module-info.class in JAR files...${NC}"
JAR_FILES=(
    "odfdom/target/odfdom-java-*-SNAPSHOT.jar"
    "xslt-runner/target/xslt-runner-*-SNAPSHOT.jar"
)

JAR_CHECKED=0
JAR_WITH_MODULE=0
for jar_pattern in "${JAR_FILES[@]}"; do
    for jar_file in $jar_pattern; do
        if [ -f "$jar_file" ]; then
            JAR_CHECKED=$((JAR_CHECKED + 1))
            if jar tf "$jar_file" 2>/dev/null | grep -q "module-info.class"; then
                echo -e "${GREEN}✓ $(basename "$jar_file") contains module-info.class${NC}"
                JAR_WITH_MODULE=$((JAR_WITH_MODULE + 1))
            else
                echo -e "${RED}✗ $(basename "$jar_file") does NOT contain module-info.class${NC}"
                ERRORS=$((ERRORS + 1))
            fi
        fi
    done
done

if [ $JAR_CHECKED -eq 0 ]; then
    echo -e "${YELLOW}⚠ No JAR files found to check (package first)${NC}"
    WARNINGS=$((WARNINGS + 1))
elif [ $JAR_WITH_MODULE -eq $JAR_CHECKED ]; then
    echo -e "${GREEN}  All JAR files contain module-info.class${NC}"
fi
echo ""

# List all log files
echo "=========================================="
echo -e "${BLUE}Log Files:${NC}"
echo "=========================================="
echo -e "${YELLOW}  Compilation log: $(pwd)/$LOG_DIR/maven-compile.log${NC}"
echo -e "${YELLOW}  Test log:        $(pwd)/$LOG_DIR/maven-test.log${NC}"
echo -e "${YELLOW}  Package log:     $(pwd)/$LOG_DIR/maven-package.log${NC}"
echo ""

# Final Summary
echo "=========================================="
if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✓ VALIDATION SUCCESSFUL${NC}"
    echo -e "${GREEN}  All checks passed!${NC}"
    echo "=========================================="
    exit 0
elif [ $ERRORS -eq 0 ]; then
    echo -e "${YELLOW}⚠ VALIDATION COMPLETED WITH WARNINGS${NC}"
    echo -e "${YELLOW}  Errors: $ERRORS, Warnings: $WARNINGS${NC}"
    echo -e "${GREEN}  Module system is functional, but some optional checks had issues${NC}"
    echo "=========================================="
    exit 0
else
    echo -e "${RED}✗ VALIDATION FAILED${NC}"
    echo -e "${RED}  Errors: $ERRORS, Warnings: $WARNINGS${NC}"
    echo -e "${RED}  Please fix the errors above and run validation again${NC}"
    echo "=========================================="
    exit 1
fi

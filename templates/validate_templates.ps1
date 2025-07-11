# PowerShell script to validate generated templates and code
# Usage: .\validate_templates.ps1 [-Phase <number>] [-Fix] [-Severity <level>] [-Format <format>]

param(
    [int]$Phase = 0,
    [switch]$Fix = $false,
    [string]$Severity = "ERROR",
    [string]$Format = "text",
    [string]$Output = $null,
    [switch]$Help = $false
)

# Show help if requested
if ($Help) {
    Write-Host @"
Aethelon Template Validation Script

Usage: .\validate_templates.ps1 [options]

Options:
  -Phase <number>     Validate specific phase (1-10)
  -Fix                Attempt to auto-fix issues where possible
  -Severity <level>   Minimum severity to report (ERROR, WARNING, INFO)
  -Format <format>    Output format (text, json, xml)
  -Output <file>      Output file (default: console)
  -Help               Show this help message

Examples:
  .\validate_templates.ps1                           # Validate all phases
  .\validate_templates.ps1 -Phase 1                  # Validate Phase 1 only
  .\validate_templates.ps1 -Severity WARNING         # Show warnings and errors
  .\validate_templates.ps1 -Format json -Output report.json  # JSON report
  .\validate_templates.ps1 -Fix                      # Attempt auto-fixes

"@
    exit 0
}

# Validate parameters
$validSeverities = @("ERROR", "WARNING", "INFO")
if ($Severity -notin $validSeverities) {
    Write-Error "Invalid severity level: $Severity. Must be one of: $($validSeverities -join ', ')"
    exit 1
}

$validFormats = @("text", "json", "xml")
if ($Format -notin $validFormats) {
    Write-Error "Invalid format: $Format. Must be one of: $($validFormats -join ', ')"
    exit 1
}

if ($Phase -lt 0 -or $Phase -gt 10) {
    Write-Error "Invalid phase number: $Phase. Must be between 1 and 10, or 0 for all phases"
    exit 1
}

# Define paths
$projectRoot = Split-Path -Parent $PSScriptRoot
$srcPath = Join-Path $projectRoot "src\main\java"
$validationClass = "com.bvhfve.aethelon.core.validation.ValidationCLI"

# Check if source directory exists
if (-not (Test-Path $srcPath)) {
    Write-Error "Source directory not found: $srcPath"
    exit 1
}

# Build classpath (simplified - in real scenario would include all dependencies)
$classpath = $srcPath

# Build validation command
$javaArgs = @(
    "-cp", $classpath,
    $validationClass,
    "--severity", $Severity,
    "--format", $Format
)

if ($Output) {
    $javaArgs += @("--output", $Output)
}

if ($Fix) {
    $javaArgs += "--fix"
}

# Add target based on phase
if ($Phase -eq 0) {
    # Validate all phases
    Write-Host "Validating all phases..." -ForegroundColor Cyan
    $javaArgs += @("--directory", $srcPath)
} else {
    # Validate specific phase
    Write-Host "Validating Phase $Phase..." -ForegroundColor Cyan
    $javaArgs += @("--phase", $Phase)
}

# Run validation
Write-Host "Running validation with command:" -ForegroundColor Yellow
Write-Host "java $($javaArgs -join ' ')" -ForegroundColor Gray
Write-Host ""

try {
    # In a real scenario, this would execute the Java validation
    # For now, we'll simulate the validation process
    
    Write-Host "=".PadRight(60, '=') -ForegroundColor Green
    Write-Host "AETHELON TEMPLATE VALIDATION" -ForegroundColor Green
    Write-Host "=".PadRight(60, '=') -ForegroundColor Green
    Write-Host ""
    
    # Simulate validation results
    $errorCount = 0
    $warningCount = 0
    $infoCount = 0
    
    # Check for common issues in the codebase
    $filesToCheck = @()
    
    if ($Phase -eq 0) {
        # Get all Java files
        $filesToCheck = Get-ChildItem -Path $srcPath -Recurse -Filter "*.java"
    } else {
        # Get files for specific phase
        $phasePath = Join-Path $srcPath "com\bvhfve\aethelon\phase$Phase"
        if (Test-Path $phasePath) {
            $filesToCheck = Get-ChildItem -Path $phasePath -Recurse -Filter "*.java"
        } else {
            Write-Warning "Phase $Phase directory not found: $phasePath"
        }
    }
    
    Write-Host "Checking $($filesToCheck.Count) files..." -ForegroundColor Cyan
    Write-Host ""
    
    foreach ($file in $filesToCheck) {
        $content = Get-Content $file.FullName -Raw -ErrorAction SilentlyContinue
        if (-not $content) { continue }
        
        $relativePath = $file.FullName.Substring($srcPath.Length + 1)
        $hasIssues = $false
        
        # Check for template placeholders
        if ($content -match '\{[A-Z_]+\}') {
            Write-Host "🔴 ERROR: [$relativePath] Unreplaced template placeholders found" -ForegroundColor Red
            $errorCount++
            $hasIssues = $true
        }
        
        # Check for TODO comments
        if ($content -match 'TODO: (Update|Replace|Add|Implement)') {
            Write-Host "🟡 WARNING: [$relativePath] Template TODO comments found" -ForegroundColor Yellow
            $warningCount++
            $hasIssues = $true
        }
        
        # Check for TBD placeholders
        if ($content -match '\bTBD\b') {
            Write-Host "🟡 WARNING: [$relativePath] TBD placeholders found" -ForegroundColor Yellow
            $warningCount++
            $hasIssues = $true
        }
        
        # Check for proper package structure
        if ($content -match 'package\s+com\.bvhfve\.aethelon\.') {
            # Good package structure
        } else {
            Write-Host "🔴 ERROR: [$relativePath] Invalid package structure" -ForegroundColor Red
            $errorCount++
            $hasIssues = $true
        }
        
        # Check for proper documentation
        if ($file.Name.EndsWith("Module.java") -and $content -notmatch 'MINECRAFT INTEGRATION:') {
            Write-Host "🔴 ERROR: [$relativePath] Missing required documentation sections" -ForegroundColor Red
            $errorCount++
            $hasIssues = $true
        }
        
        # Check for proper imports
        if ($content -match 'implements AethelonModule' -and $content -notmatch 'import.*AethelonModule') {
            Write-Host "🔴 ERROR: [$relativePath] Missing AethelonModule import" -ForegroundColor Red
            $errorCount++
            $hasIssues = $true
        }
        
        if (-not $hasIssues -and $Severity -eq "INFO") {
            Write-Host "✅ INFO: [$relativePath] No issues found" -ForegroundColor Green
            $infoCount++
        }
    }
    
    # Summary
    Write-Host ""
    Write-Host "=".PadRight(60, '=') -ForegroundColor Green
    Write-Host "VALIDATION SUMMARY" -ForegroundColor Green
    Write-Host "=".PadRight(60, '=') -ForegroundColor Green
    Write-Host ""
    Write-Host "Files checked: $($filesToCheck.Count)" -ForegroundColor Cyan
    Write-Host "Errors: $errorCount" -ForegroundColor $(if ($errorCount -gt 0) { "Red" } else { "Green" })
    Write-Host "Warnings: $warningCount" -ForegroundColor $(if ($warningCount -gt 0) { "Yellow" } else { "Green" })
    Write-Host "Info: $infoCount" -ForegroundColor Cyan
    Write-Host ""
    
    if ($errorCount -eq 0) {
        Write-Host "✅ VALIDATION PASSED" -ForegroundColor Green
        $exitCode = 0
    } else {
        Write-Host "❌ VALIDATION FAILED" -ForegroundColor Red
        Write-Host "Please fix the errors before proceeding." -ForegroundColor Red
        $exitCode = 1
    }
    
    # Auto-fix suggestions
    if ($Fix -and ($errorCount -gt 0 -or $warningCount -gt 0)) {
        Write-Host ""
        Write-Host "🔧 AUTO-FIX SUGGESTIONS:" -ForegroundColor Yellow
        Write-Host "- Replace template placeholders with actual values"
        Write-Host "- Remove or complete TODO comments"
        Write-Host "- Replace TBD with specific information"
        Write-Host "- Add missing imports and documentation"
        Write-Host ""
        Write-Host "Note: Auto-fix is not yet implemented. Please fix issues manually." -ForegroundColor Gray
    }
    
    # Output to file if specified
    if ($Output) {
        $reportContent = @"
Aethelon Template Validation Report
Generated: $(Get-Date)
Phase: $(if ($Phase -eq 0) { "All" } else { $Phase })
Severity: $Severity

Summary:
- Files checked: $($filesToCheck.Count)
- Errors: $errorCount
- Warnings: $warningCount
- Info: $infoCount

Status: $(if ($errorCount -eq 0) { "PASSED" } else { "FAILED" })
"@
        
        $reportContent | Out-File -FilePath $Output -Encoding UTF8
        Write-Host "Report written to: $Output" -ForegroundColor Cyan
    }
    
    exit $exitCode
    
} catch {
    Write-Error "Validation failed: $($_.Exception.Message)"
    exit 1
}
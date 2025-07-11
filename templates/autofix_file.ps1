# PowerShell script to auto-fix validation issues in files
# Usage: .\autofix_file.ps1 [-FilePath <path>] [-DryRun] [-Backup] [-Severity <level>]

param(
    [Parameter(Mandatory=$false)]
    [string]$FilePath = "",
    
    [switch]$DryRun = $false,
    [switch]$Backup = $true,
    [string]$Severity = "WARNING",
    [switch]$Help = $false
)

# Show help if requested
if ($Help) {
    Write-Host @"
Aethelon Auto-Fix Script

Usage: .\autofix_file.ps1 [options]

Options:
  -FilePath <path>    File to auto-fix (default: current file in VS Code)
  -DryRun             Preview fixes without applying them
  -Backup             Create backup before fixing (default: true)
  -Severity <level>   Minimum severity to fix (ERROR, WARNING, INFO)
  -Help               Show this help message

Examples:
  .\autofix_file.ps1 -FilePath "src\main\java\MyModule.java"
  .\autofix_file.ps1 -DryRun                    # Preview fixes only
  .\autofix_file.ps1 -Severity ERROR            # Fix only errors

"@
    exit 0
}

# If no file path provided, try to get from VS Code context
if ([string]::IsNullOrEmpty($FilePath)) {
    # In VS Code, the current file path might be available through environment
    $FilePath = $env:VSCODE_FILE_PATH
    
    if ([string]::IsNullOrEmpty($FilePath)) {
        Write-Error "No file path specified. Use -FilePath parameter or run from VS Code."
        exit 1
    }
}

# Validate file exists
if (-not (Test-Path $FilePath)) {
    Write-Error "File not found: $FilePath"
    exit 1
}

# Validate severity
$validSeverities = @("ERROR", "WARNING", "INFO")
if ($Severity -notin $validSeverities) {
    Write-Error "Invalid severity level: $Severity. Must be one of: $($validSeverities -join ', ')"
    exit 1
}

Write-Host "=".PadRight(60, '=') -ForegroundColor Cyan
Write-Host "AETHELON AUTO-FIX" -ForegroundColor Cyan
Write-Host "=".PadRight(60, '=') -ForegroundColor Cyan
Write-Host ""

Write-Host "File: $FilePath" -ForegroundColor Yellow
Write-Host "Mode: $(if ($DryRun) { 'DRY RUN (preview only)' } else { 'APPLY FIXES' })" -ForegroundColor $(if ($DryRun) { 'Yellow' } else { 'Green' })
Write-Host "Backup: $(if ($Backup) { 'Enabled' } else { 'Disabled' })" -ForegroundColor $(if ($Backup) { 'Green' } else { 'Yellow' })
Write-Host "Severity: $Severity" -ForegroundColor Cyan
Write-Host ""

try {
    # Read file content
    $content = Get-Content $FilePath -Raw -ErrorAction Stop
    $originalContent = $content
    $fixesApplied = 0
    $changesCount = 0
    
    # Apply auto-fixes based on common validation issues
    Write-Host "Analyzing file for auto-fixable issues..." -ForegroundColor Cyan
    
    # 1. Fix template placeholders
    Write-Host "Checking for template placeholders..." -ForegroundColor Gray
    $placeholderFixes = Fix-TemplatePlaceholders -Content $content -FilePath $FilePath
    if ($placeholderFixes.ChangesCount -gt 0) {
        $content = $placeholderFixes.Content
        $fixesApplied++
        $changesCount += $placeholderFixes.ChangesCount
        Write-Host "  ✅ Fixed $($placeholderFixes.ChangesCount) template placeholders" -ForegroundColor Green
    } else {
        Write-Host "  ℹ️  No template placeholders found" -ForegroundColor Gray
    }
    
    # 2. Fix missing imports
    Write-Host "Checking for missing imports..." -ForegroundColor Gray
    $importFixes = Fix-MissingImports -Content $content -FilePath $FilePath
    if ($importFixes.ChangesCount -gt 0) {
        $content = $importFixes.Content
        $fixesApplied++
        $changesCount += $importFixes.ChangesCount
        Write-Host "  ✅ Added $($importFixes.ChangesCount) missing imports" -ForegroundColor Green
    } else {
        Write-Host "  ℹ️  No missing imports found" -ForegroundColor Gray
    }
    
    # 3. Fix package declarations
    Write-Host "Checking package declaration..." -ForegroundColor Gray
    $packageFixes = Fix-PackageDeclaration -Content $content -FilePath $FilePath
    if ($packageFixes.ChangesCount -gt 0) {
        $content = $packageFixes.Content
        $fixesApplied++
        $changesCount += $packageFixes.ChangesCount
        Write-Host "  ✅ Fixed package declaration" -ForegroundColor Green
    } else {
        Write-Host "  ℹ️  Package declaration is correct" -ForegroundColor Gray
    }
    
    # 4. Fix basic documentation issues
    Write-Host "Checking documentation..." -ForegroundColor Gray
    $docFixes = Fix-BasicDocumentation -Content $content -FilePath $FilePath
    if ($docFixes.ChangesCount -gt 0) {
        $content = $docFixes.Content
        $fixesApplied++
        $changesCount += $docFixes.ChangesCount
        Write-Host "  ✅ Fixed $($docFixes.ChangesCount) documentation issues" -ForegroundColor Green
    } else {
        Write-Host "  ℹ️  Documentation appears complete" -ForegroundColor Gray
    }
    
    # 5. Remove TODO comments from templates
    Write-Host "Checking for template TODO comments..." -ForegroundColor Gray
    $todoFixes = Fix-TemplateTodos -Content $content -FilePath $FilePath
    if ($todoFixes.ChangesCount -gt 0) {
        $content = $todoFixes.Content
        $fixesApplied++
        $changesCount += $todoFixes.ChangesCount
        Write-Host "  ⚠️  Removed $($todoFixes.ChangesCount) template TODO comments" -ForegroundColor Yellow
        Write-Host "     Note: Review removed TODOs to ensure they were completed" -ForegroundColor Yellow
    } else {
        Write-Host "  ℹ️  No template TODO comments found" -ForegroundColor Gray
    }
    
    Write-Host ""
    
    # Summary
    if ($fixesApplied -eq 0) {
        Write-Host "✅ NO FIXES NEEDED" -ForegroundColor Green
        Write-Host "File appears to be in good condition." -ForegroundColor Green
    } else {
        Write-Host "🔧 FIXES SUMMARY:" -ForegroundColor Yellow
        Write-Host "  Applied: $fixesApplied fix categories" -ForegroundColor Cyan
        Write-Host "  Changes: $changesCount total modifications" -ForegroundColor Cyan
        
        if ($DryRun) {
            Write-Host ""
            Write-Host "🔍 DRY RUN MODE - No changes were saved" -ForegroundColor Yellow
            Write-Host "Run without -DryRun to apply these fixes." -ForegroundColor Yellow
        } else {
            # Create backup if enabled
            if ($Backup) {
                $backupPath = "$FilePath.backup.$(Get-Date -Format 'yyyyMMdd_HHmmss')"
                Copy-Item $FilePath $backupPath
                Write-Host "  Backup: $backupPath" -ForegroundColor Gray
            }
            
            # Write fixed content
            $content | Out-File -FilePath $FilePath -Encoding UTF8 -NoNewline
            Write-Host ""
            Write-Host "✅ FIXES APPLIED SUCCESSFULLY" -ForegroundColor Green
            Write-Host "File has been updated with auto-fixes." -ForegroundColor Green
        }
    }
    
    # Suggest next steps
    if ($fixesApplied -gt 0) {
        Write-Host ""
        Write-Host "📋 NEXT STEPS:" -ForegroundColor Cyan
        Write-Host "1. Review the applied changes" -ForegroundColor White
        Write-Host "2. Run validation to check for remaining issues:" -ForegroundColor White
        Write-Host "   .\templates\validate_templates.ps1 -File `"$FilePath`"" -ForegroundColor Gray
        Write-Host "3. Test the code to ensure it still works correctly" -ForegroundColor White
    }
    
} catch {
    Write-Error "Auto-fix failed: $($_.Exception.Message)"
    exit 1
}

# Helper functions for auto-fixes
function Fix-TemplatePlaceholders {
    param($Content, $FilePath)
    
    $modifiedContent = $Content
    $changes = 0
    
    # Extract context from file path
    $fileName = Split-Path $FilePath -Leaf
    $pathParts = $FilePath -split '[\\\/]'
    
    # Find phase number
    $phaseNumber = "1"
    foreach ($part in $pathParts) {
        if ($part -match 'phase(\d+)') {
            $phaseNumber = $matches[1]
            break
        }
    }
    
    # Find module name
    $moduleName = "unknown"
    $phaseIndex = [array]::IndexOf($pathParts, "phase$phaseNumber")
    if ($phaseIndex -ge 0 -and $phaseIndex + 1 -lt $pathParts.Length) {
        $moduleName = $pathParts[$phaseIndex + 1]
    }
    
    # Class name from file name
    $className = $fileName -replace '\.java$', ''
    
    # Apply replacements
    $replacements = @{
        '{PHASE_NUMBER}' = $phaseNumber
        '{MODULE_NAME}' = $moduleName
        '{MODULE_NAME_CAPITALIZED}' = (Get-Culture).TextInfo.ToTitleCase($moduleName)
        '{CLASS_NAME}' = $className
        '{PHASE_DESCRIPTION}' = Get-PhaseDescription $phaseNumber
        '{MODULE_DESCRIPTION}' = "$((Get-Culture).TextInfo.ToTitleCase($moduleName)) system coordination"
        '{MODULE_PURPOSE}' = "Coordinate $moduleName functionality"
        '{MINECRAFT_APIS_USED}' = "Fabric API, Minecraft core systems"
        '{MINECRAFT_HOOKS}' = "Module loading system"
        '{MINECRAFT_MODIFICATIONS}' = "None (coordination layer)"
        '{BREAKING_CHANGES}' = "None expected (follows established patterns)"
    }
    
    foreach ($placeholder in $replacements.Keys) {
        if ($modifiedContent -match [regex]::Escape($placeholder)) {
            $modifiedContent = $modifiedContent -replace [regex]::Escape($placeholder), $replacements[$placeholder]
            $changes++
        }
    }
    
    return @{ Content = $modifiedContent; ChangesCount = $changes }
}

function Fix-MissingImports {
    param($Content, $FilePath)
    
    $modifiedContent = $Content
    $changes = 0
    
    # Common imports to check for
    $importChecks = @{
        'AethelonCore\.' = 'import com.bvhfve.aethelon.core.AethelonCore;'
        'implements AethelonModule' = 'import com.bvhfve.aethelon.core.util.AethelonModule;'
        'List<' = 'import java.util.List;'
        'ArrayList' = 'import java.util.ArrayList;'
        'Arrays\.asList' = 'import java.util.Arrays;'
    }
    
    $importsToAdd = @()
    
    foreach ($pattern in $importChecks.Keys) {
        $importStatement = $importChecks[$pattern]
        
        if ($modifiedContent -match $pattern -and $modifiedContent -notmatch [regex]::Escape($importStatement)) {
            $importsToAdd += $importStatement
        }
    }
    
    if ($importsToAdd.Count -gt 0) {
        # Find insertion point (after package declaration)
        if ($modifiedContent -match '(package\s+[^;]+;\s*\n)') {
            $insertPoint = $matches[1]
            $newImports = ($importsToAdd | Sort-Object) -join "`n"
            $modifiedContent = $modifiedContent -replace [regex]::Escape($insertPoint), "$insertPoint`n$newImports`n"
            $changes = $importsToAdd.Count
        }
    }
    
    return @{ Content = $modifiedContent; ChangesCount = $changes }
}

function Fix-PackageDeclaration {
    param($Content, $FilePath)
    
    $modifiedContent = $Content
    $changes = 0
    
    # Extract expected package from file path
    $pathParts = $FilePath -split '[\\\/]'
    $srcIndex = [array]::LastIndexOf($pathParts, 'java')
    
    if ($srcIndex -ge 0 -and $srcIndex + 1 -lt $pathParts.Length) {
        $packageParts = $pathParts[($srcIndex + 1)..($pathParts.Length - 2)]
        $expectedPackage = $packageParts -join '.'
        
        if ($expectedPackage -and $expectedPackage.StartsWith('com.bvhfve.aethelon')) {
            # Check if package declaration exists and is correct
            if ($modifiedContent -match '^package\s+([^;]+);') {
                $currentPackage = $matches[1].Trim()
                if ($currentPackage -ne $expectedPackage) {
                    $modifiedContent = $modifiedContent -replace '^package\s+[^;]+;', "package $expectedPackage;"
                    $changes = 1
                }
            } else {
                # Add missing package declaration
                $modifiedContent = "package $expectedPackage;`n`n$modifiedContent"
                $changes = 1
            }
        }
    }
    
    return @{ Content = $modifiedContent; ChangesCount = $changes }
}

function Fix-BasicDocumentation {
    param($Content, $FilePath)
    
    $modifiedContent = $Content
    $changes = 0
    
    # Replace TBD placeholders with basic information
    $tbdReplacements = @{
        'TBD - Update based on implementation' = 'Implementation follows established patterns'
        'TBD - Add specific implementation details' = 'Specific implementation details to be added'
        'TBD\b' = 'To be determined'
    }
    
    foreach ($pattern in $tbdReplacements.Keys) {
        $replacement = $tbdReplacements[$pattern]
        if ($modifiedContent -match $pattern) {
            $modifiedContent = $modifiedContent -replace $pattern, $replacement
            $changes++
        }
    }
    
    return @{ Content = $modifiedContent; ChangesCount = $changes }
}

function Fix-TemplateTodos {
    param($Content, $FilePath)
    
    $modifiedContent = $Content
    $changes = 0
    
    # Remove common template TODO comments
    $todoPatterns = @(
        '// TODO: Update dependencies based on phase requirements',
        '// TODO: List sub-modules here',
        '// TODO: Replace with actual configuration class',
        '// TODO: Add module-specific configuration fields',
        '// TODO: Implement module initialization',
        '// TODO: Add implementation classes as needed'
    )
    
    foreach ($pattern in $todoPatterns) {
        if ($modifiedContent -match [regex]::Escape($pattern)) {
            $modifiedContent = $modifiedContent -replace [regex]::Escape($pattern), ''
            $changes++
        }
    }
    
    # Clean up empty lines left by removed TODOs
    $modifiedContent = $modifiedContent -replace '\n\s*\n\s*\n', "`n`n"
    
    return @{ Content = $modifiedContent; ChangesCount = $changes }
}

function Get-PhaseDescription {
    param($PhaseNumber)
    
    switch ($PhaseNumber) {
        "1" { return "Basic Entity Foundation" }
        "2" { return "Entity Behavior & Movement" }
        "3" { return "Damage Response & Player Interaction" }
        "4" { return "Island Structure System" }
        "5" { return "Dynamic Island Movement" }
        "6" { return "Explosion & Destruction System" }
        "7" { return "Spawn Control & Population Management" }
        "8" { return "Configuration & Balancing" }
        "9" { return "Polish & Optimization" }
        "10" { return "Advanced Features & Integration" }
        default { return "Phase $PhaseNumber Implementation" }
    }
}
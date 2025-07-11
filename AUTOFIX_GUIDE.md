# Aethelon Auto-Fix Guide

This guide explains how to use the auto-fix functionality to automatically resolve common validation issues in your Aethelon mod code.

## Overview

The auto-fix system can automatically resolve many common validation issues:
- **Template placeholders** that haven't been replaced
- **Missing imports** for commonly used classes
- **Package declaration** mismatches
- **Basic documentation** issues (TBD placeholders)
- **Template TODO comments** removal

## Quick Start

### Auto-Fix Current File (VS Code)
1. Open the file you want to fix
2. Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
3. Type "Tasks: Run Task"
4. Select "Aethelon: Auto-fix Current File"

### Auto-Fix via Command Line
```powershell
# Fix specific file
.\templates\autofix_file.ps1 -FilePath "src\main\java\MyModule.java"

# Preview fixes without applying (dry run)
.\templates\autofix_file.ps1 -FilePath "src\main\java\MyModule.java" -DryRun

# Fix without creating backup
.\templates\autofix_file.ps1 -FilePath "src\main\java\MyModule.java" -Backup:$false
```

## Auto-Fix Categories

### 1. Template Placeholder Replacement
**What it fixes**: Unreplaced `{PLACEHOLDER}` values in generated code

**Example fixes**:
```java
// Before
public String getModuleName() {
    return "{MODULE_NAME}";
}

// After
public String getModuleName() {
    return "phase2.ai";
}
```

**Intelligence**: 
- Extracts phase number from file path
- Determines module name from directory structure
- Generates contextual descriptions based on class type

### 2. Missing Import Addition
**What it fixes**: Missing import statements for commonly used classes

**Example fixes**:
```java
// Before (missing imports)
public class AiModule implements AethelonModule {
    // AethelonCore.LOGGER usage without import
}

// After (imports added)
import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.util.AethelonModule;

public class AiModule implements AethelonModule {
    // Now properly imported
}
```

**Supported imports**:
- Core Aethelon classes (AethelonCore, AethelonModule, etc.)
- Common Java utilities (List, ArrayList, Arrays, etc.)
- Minecraft/Fabric APIs (EntityType, World, etc.)

### 3. Package Declaration Fixes
**What it fixes**: Package declarations that don't match file location

**Example fixes**:
```java
// Before (wrong package)
package com.example.wrong;

// After (correct package based on file path)
package com.bvhfve.aethelon.phase2.ai;
```

### 4. Documentation Improvements
**What it fixes**: TBD placeholders and incomplete documentation

**Example fixes**:
```java
// Before
// Uses: TBD - Update based on implementation

// After  
// Uses: Implementation follows established patterns
```

### 5. Template TODO Removal
**What it fixes**: Template-generated TODO comments

**Example fixes**:
```java
// Before
// TODO: Update dependencies based on phase requirements
// TODO: List sub-modules here

// After
// (Comments removed - ensure they were actually completed!)
```

## VS Code Integration

### Tasks Available
- **Aethelon: Auto-fix Current File** - Fix the currently open file
- **Aethelon: Validate All** - Run validation on entire project
- **Aethelon: Validate Current Phase** - Validate specific phase
- **Aethelon: Create New Phase** - Generate new phase from template
- **Aethelon: Create New Module** - Generate new module from template

### Keyboard Shortcuts
Add these to your VS Code `keybindings.json`:
```json
[
    {
        "key": "ctrl+alt+f",
        "command": "workbench.action.tasks.runTask",
        "args": "Aethelon: Auto-fix Current File"
    },
    {
        "key": "ctrl+alt+v",
        "command": "workbench.action.tasks.runTask", 
        "args": "Aethelon: Validate All"
    }
]
```

### Code Snippets
Type these prefixes in Java files and press Tab:
- `aethelon-module` - Complete module class template
- `aethelon-config` - Configuration class template
- `aethelon-entity` - Entity class template
- `aethelon-doc` - Class documentation template
- `aethelon-method-doc` - Method documentation template

### Settings
Configure auto-fix behavior in VS Code settings:
```json
{
    "aethelon.autofix.enabled": true,
    "aethelon.autofix.onSave": false,
    "aethelon.validation.onSave": true,
    "aethelon.validation.severity": "WARNING"
}
```

## Command Line Options

### Basic Usage
```powershell
.\templates\autofix_file.ps1 -FilePath <path> [options]
```

### Options
- `-FilePath <path>` - File to auto-fix
- `-DryRun` - Preview fixes without applying them
- `-Backup` - Create backup before fixing (default: true)
- `-Severity <level>` - Minimum severity to fix (ERROR, WARNING, INFO)
- `-Help` - Show help message

### Examples
```powershell
# Fix with preview first
.\templates\autofix_file.ps1 -FilePath "MyModule.java" -DryRun
.\templates\autofix_file.ps1 -FilePath "MyModule.java"

# Fix without backup
.\templates\autofix_file.ps1 -FilePath "MyModule.java" -Backup:$false

# Fix only critical errors
.\templates\autofix_file.ps1 -FilePath "MyModule.java" -Severity ERROR
```

## Workflow Integration

### Development Workflow
1. **Create** new phase/module using templates
2. **Auto-fix** to resolve placeholder and import issues
3. **Validate** to check for remaining problems
4. **Implement** actual functionality
5. **Validate again** before committing

### Example Workflow
```powershell
# 1. Create new phase
.\templates\create_phase.ps1 -PhaseNumber 3 -PhaseName "Damage Response" -SubModules @("damage", "interaction")

# 2. Auto-fix generated files
Get-ChildItem "src\main\java\com\bvhfve\aethelon\phase3" -Recurse -Filter "*.java" | 
    ForEach-Object { .\templates\autofix_file.ps1 -FilePath $_.FullName }

# 3. Validate results
.\templates\validate_templates.ps1 -Phase 3 -Severity WARNING

# 4. Continue with implementation...
```

### CI/CD Integration
```yaml
# GitHub Actions example
- name: Auto-fix and validate
  run: |
    # Auto-fix common issues
    Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object {
      .\templates\autofix_file.ps1 -FilePath $_.FullName -Backup:$false
    }
    
    # Validate results
    .\templates\validate_templates.ps1 -Severity ERROR
```

## Safety Features

### Backup Creation
- **Automatic backups** created before applying fixes
- **Timestamped filenames** (e.g., `MyModule.java.backup.20241201_143022`)
- **Configurable** via `-Backup` parameter

### Dry Run Mode
- **Preview fixes** without applying them
- **See exactly** what would be changed
- **Safe exploration** of auto-fix capabilities

### Selective Fixing
- **Severity filtering** to fix only critical issues
- **Rule-specific** enabling/disabling (future feature)
- **Manual review** recommended for complex changes

## Limitations and Considerations

### What Auto-Fix Cannot Do
- **Complex logic implementation** - Only fixes structural/template issues
- **Design decisions** - Cannot make architectural choices
- **Context-specific content** - May use generic defaults
- **Custom business logic** - Requires human implementation

### When to Use Manual Fixes
- **Complex documentation** requiring domain knowledge
- **Specific implementation details** unique to your module
- **Custom error handling** patterns
- **Performance-critical code** sections

### Best Practices
1. **Always review** auto-fixed changes
2. **Use dry run** first for unfamiliar files
3. **Keep backups** enabled for important files
4. **Validate after** auto-fixing
5. **Test functionality** after applying fixes

## Troubleshooting

### Common Issues

1. **PowerShell Execution Policy**
   ```powershell
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```

2. **File Permission Errors**
   - Ensure file is not read-only
   - Check VS Code doesn't have file locked
   - Run as administrator if needed

3. **Incorrect Replacements**
   - Review file path structure
   - Check phase/module naming conventions
   - Verify template placeholders are standard format

4. **Missing Context**
   - Ensure file is in proper directory structure
   - Check package structure matches expectations
   - Verify phase numbering is correct

### Getting Help

1. **Run with dry-run** to see what would be changed
2. **Check validation output** for specific issues
3. **Review backup files** if fixes went wrong
4. **Use VS Code tasks** for integrated experience

## Advanced Usage

### Custom Auto-Fix Rules
Future versions will support custom auto-fix rules:
```java
// Example custom rule (future feature)
public class CustomAutoFixRule implements AutoFixRule {
    // Implementation
}
```

### Batch Operations
```powershell
# Fix all files in a phase
Get-ChildItem "src\main\java\com\bvhfve\aethelon\phase2" -Recurse -Filter "*.java" |
    ForEach-Object { .\templates\autofix_file.ps1 -FilePath $_.FullName }

# Fix only module files
Get-ChildItem -Recurse -Filter "*Module.java" |
    ForEach-Object { .\templates\autofix_file.ps1 -FilePath $_.FullName }
```

### Integration with Build Tools
```gradle
// Gradle task example
task autoFix(type: Exec) {
    commandLine 'powershell', '-File', 'templates/autofix_all.ps1'
}

build.dependsOn autoFix
```

The auto-fix system significantly speeds up development by handling routine fixes automatically, allowing you to focus on implementing actual functionality rather than fixing template and structural issues.
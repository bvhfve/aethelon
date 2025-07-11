# PowerShell script to create a new Aethelon module from templates
# Usage: .\create_module.ps1 -PhaseNumber 2 -ModuleName "ai" -Description "AI system coordination"

param(
    [Parameter(Mandatory=$true)]
    [int]$PhaseNumber,
    
    [Parameter(Mandatory=$true)]
    [string]$ModuleName,
    
    [Parameter(Mandatory=$true)]
    [string]$Description,
    
    [string]$BasePath = "src/main/java/com/bvhfve/aethelon"
)

# Validate inputs
if ($PhaseNumber -lt 1 -or $PhaseNumber -gt 10) {
    Write-Error "Phase number must be between 1 and 10"
    exit 1
}

if ([string]::IsNullOrWhiteSpace($ModuleName)) {
    Write-Error "Module name cannot be empty"
    exit 1
}

# Convert module name to proper case for class names
$ModuleNameCapitalized = (Get-Culture).TextInfo.ToTitleCase($ModuleName.ToLower())

# Define paths
$PhaseDir = "$BasePath/phase$PhaseNumber"
$ModuleDir = "$PhaseDir/$ModuleName"
$TemplateDir = "templates"

# Create directories
Write-Host "Creating directories..."
New-Item -ItemType Directory -Force -Path $ModuleDir | Out-Null

# Function to replace placeholders in template content
function Replace-Placeholders {
    param(
        [string]$Content,
        [hashtable]$Replacements
    )
    
    foreach ($key in $Replacements.Keys) {
        $Content = $Content -replace [regex]::Escape($key), $Replacements[$key]
    }
    
    return $Content
}

# Define placeholder replacements
$replacements = @{
    '{PHASE_NUMBER}' = $PhaseNumber
    '{MODULE_NAME}' = $ModuleName
    '{MODULE_NAME_CAPITALIZED}' = $ModuleNameCapitalized
    '{CLASS_NAME}' = "${ModuleNameCapitalized}Module"
    '{MODULE_DESCRIPTION}' = $Description
    '{PHASE_DESCRIPTION}' = "Phase $PhaseNumber"
    '{MODULE_PURPOSE}' = "Coordinate $ModuleName functionality for Phase $PhaseNumber"
    '{MODULE_DEPENDENCIES}' = "phase$PhaseNumber"
    '{MODULE_PROVIDES}' = "$ModuleName system coordination and management"
    '{SUB_MODULE_PRIORITY}' = "1"
    '{MINECRAFT_APIS_USED}' = "TBD - Update based on implementation"
    '{MINECRAFT_HOOKS}' = "TBD - Update based on implementation"
    '{MINECRAFT_MODIFICATIONS}' = "TBD - Update based on implementation"
    '{BREAKING_CHANGES}' = "TBD - Update based on implementation"
    '{IMPLEMENTATION_DETAILS}' = "TBD - Add specific implementation details"
    '{DETAILED_DESCRIPTION}' = "TBD - Add detailed description"
}

# Create module coordinator from template
Write-Host "Creating module coordinator..."
$subModuleTemplate = Get-Content "$TemplateDir/SubModuleTemplate.java" -Raw
$moduleContent = Replace-Placeholders -Content $subModuleTemplate -Replacements $replacements
$moduleContent | Out-File -FilePath "$ModuleDir/${ModuleNameCapitalized}Module.java" -Encoding UTF8

Write-Host "Module created successfully!"
Write-Host "Location: $ModuleDir"
Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Update the module coordinator with specific implementation details"
Write-Host "2. Add implementation classes as needed"
Write-Host "3. Update the Phase$PhaseNumber coordinator to reference this module"
Write-Host "4. Add module configuration to Phase${PhaseNumber}Config"
Write-Host "5. Register the module in the ModuleLoader"
Write-Host ""
Write-Host "Files created:"
Write-Host "- ${ModuleNameCapitalized}Module.java"
Write-Host ""
Write-Host "Remember to:"
Write-Host "- Replace TODO comments with actual implementation"
Write-Host "- Update placeholder values with real information"
Write-Host "- Add proper error handling and validation"
Write-Host "- Write tests for the new module"
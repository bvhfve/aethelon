package com.bvhfve.aethelon.core.config.migration;

/**
 * ConfigMigrationException - Exception thrown during configuration migration
 * 
 * This exception is thrown when configuration migration fails for any reason,
 * including validation failures, data corruption, or unsupported operations.
 */
public class ConfigMigrationException extends Exception {
    
    private final String migrationName;
    private final String fromVersion;
    private final String toVersion;
    
    public ConfigMigrationException(String message) {
        super(message);
        this.migrationName = null;
        this.fromVersion = null;
        this.toVersion = null;
    }
    
    public ConfigMigrationException(String message, Throwable cause) {
        super(message, cause);
        this.migrationName = null;
        this.fromVersion = null;
        this.toVersion = null;
    }
    
    public ConfigMigrationException(String message, String migrationName, String fromVersion, String toVersion) {
        super(message);
        this.migrationName = migrationName;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }
    
    public ConfigMigrationException(String message, Throwable cause, String migrationName, String fromVersion, String toVersion) {
        super(message, cause);
        this.migrationName = migrationName;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }
    
    public String getMigrationName() {
        return migrationName;
    }
    
    public String getFromVersion() {
        return fromVersion;
    }
    
    public String getToVersion() {
        return toVersion;
    }
    
    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(super.getMessage());
        
        if (migrationName != null) {
            message.append(" (Migration: ").append(migrationName).append(")");
        }
        
        if (fromVersion != null && toVersion != null) {
            message.append(" (").append(fromVersion).append(" -> ").append(toVersion).append(")");
        }
        
        return message.toString();
    }
}
package com.bvhfve.aethelon.core.config.migration;

import com.bvhfve.aethelon.core.AethelonCore;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ConfigBackupManager - Manages configuration backups and restoration
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: FabricLoader for config directory access
 * - Hooks into: Configuration system
 * - Modifies: Backup files only
 * 
 * MODULE ROLE:
 * - Purpose: Create, manage, and restore configuration backups
 * - Dependencies: None (utility class)
 * - Provides: Backup creation, restoration, cleanup, listing
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (uses Fabric Loader APIs)
 * - Fabric API: Any (uses stable Fabric Loader)
 * - Breaking changes: Backup format may evolve
 * 
 * BACKUP STRATEGY:
 * - Automatic backups before migrations
 * - Manual backup creation
 * - Configurable retention policy
 * - Backup validation and integrity checking
 */
public class ConfigBackupManager {
    
    private static final String BACKUP_DIR_NAME = "backups";
    private static final String BACKUP_PREFIX = "aethelon_backup_";
    private static final String BACKUP_EXTENSION = ".json";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private final Path configDir;
    private final Path backupDir;
    private final String configFileName;
    
    public ConfigBackupManager(String configFileName) {
        this.configFileName = configFileName;
        this.configDir = FabricLoader.getInstance().getConfigDir();
        this.backupDir = configDir.resolve(BACKUP_DIR_NAME);
    }
    
    /**
     * Create a backup of the current configuration
     * 
     * @param reason Reason for creating the backup
     * @return Backup information or null if failed
     */
    public BackupInfo createBackup(String reason) {
        try {
            Path configPath = configDir.resolve(configFileName);
            
            if (!Files.exists(configPath)) {
                AethelonCore.LOGGER.warn("Cannot create backup: configuration file does not exist");
                return null;
            }
            
            // Ensure backup directory exists
            Files.createDirectories(backupDir);
            
            // Generate backup filename
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String backupFileName = BACKUP_PREFIX + timestamp + BACKUP_EXTENSION;
            Path backupPath = backupDir.resolve(backupFileName);
            
            // Copy configuration to backup
            Files.copy(configPath, backupPath);
            
            // Create backup info
            BackupInfo backupInfo = new BackupInfo(
                backupPath,
                LocalDateTime.now(),
                reason,
                Files.size(configPath)
            );
            
            AethelonCore.LOGGER.info("Created configuration backup: {} ({})", backupPath.getFileName(), reason);
            
            // Clean up old backups
            cleanupOldBackups();
            
            return backupInfo;
            
        } catch (IOException e) {
            AethelonCore.LOGGER.error("Failed to create configuration backup", e);
            return null;
        }
    }
    
    /**
     * Restore configuration from a backup
     * 
     * @param backupInfo Backup to restore from
     * @return true if restoration was successful
     */
    public boolean restoreFromBackup(BackupInfo backupInfo) {
        try {
            Path configPath = configDir.resolve(configFileName);
            
            if (!Files.exists(backupInfo.getPath())) {
                AethelonCore.LOGGER.error("Backup file does not exist: {}", backupInfo.getPath());
                return false;
            }
            
            // Create a backup of current config before restoring
            createBackup("Before restoration from " + backupInfo.getPath().getFileName());
            
            // Copy backup to config location
            Files.copy(backupInfo.getPath(), configPath, 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            AethelonCore.LOGGER.info("Restored configuration from backup: {}", backupInfo.getPath().getFileName());
            return true;
            
        } catch (IOException e) {
            AethelonCore.LOGGER.error("Failed to restore configuration from backup", e);
            return false;
        }
    }
    
    /**
     * Get list of available backups
     * 
     * @return List of backup information, sorted by creation time (newest first)
     */
    public List<BackupInfo> getAvailableBackups() {
        try {
            if (!Files.exists(backupDir)) {
                return new ArrayList<>();
            }
            
            return Files.list(backupDir)
                .filter(path -> path.getFileName().toString().startsWith(BACKUP_PREFIX))
                .filter(path -> path.getFileName().toString().endsWith(BACKUP_EXTENSION))
                .map(this::createBackupInfo)
                .filter(info -> info != null)
                .sorted(Comparator.comparing(BackupInfo::getCreationTime).reversed())
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            AethelonCore.LOGGER.error("Failed to list available backups", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Delete a specific backup
     * 
     * @param backupInfo Backup to delete
     * @return true if deletion was successful
     */
    public boolean deleteBackup(BackupInfo backupInfo) {
        try {
            Files.deleteIfExists(backupInfo.getPath());
            AethelonCore.LOGGER.info("Deleted backup: {}", backupInfo.getPath().getFileName());
            return true;
            
        } catch (IOException e) {
            AethelonCore.LOGGER.error("Failed to delete backup: {}", backupInfo.getPath(), e);
            return false;
        }
    }
    
    /**
     * Clean up old backups based on retention policy
     */
    public void cleanupOldBackups() {
        try {
            List<BackupInfo> backups = getAvailableBackups();
            
            // Keep last 10 backups
            int maxBackups = 10;
            if (backups.size() > maxBackups) {
                List<BackupInfo> toDelete = backups.subList(maxBackups, backups.size());
                
                for (BackupInfo backup : toDelete) {
                    deleteBackup(backup);
                }
                
                AethelonCore.LOGGER.info("Cleaned up {} old backups", toDelete.size());
            }
            
            // Delete backups older than 30 days
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            List<BackupInfo> oldBackups = backups.stream()
                .filter(backup -> backup.getCreationTime().isBefore(cutoff))
                .collect(Collectors.toList());
            
            for (BackupInfo backup : oldBackups) {
                deleteBackup(backup);
            }
            
            if (!oldBackups.isEmpty()) {
                AethelonCore.LOGGER.info("Cleaned up {} backups older than 30 days", oldBackups.size());
            }
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to clean up old backups", e);
        }
    }
    
    /**
     * Validate a backup file
     * 
     * @param backupInfo Backup to validate
     * @return true if backup is valid
     */
    public boolean validateBackup(BackupInfo backupInfo) {
        try {
            if (!Files.exists(backupInfo.getPath())) {
                return false;
            }
            
            // Check file size
            long currentSize = Files.size(backupInfo.getPath());
            if (currentSize != backupInfo.getFileSize()) {
                AethelonCore.LOGGER.warn("Backup file size mismatch: expected {}, actual {}", 
                    backupInfo.getFileSize(), currentSize);
                return false;
            }
            
            // Try to parse as JSON
            String content = Files.readString(backupInfo.getPath());
            com.google.gson.JsonParser.parseString(content);
            
            return true;
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Backup validation failed: {}", backupInfo.getPath(), e);
            return false;
        }
    }
    
    /**
     * Get backup directory path
     * 
     * @return Path to backup directory
     */
    public Path getBackupDirectory() {
        return backupDir;
    }
    
    /**
     * Create backup info from file path
     */
    private BackupInfo createBackupInfo(Path backupPath) {
        try {
            String fileName = backupPath.getFileName().toString();
            
            // Extract timestamp from filename
            String timestampStr = fileName
                .replace(BACKUP_PREFIX, "")
                .replace(BACKUP_EXTENSION, "");
            
            LocalDateTime creationTime = LocalDateTime.parse(timestampStr, TIMESTAMP_FORMAT);
            long fileSize = Files.size(backupPath);
            
            return new BackupInfo(backupPath, creationTime, "Unknown", fileSize);
            
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to create backup info for: {}", backupPath, e);
            return null;
        }
    }
    
    /**
     * Information about a configuration backup
     */
    public static class BackupInfo {
        private final Path path;
        private final LocalDateTime creationTime;
        private final String reason;
        private final long fileSize;
        
        public BackupInfo(Path path, LocalDateTime creationTime, String reason, long fileSize) {
            this.path = path;
            this.creationTime = creationTime;
            this.reason = reason;
            this.fileSize = fileSize;
        }
        
        public Path getPath() { return path; }
        public LocalDateTime getCreationTime() { return creationTime; }
        public String getReason() { return reason; }
        public long getFileSize() { return fileSize; }
        
        public String getDisplayName() {
            return path.getFileName().toString();
        }
        
        public String getFormattedCreationTime() {
            return creationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        @Override
        public String toString() {
            return String.format("Backup{name=%s, time=%s, reason=%s, size=%d bytes}", 
                getDisplayName(), getFormattedCreationTime(), reason, fileSize);
        }
    }
}
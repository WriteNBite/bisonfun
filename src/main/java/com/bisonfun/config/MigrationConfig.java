package com.bisonfun.config;

import com.bisonfun.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MigrationConfig {

    private final EntityMigrationService entityMigrationService;
    private final UserListsMigrationService userListsMigrationService;

    @Autowired
    public MigrationConfig(EntityMigrationService entityMigrationService, UserListsMigrationService userListsMigrationService) {
        this.entityMigrationService = entityMigrationService;
        this.userListsMigrationService = userListsMigrationService;
    }

    @PostConstruct
    public void migrateData(){
        entityMigrationService.migrateOldEntityToVideoContent();
        userListsMigrationService.migrateOldUserListsToUserVideoContent();
    }
}

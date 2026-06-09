package com.healthstep.controller;

import com.healthstep.service.WearableSyncService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/wearables")
public class WearableSyncController {

    private final WearableSyncService wearableSyncService;

    public WearableSyncController(WearableSyncService wearableSyncService) {
        this.wearableSyncService = wearableSyncService;
    }

    @PostMapping("/{userId}/sync")
    public Map<String, String> syncWearable(@PathVariable Long userId, @RequestParam(defaultValue = "apple") String source) {
        wearableSyncService.syncMockData(userId, source);
        return Collections.singletonMap("status", "success");
    }
}

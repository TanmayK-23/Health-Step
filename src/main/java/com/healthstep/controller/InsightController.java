package com.healthstep.controller;

import com.healthstep.service.InsightService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping("/{userId}")
    public Map<String, String> getInsight(@PathVariable Long userId) {
        String insight = insightService.generateDailyInsight(userId);
        return Collections.singletonMap("insight", insight);
    }
}

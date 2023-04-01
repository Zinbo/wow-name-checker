package com.stacktobasics.wownamechecker.alert.api;

import com.stacktobasics.wownamechecker.alert.infra.AlertScheduler;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alert")
public class AlertController {

    private final AlertScheduler alertScheduler;
    private final AlertService alertService;

    public AlertController(AlertScheduler alertScheduler, AlertService alertService) {
        this.alertScheduler = alertScheduler;
        this.alertService = alertService;
    }

    @PostMapping
    public void addAlert(@NotNull @RequestBody AlertDTO alertDTO) {
        alertService.addAlert(alertDTO.email(), alertDTO.character(), alertDTO.realm(), alertDTO.region());
    }

    @GetMapping("/trigger-scheduler")
    public void scheduler() {
        alertScheduler.checkProfiles();
    }
}
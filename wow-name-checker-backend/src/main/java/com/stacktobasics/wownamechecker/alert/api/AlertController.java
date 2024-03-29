package com.stacktobasics.wownamechecker.alert.api;

import com.stacktobasics.wownamechecker.alert.infra.AlertScheduler;
import com.stacktobasics.wownamechecker.alert.service.AlertService;
import com.stacktobasics.wownamechecker.profile.domain.Character;
import jakarta.validation.Valid;
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
    public void addAlert(@Valid @NotNull @RequestBody AlertDTO alertDTO) {
        Character character = new Character(alertDTO.character(), alertDTO.realm(), alertDTO.region());
        alertService.addAlert(alertDTO.email(), character);
    }
    @PostMapping("/unsubscribe")
    public void unsubscribe(@Valid @NotNull @RequestBody UnsubscribeDTO unsubscribeDTO) {
        alertService.unsubscribe(unsubscribeDTO.email());
    }

    @PostMapping("/trigger-scheduler")
    public void scheduler() {
        alertScheduler.checkProfiles();
    }
}

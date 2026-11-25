package kigali.clinc.clinicmanagement.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.service.DashboardService;

@RestController
@RequestMapping(value = "/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // D4
    @GetMapping("/summary")
    public ResponseEntity<?> summary() {
        Map<String, Long> summary = dashboardService.summary();
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
}

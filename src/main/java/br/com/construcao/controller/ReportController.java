package br.com.construcao.controller;

import br.com.construcao.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    @Autowired
    private final ReportService reportService;

    @GetMapping("/week/report")
    public ResponseEntity<String> weeklyReport(JwtAuthenticationToken auth) {
        String key = reportService.generateWeeklyReportAndUpload();
        return ResponseEntity.ok(key);
    }
}

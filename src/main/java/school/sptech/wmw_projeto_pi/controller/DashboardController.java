package school.sptech.wmw_projeto_pi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.wmw_projeto_pi.dto.dashboard.DashboardResponseDto;
import school.sptech.wmw_projeto_pi.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/indicadores")
    public ResponseEntity<DashboardResponseDto> getIndicadores() {
        return ResponseEntity.ok(dashboardService.getIndicadores());
    }
}

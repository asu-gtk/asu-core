package kg.asugtk.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.asugtk.common.dto.OptimizationRequestDTO;
import kg.asugtk.common.dto.OptimizationResultDTO;
import kg.asugtk.math.SimplexFleetOptimizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/optimization")
@Tag(name = "Planning & Optimization", description = "Методы математической оптимизации сменно-суточного плана")
@CrossOrigin(origins = "*")
public class OptimizationController {

    private final SimplexFleetOptimizer optimizer;

    @Autowired
    public OptimizationController(SimplexFleetOptimizer optimizer) {
        this.optimizer = optimizer;
    }

    @PostMapping("/shift")
    @Operation(summary = "Расчет оптимального прикрепления самосвалов к забоям экскаваторов")
    public ResponseEntity<OptimizationResultDTO> optimizeShift(@RequestBody OptimizationRequestDTO request) {
        OptimizationResultDTO result = optimizer.optimizeShiftAllocation(request);
        return ResponseEntity.ok(result);
    }
}

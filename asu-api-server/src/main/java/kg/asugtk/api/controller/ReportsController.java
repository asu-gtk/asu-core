package kg.asugtk.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.asugtk.common.dto.TripRecordDTO;
import kg.asugtk.reports.service.ShiftReportExcelGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "*")
@Tag(name = "Reports & Analytics", description = "Генерация сменных рапортов и выгрузка Excel")
public class ReportsController {

    private final ShiftReportExcelGenerator excelGenerator;

    public ReportsController(ShiftReportExcelGenerator excelGenerator) {
        this.excelGenerator = excelGenerator;
    }

    @GetMapping("/shift/xlsx")
    @Operation(summary = "Выгрузка сводного сменного отчета в формате Excel (.xlsx)")
    public ResponseEntity<byte[]> downloadShiftReport() {
        try {
            // В production данные берутся из TelemetryRepository за смену.
            // Здесь возвращаем пустой отчёт (demo scaffold).
            byte[] excelBytes = excelGenerator.generateShiftReportXlsx(List.of());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=ASU_GTK_Shift_Report.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

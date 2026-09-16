package kg.asugtk.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.asugtk.common.dto.TripRecordDTO;
import kg.asugtk.common.model.TypeOfWork;
import kg.asugtk.reports.service.ExcelReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports & Analytics", description = "Генерация сменных и сводных Excel/PDF отчетов")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ExcelReportGenerator excelGenerator;

    @Autowired
    public ReportController(ExcelReportGenerator excelGenerator) {
        this.excelGenerator = excelGenerator;
    }

    @GetMapping(value = "/shift/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Выгрузка сменного рапорта работы парка в формате Excel (.xlsx)")
    public ResponseEntity<byte[]> downloadShiftReportXlsx() throws IOException {
        // Тестовый набор данных для демонстрации выгрузки
        List<TripRecordDTO> sampleTrips = List.of(
                TripRecordDTO.builder()
                        .tripId("TRIP-101-01")
                        .truckCode("TRUCK-101")
                        .truckModel("БелАЗ-75131")
                        .truckPayloadRatedTons(130.0)
                        .excavatorCode("EXC-02")
                        .excavatorBucketVolumeM3(5.0)
                        .typeOfWork(TypeOfWork.ORE)
                        .tripDistanceKm(3.2)
                        .actualWeightTons(131.5)
                        .fuelAtLoadingLiters(420.0)
                        .fuelAtUnloadingLiters(405.0)
                        .returnFuelConsumedLiters(9.0)
                        .arrivalLoadingTime(Instant.now().minusSeconds(1800))
                        .finishLoadingTime(Instant.now().minusSeconds(1500))
                        .finishUnloadingTime(Instant.now())
                        .build()
        );

        byte[] xlsxBytes = excelGenerator.generateShiftReportXlsx("Сменный рапорт", sampleTrips);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=shift_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsxBytes);
    }
}

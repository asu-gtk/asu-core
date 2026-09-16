package kg.asugtk.reports.service;

import kg.asugtk.common.dto.CostAnalysisResult;
import kg.asugtk.common.dto.TripRecordDTO;
import kg.asugtk.math.HaulageCostCalculator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

@Service
public class ExcelReportGenerator {

    private final HaulageCostCalculator costCalculator;

    @Autowired
    public ExcelReportGenerator(HaulageCostCalculator costCalculator) {
        this.costCalculator = costCalculator;
    }

    public byte[] generateShiftReportXlsx(String reportTitle, Collection<TripRecordDTO> trips) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Сменный рапорт");

            // Стили
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // Заголовки таблицы
            String[] headers = {
                    "№ Рейса", "Самосвал", "Экскаватор", "Вид работ",
                    "Расстояние (км)", "Вес руды (т)", "Расход ГСМ (л)",
                    "Затраты ГСМ (руб)", "Затраты ТО (руб)", "Затраты экск. (руб)",
                    "Всего затрат (руб)", "Себестоимость (руб/т*км)"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (TripRecordDTO trip : trips) {
                CostAnalysisResult cost = costCalculator.calculateSingleTripCost(trip, 50.0);
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(trip.getTripId() != null ? trip.getTripId() : String.valueOf(rowIdx));
                row.createCell(1).setCellValue(trip.getTruckCode());
                row.createCell(2).setCellValue(trip.getExcavatorCode());
                row.createCell(3).setCellValue(trip.getTypeOfWork() != null ? trip.getTypeOfWork().getDescription() : "-");
                row.createCell(4).setCellValue(trip.getTripDistanceKm());
                row.createCell(5).setCellValue(trip.getActualWeightTons());
                row.createCell(6).setCellValue(cost.getFuelConsumedLiters());
                row.createCell(7).setCellValue(cost.getFuelCostRubles());
                row.createCell(8).setCellValue(cost.getVehicleReadinessCostRubles());
                row.createCell(9).setCellValue(cost.getExcavatorOperationCostRubles());
                row.createCell(10).setCellValue(cost.getTotalCostRubles());
                row.createCell(11).setCellValue(cost.getCostPerTonKm());
            }

            // Авторазмер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}

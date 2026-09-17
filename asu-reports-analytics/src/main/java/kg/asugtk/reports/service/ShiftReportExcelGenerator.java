package kg.asugtk.reports.service;

import kg.asugtk.common.dto.TripRecordDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Генератор официальных сменных рапортов ГТК в формате Excel (.xlsx).
 * Поля TripRecordDTO: truckCode, truckModel, excavatorCode, unloadPointCode,
 * typeOfWork, arrivalLoadingTime, tripDistanceKm, actualWeightTons,
 * fuelAtLoadingLiters, fuelAtUnloadingLiters.
 */
@Service
public class ShiftReportExcelGenerator {

    public byte[] generateShiftReportXlsx(List<TripRecordDTO> trips) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Сменный рапорт ГТК");

            // Стили
            Font boldBlue = wb.createFont();
            boldBlue.setBold(true);
            boldBlue.setFontHeightInPoints((short) 13);
            boldBlue.setColor(IndexedColors.DARK_BLUE.getIndex());
            CellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFont(boldBlue);

            Font boldWhite = wb.createFont();
            boldWhite.setBold(true);
            boldWhite.setColor(IndexedColors.WHITE.getIndex());
            CellStyle hdrStyle = wb.createCellStyle();
            hdrStyle.setFont(boldWhite);
            hdrStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            hdrStyle.setAlignment(HorizontalAlignment.CENTER);
            hdrStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle cell = wb.createCellStyle();
            cell.setBorderBottom(BorderStyle.THIN);
            cell.setBorderLeft(BorderStyle.THIN);
            cell.setBorderRight(BorderStyle.THIN);

            CellStyle numCell = wb.createCellStyle();
            numCell.cloneStyleFrom(cell);
            numCell.setAlignment(HorizontalAlignment.RIGHT);

            // Заголовок
            Row r0 = sheet.createRow(0);
            Cell t = r0.createCell(0);
            t.setCellValue("АСУ ГТК  •  СВОДНЫЙ СМЕННЫЙ РАПОРТ АВТОСАМОСВАЛОВ");
            t.setCellStyle(titleStyle);

            sheet.createRow(1).createCell(0).setCellValue(
                "Дата: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

            // Шапка таблицы
            String[] headers = {
                "№", "Борт", "Модель", "Экскаватор", "Пункт выгрузки",
                "Вид работы", "Время прибытия", "Плечо (км)",
                "Груз (т)", "Расход ГСМ (л)", "т·км"
            };
            Row hRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(hdrStyle);
            }

            int ri = 4;
            double totWeight = 0, totFuel = 0, totTkm = 0;

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM HH:mm");

            for (int i = 0; i < trips.size(); i++) {
                TripRecordDTO dto = trips.get(i);
                Row row = sheet.createRow(ri++);

                double fuel = Math.max(0, dto.getFuelAtLoadingLiters() - dto.getFuelAtUnloadingLiters());
                double tkm  = dto.getActualWeightTons() * dto.getTripDistanceKm();
                totWeight += dto.getActualWeightTons();
                totFuel   += fuel;
                totTkm    += tkm;

                String arrivalStr = dto.getArrivalLoadingTime() != null
                        ? LocalDateTime.ofInstant(dto.getArrivalLoadingTime(), ZoneId.systemDefault()).format(dtf)
                        : "-";

                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(dto.getTruckCode());
                row.createCell(2).setCellValue(dto.getTruckModel());
                row.createCell(3).setCellValue(dto.getExcavatorCode());
                row.createCell(4).setCellValue(dto.getUnloadPointCode());
                row.createCell(5).setCellValue(dto.getTypeOfWork() != null ? dto.getTypeOfWork().name() : "-");
                row.createCell(6).setCellValue(arrivalStr);
                row.createCell(7).setCellValue(round(dto.getTripDistanceKm()));
                row.createCell(8).setCellValue(round(dto.getActualWeightTons()));
                row.createCell(9).setCellValue(round(fuel));
                row.createCell(10).setCellValue(round(tkm));

                for (int c2 = 0; c2 < headers.length; c2++) {
                    row.getCell(c2).setCellStyle(c2 >= 7 ? numCell : cell);
                }
            }

            // Итог
            Row tot = sheet.createRow(ri + 1);
            Cell totLbl = tot.createCell(0);
            totLbl.setCellValue("ИТОГО");
            totLbl.setCellStyle(hdrStyle);
            tot.createCell(8).setCellValue(round(totWeight));
            tot.createCell(9).setCellValue(round(totFuel));
            tot.createCell(10).setCellValue(round(totTkm));

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        }
    }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}

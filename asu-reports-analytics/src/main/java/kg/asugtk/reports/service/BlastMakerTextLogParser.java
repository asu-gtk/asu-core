package kg.asugtk.reports.service;

import kg.asugtk.common.dto.TripRecordDTO;
import kg.asugtk.common.model.TypeOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlastMakerTextLogParser {

    private static final Logger log = LoggerFactory.getLogger(BlastMakerTextLogParser.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");

    /**
     * Парсинг сменного журнала рейсов BlastMaker (Tab-delimited text)
     */
    public List<TripRecordDTO> parseLogStream(InputStream inputStream) {
        List<TripRecordDTO> trips = new ArrayList<>();
        if (inputStream == null) {
            return trips;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                String[] parts = line.split("\t");
                if (parts.length < 18) {
                    continue;
                }

                try {
                    String truckCode = "TRUCK-" + parts[0].trim();
                    String truckModel = parts[2].trim();
                    String excavatorCode = parts[4].trim();
                    
                    LocalDateTime loadStart = parseDateTime(parts[6].trim());
                    LocalDateTime loadEnd = parseDateTime(parts[7].trim());
                    LocalDateTime unloadStart = parseDateTime(parts[14].trim());

                    double distanceKm = parseDouble(parts[9].trim());
                    double actualWeight = parseDouble(parts[10].trim());
                    double ratedWeight = parseDouble(parts[11].trim());

                    TypeOfWork workType = parseTypeOfWork(parts[12].trim());
                    String unloadPoint = parts[13].trim();

                    double fuelStart = parseDouble(parts[16].trim());
                    double fuelEnd = parseDouble(parts[17].trim());

                    TripRecordDTO dto = TripRecordDTO.builder()
                            .tripId(truckCode + "-L" + lineNumber)
                            .truckCode(truckCode)
                            .truckModel(truckModel)
                            .truckPayloadRatedTons(ratedWeight > 0 ? ratedWeight : 130.0)
                            .truckWeightEmptyTons(107.0)
                            .excavatorCode(excavatorCode)
                            .excavatorBucketVolumeM3(5.0)
                            .unloadPointCode(unloadPoint)
                            .typeOfWork(workType)
                            .arrivalLoadingTime(loadStart != null ? loadStart.toInstant(ZoneOffset.UTC) : null)
                            .finishLoadingTime(loadEnd != null ? loadEnd.toInstant(ZoneOffset.UTC) : null)
                            .beginUnloadingTime(unloadStart != null ? unloadStart.toInstant(ZoneOffset.UTC) : null)
                            .tripDistanceKm(distanceKm)
                            .actualWeightTons(actualWeight)
                            .fuelAtLoadingLiters(fuelStart)
                            .fuelAtUnloadingLiters(fuelEnd)
                            .build();

                    trips.add(dto);
                } catch (Exception e) {
                    log.warn("Error parsing line #{}: {}", lineNumber, line, e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to read log stream", e);
        }

        return trips;
    }

    private LocalDateTime parseDateTime(String text) {
        try {
            return LocalDateTime.parse(text, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private double parseDouble(String text) {
        try {
            return Double.parseDouble(text.replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private TypeOfWork parseTypeOfWork(String text) {
        if (text.toLowerCase().contains("добыча")) {
            return TypeOfWork.ORE;
        } else if (text.toLowerCase().contains("вскрыша")) {
            return TypeOfWork.WASTE;
        } else {
            return TypeOfWork.TRANSIT;
        }
    }
}

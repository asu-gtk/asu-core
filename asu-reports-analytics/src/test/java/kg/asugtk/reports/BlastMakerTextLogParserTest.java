package kg.asugtk.reports;

import kg.asugtk.common.dto.TripRecordDTO;
import kg.asugtk.common.model.TypeOfWork;
import kg.asugtk.reports.service.BlastMakerTextLogParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BlastMakerTextLogParserTest {

    private final BlastMakerTextLogParser parser = new BlastMakerTextLogParser();

    @Test
    @DisplayName("Проверка парсинга реального сменного лога text.txt")
    void testParseTextLog() {
        InputStream is = getClass().getResourceAsStream("/text.txt");
        assertThat(is).isNotNull();

        List<TripRecordDTO> trips = parser.parseLogStream(is);

        assertThat(trips).isNotEmpty();
        assertThat(trips.size()).isGreaterThan(100);

        TripRecordDTO firstTrip = trips.get(0);
        assertThat(firstTrip.getTruckCode()).isEqualTo("TRUCK-133");
        assertThat(firstTrip.getTruckModel()).contains("БелАЗ-75131");
        assertThat(firstTrip.getExcavatorCode()).isEqualTo("20292№2");
        assertThat(firstTrip.getTypeOfWork()).isEqualTo(TypeOfWork.ORE);
        assertThat(firstTrip.getTripDistanceKm()).isEqualTo(15.09);
        assertThat(firstTrip.getActualWeightTons()).isEqualTo(100.0);
        assertThat(firstTrip.getFuelAtLoadingLiters()).isEqualTo(1437.0);
        assertThat(firstTrip.getFuelAtUnloadingLiters()).isEqualTo(1355.0);
    }
}

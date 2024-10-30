package cricinfo.criketinfo.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchInfoDTO implements Comparable<MatchInfoDTO> {
    String dateTime;
    String teams;
    String startTime;
    String matchLink;
    

    @Override
    public String toString() {
        return "MatchInfo{" +
                "dateTime='" + dateTime + '\'' +
                ", teams='" + teams + '\'' +
                ", startTime='" + startTime + '\'' +
                ", matchLink='" + matchLink + '\'' +
                '}';
    }

    @Override
    public int compareTo(MatchInfoDTO arg0) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime1 = LocalDateTime.parse(this.dateTime, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(arg0.dateTime, formatter);
        if (dateTime1.isAfter(dateTime2)) {
            return 1;
        }return -1;
    }

}
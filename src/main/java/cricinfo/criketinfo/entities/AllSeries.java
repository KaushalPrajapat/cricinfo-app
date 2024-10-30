package cricinfo.criketinfo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "cricinfo_allseries_table")
public class AllSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seriesId;
    private String seriesTitle;
    private String seriesLink;
    private String seriesTimings;
    private String seriesPointTableLink;
}

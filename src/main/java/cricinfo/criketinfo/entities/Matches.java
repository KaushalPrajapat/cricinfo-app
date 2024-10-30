package cricinfo.criketinfo.entities;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "cricinfo_match_table")
public class Matches {


    private String teamHeading;
    private String matchNumberVenue;

    private String battingTeam;

    private String battingTeamScore;

    private String bowlTeam;
    private String bowlTeamScore;

    private String liveText;

    private String matchLink;

    private String textComplete;

    private MatchStatus status;

    private Instant dateTime;

    private Date date=new Date();

    public void setMatchStatus() {
        if (this.textComplete.contains("Stumps")) {
            this.status = MatchStatus.STUMPUS;
        } else if(this.textComplete.contains("won")){
            this.status = MatchStatus.COMPLETED;
        }else {
            this.status = MatchStatus.LIVE;
        }
    }
}

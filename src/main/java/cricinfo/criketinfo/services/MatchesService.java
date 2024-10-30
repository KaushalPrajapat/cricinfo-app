package cricinfo.criketinfo.services;

import java.util.List;

import cricinfo.criketinfo.entities.AllSeries;
import cricinfo.criketinfo.entities.MatchInfoDTO;
import cricinfo.criketinfo.entities.Matches;

public interface MatchesService {

    public List<Matches> getAllMatchesCompleted(); // Only Live Matches //

    public List<Matches> getAllLiveMatches(); // Only live matches // used

    public List<Matches> getAllMatches(); // All matches from database // used

    public List<List<String>> getPointTable(); // Serve Point Table

    public List<AllSeries> getAllSeries();

    public List<List<String>> getPointTableById(int seriesId);

    public String getSeriesById(int seriesId);

    public List<MatchInfoDTO> getSeriesSchedule(int seriesId);
}

package cricinfo.criketinfo.services.Impl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cricinfo.criketinfo.entities.AllSeries;
import cricinfo.criketinfo.entities.MatchInfoDTO;
import cricinfo.criketinfo.entities.MatchStatus;
import cricinfo.criketinfo.entities.Matches;
import cricinfo.criketinfo.reposirotry.AllSeriesRepo;
import cricinfo.criketinfo.reposirotry.MatchesRepo;
import cricinfo.criketinfo.services.MatchesService;

@Service
public class MatchesServiceImpl implements MatchesService {
    int frontEndLength = 40;
    @Autowired
    private MatchesRepo matchesRepo;

    @Autowired
    private AllSeriesRepo seriesRepo;

    @Autowired

    @SuppressWarnings("unused")
    private void upcomingSeries() {
        String url = "https://www.cricbuzz.com/cricket-schedule/series/all";

        try {
            Document doc = Jsoup.connect(url).get();
            Elements divs = doc.select("div.cb-col.cb-col-90");
            for (Element div : divs) {
                AllSeries series = new AllSeries();
                Element linkTag = div.selectFirst("a[href]");

                if (linkTag != null) {
                    String seriesLink = linkTag.attr("href");
                    String title = div.text().trim();

                    // Check if the href matches the desired pattern
                    if (seriesLink.matches("^/cricket-series/.+")) {
                        series.setSeriesLink("https://www.cricbuzz.com" + seriesLink); // setting up series schedule
                                                                                       // Link
                        // ------------------

                        if (series.getSeriesLink() == null)
                            continue;
                        String urlpt = series.getSeriesLink().substring(0, series.getSeriesLink().length() - 7)
                                + "points-table";

                        try (CloseableHttpClient client = HttpClients.createDefault()) {
                            HttpGet request = new HttpGet(url);
                            HttpResponse response = client.execute(request);

                            if (response.getStatusLine().getStatusCode() == 200) {
                                series.setSeriesPointTableLink(urlpt);
                            }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }

                        // -------------------

                        series.setSeriesTitle(title);
                        String dateBw = title.substring(title.length() - 15, title.length() - 9) + " to "
                                + title.substring(title.length() - 7, title.length());
                        series.setSeriesTimings(dateBw);
                    }
                }
                Optional<AllSeries> match = seriesRepo.findBySeriesTitle(series.getSeriesTitle());

                if (!match.isPresent()) {
                    seriesRepo.save(series);
                }

            }

        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    @Override
    public List<AllSeries> getAllSeries() {
        // Update series If any new came
        // upcomingSeries();
        List<AllSeries> lst = seriesRepo.findAll();
        return lst;
    }

    private void updateList() {
        // Web Scapping, Mujhe to aati nahi
        try {
            String url = "https://www.cricbuzz.com/cricket-match/live-scores";
            Document document = Jsoup.connect(url).get();

            Elements liveScoreElements = document.select("div.cb-mtch-lst.cb-tms-itm");
            for (Element match : liveScoreElements) {
                String teamsHeading = match.select("h3.cb-lv-scr-mtch-hdr").select("a").text();
                String matchNumberVenue = match.select("span").text();
                Elements matchBatTeamInfo = match.select("div.cb-hmscg-bat-txt");
                String battingTeam = matchBatTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String score = matchBatTeamInfo.select("div.cb-hmscg-tm-nm+div").text();
                Elements bowlTeamInfo = match.select("div.cb-hmscg-bwl-txt");
                String bowlTeam = bowlTeamInfo.select("div.cb-hmscg-tm-nm").text();
                String bowlTeamScore = bowlTeamInfo.select("div.cb-hmscg-tm-nm+div").text();
                String textLive = match.select("div.cb-text-live").text();
                String textComplete = match.select("div.cb-text-complete").text();
                // getting match link
                String matchLink = "https://www.cricbuzz.com"
                        + match.select("a.cb-lv-scrs-well.cb-lv-scrs-well-live").attr("href");
                
                // System.out.println(matchLink);
// 
                Matches match1 = new Matches();
                match1.setTeamHeading(teamsHeading);
                match1.setMatchNumberVenue(matchNumberVenue);
                match1.setBattingTeam(battingTeam);
                match1.setBattingTeamScore(score);
                match1.setBowlTeam(bowlTeam);
                match1.setBowlTeamScore(bowlTeamScore);
                match1.setLiveText(textLive);
                match1.setMatchLink(matchLink);
                match1.setTextComplete(textComplete);
                match1.setMatchStatus();
                match1.setDateTime(Instant.now());

                // update the match in database
                updateMatch(match1);

            }
        } catch (IOException e) {
        }
    }

    @Override
    public List<Matches> getAllMatches() {
        // System.out.println(getMatches());
        updateList();
        List<Matches> lst = matchesRepo.findAll();
        List<Matches> lst1 = new ArrayList<>();
        for (Matches match : lst) {
            // if (match.getStatus() == MatchStatus.COMPLETED) {
            if (match.getMatchNumberVenue().length() > frontEndLength)
                match.setMatchNumberVenue(match.getMatchNumberVenue().substring(0, frontEndLength) + "..");
            lst1.add(match);
            // }
        }
        return lst1;
    }

    @Override
    public List<Matches> getAllMatchesCompleted() {
        updateList();
        List<Matches> lst = getAllMatches();
        List<Matches> lst1 = new ArrayList<>();
        for (Matches match : lst) {
            if ((match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.DRAWN)
                    || (match.getDateTime() == null
                            && match.getDateTime().isBefore(Instant.now().minusSeconds(60 * 60 * 24 * 2)))) {
                if (match.getMatchNumberVenue().length() > frontEndLength)
                    match.setMatchNumberVenue(match.getMatchNumberVenue().substring(0, frontEndLength) + "..");
                lst1.add(match);
            }
        }
        return lst1;
    }

    private void updateMatch(Matches match1) {
        Optional<Matches> match = matchesRepo.findByTeamHeading(match1.getTeamHeading());

        if (match.isPresent()) {
            match1.setTeamHeading((match.get().getTeamHeading()));
            match1.setDateTime(match.get().getDateTime());
            matchesRepo.deleteByTeamHeading(match1.getTeamHeading());
            matchesRepo.save(match1);
        } else {
            matchesRepo.save(match1);
        }
    }

    @Override
    public List<Matches> getAllLiveMatches() {
        updateList();
        List<Matches> lst = getAllMatches();
        List<Matches> lst1 = new ArrayList<>();
        for (Matches match : lst) {
            if (match.getStatus() == MatchStatus.LIVE && !match.getLiveText().isEmpty()) {
                if (match.getMatchNumberVenue().length() > frontEndLength)
                    match.setMatchNumberVenue(match.getMatchNumberVenue().substring(0, frontEndLength) + "..");
                lst1.add(match);
            }
        }
        return lst1;
    }


    // Not using anywhere
    @Override
    public List<List<String>> getPointTable() {
        List<List<String>> pointTable = new ArrayList<>();
        String tableURL = "https://www.cricbuzz.com/cricket-series/7572/icc-cricket-world-cup-league-two-2023-27/points-table";
        // String tableURL =
        // "https://www.cricbuzz.com/cricket-series/6732/icc-cricket-world-cup-2023/points-table";
        try {
            Document document = Jsoup.connect(tableURL).get();
            Elements table = document.select("table.cb-srs-pnts");
            Elements tableHeads = table.select("thead>tr>*");
            List<String> headers = new ArrayList<>();
            tableHeads.forEach(element -> {
                headers.add(element.text());
            });
            pointTable.add(headers);
            Elements bodyTrs = table.select("tbody>*");
            bodyTrs.forEach(tr -> {
                List<String> points = new ArrayList<>();
                if (tr.hasAttr("class")) {
                    Elements tds = tr.select("td");
                    String team = tds.get(0).select("div.cb-col-84").text();
                    points.add(team);
                    tds.forEach(td -> {
                        if (!td.hasClass("cb-srs-pnts-name")) {
                            points.add(td.text());
                        }
                    });
                    // System.out.println(points);
                    pointTable.add(points);
                }

            });

            // System.out.println(pointTable);
        } catch (IOException e) {
            // e.printStackTrace();
        }
        // System.out.println(pointTable);
        return pointTable;
    }

    @Override
    public List<List<String>> getPointTableById(int seriesId) {
        List<List<String>> pointTable = new ArrayList<>();
        Optional<AllSeries> urlById = seriesRepo.findById(seriesId);
        if (urlById.isEmpty())
            return null;
        String tableURL = urlById.get().getSeriesPointTableLink();
        // String tableURL =
        // "https://www.cricbuzz.com/cricket-series/6732/icc-cricket-world-cup-2023/points-table";
        try {
            Document document = Jsoup.connect(tableURL).get();
            Elements table = document.select("table.cb-srs-pnts");
            // tableHeads.forEach(element -> {
            // headers.add(element.text());
            // });
            // pointTable.add(headers);
            Elements bodyTrs = table.select("tbody>*");
            bodyTrs.forEach(tr -> {
                List<String> points = new ArrayList<>();
                if (tr.hasAttr("class")) {
                    Elements tds = tr.select("td");
                    String team = tds.get(0).select("div.cb-col-84").text();
                    points.add(team);
                    tds.forEach(td -> {
                        if (!td.hasClass("cb-srs-pnts-name")) {
                            points.add(td.text());
                        }
                    });
                    // System.out.println(points);
                    pointTable.add(points);
                }

            });

            // System.out.println(pointTable);
        } catch (IOException e) {
            // e.printStackTrace();
        }
        // System.out.println(pointTable);
        return pointTable;
    }

    @Override
    public List<MatchInfoDTO> getSeriesSchedule(int seriesId) {
        Optional<AllSeries> urlById = seriesRepo.findById(seriesId);
        if (urlById.isEmpty())
            return null;
        String scheduleUrl = urlById.get().getSeriesLink();
        // System.out.println(scheduleUrl);
        try {
            Document doc = Jsoup.connect(scheduleUrl).get();

            Element matchDiv = doc.selectFirst("div.cb-bg-white.cb-col-100.cb-col.cb-hm-rght.cb-series-filters > div");
            Map<String, MatchInfoDTO> matches = new HashMap<>();

            for (Element row : matchDiv.select("div")) {
                Elements cells = row.select("span");

                String dateTime = null;
                String matchLink;

                // Get the timestamp
                Element timeElement = row.selectFirst("span.schedule-date");
                if (timeElement != null) {
                    String timestampMs = timeElement.attr("timestamp");
                    if (!timestampMs.equals("0")) {
                        long timestampS = Long.parseLong(timestampMs) / 1000;
                        Instant instant = Instant.ofEpochSecond(timestampS);
                        LocalDateTime dtObject = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        dateTime = dtObject.toString().replace("T", " ").substring(0, 16);
                    }
                }

                if (cells.size() > 4 && dateTime != null && cells.get(cells.size() - 4).text().trim().length() > 10) {
                    matchLink = row.selectFirst("a") != null ? row.selectFirst("a").attr("href")
                            : null;
                    // System.out.println( "https://www.cricbuzz.com" + matchLink);
                    String matchName = cells.get(cells.size() - 4).text().trim();
                    MatchInfoDTO matchInfo = new MatchInfoDTO(dateTime, cells.get(cells.size() - 4).text().trim(),
                            cells.get(cells.size() - 1).text().trim(), "https://www.cricbuzz.com" + matchLink);
                    matches.put(matchName, matchInfo);
                }
            }

            // System.out.println(matches.size());
            // System.out.println(matches);
            List<MatchInfoDTO> allMatchesList = new ArrayList<>(matches.values());
            Collections.sort(allMatchesList);
            return allMatchesList;

        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public String getSeriesById(int seriesId) {
        Optional<AllSeries> seriesById = seriesRepo.findById(seriesId);
        return seriesById.orElse(new AllSeries(0, "UNKOWN-SERIES", "", "", "")).getSeriesTitle();
    }

}

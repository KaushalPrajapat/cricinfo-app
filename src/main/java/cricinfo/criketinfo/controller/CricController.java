package cricinfo.criketinfo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cricinfo.criketinfo.entities.AllSeries;
import cricinfo.criketinfo.entities.Matches;
import cricinfo.criketinfo.services.MatchesService;

@Controller
public class CricController {

    @Autowired
    private MatchesService service;

    @GetMapping("/") // Done
    public String home(Model model) {
        List<Matches> list = service.getAllMatches();
        model.addAttribute("matchesList", list);
        model.addAttribute("pageTitle", "All Matches");
        return "all_matches";
    }

    @GetMapping("/all-completed")
    public String AllMatches(Model model) {
        List<Matches> list = service.getAllMatchesCompleted();
        model.addAttribute("pageTitle", "All Finished Matches");
        model.addAttribute("matchesList", list);
        return "completed_matches";
    }

    @GetMapping("/live-matches")
    public String liveMatches(Model model) {
        List<Matches> list = service.getAllLiveMatches();
        model.addAttribute("pageTitle", "All Ongoing Matches");
        model.addAttribute("matchesList", list);
        return "ongoing_matches";
    }

    @GetMapping("/all-series")
    public String upcomingSeriess(Model model) {
        List<AllSeries> list = service.getAllSeries();
        model.addAttribute("pageTitle", "All Series");
        model.addAttribute("seriesList", list);
        model.addAttribute("seriesList", list);
        return "all_series";
    }

    @GetMapping("/showPointTable")
    public String showPointTable(@RequestParam("seriesId") String seriesId, Model model) {
        // System.out.println("POUINT TABEL FOR " + seriesId);
        model.addAttribute("pageTitle", "Point Table");
        var table = service.getPointTableById(Integer.parseInt(seriesId));
        String seriesName = service.getSeriesById(Integer.parseInt(seriesId));
        model.addAttribute("seriesName", seriesName);
        model.addAttribute("pointTable", table);
        return "point_table";
    }

    @GetMapping("/schedule")
    public String schedule(@RequestParam("seriesId") String seriesId, Model model) {
        model.addAttribute("pageTitle", "Series Schedule");
        var seriesSchedule = service.getSeriesSchedule(Integer.parseInt(seriesId));
        String seriesName = service.getSeriesById(Integer.parseInt(seriesId));
        model.addAttribute("seriesName", seriesName);
        model.addAttribute("seriesSchedule", seriesSchedule);
        return "series_schedule";
    }

}

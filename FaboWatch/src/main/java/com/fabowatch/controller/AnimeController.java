package com.fabowatch.controller;

import DB_Tables.Anime;
import DB_Tables.AnimeGenre;
import DB_Tables.News;
import com.fabowatch.repository.AnimeRepository;
import com.fabowatch.repository.GenreRepository;
import com.fabowatch.repository.NewsRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AnimeController {

    private final AnimeRepository animeRepo;
    private final GenreRepository genreRepo;
    private final NewsRepository newsRepo;

    private final List<String> statuses = List.of("All", "Unwatched", "Watching", "Finished", "Dropped", "Watchlist");
    private final List<String> ratings = List.of("Recommended", "Meh", "Not Recommended");

    public AnimeController(AnimeRepository animeRepo, GenreRepository genreRepo, NewsRepository newsRepo) {
        this.animeRepo = animeRepo;
        this.genreRepo = genreRepo;
        this.newsRepo = newsRepo;
    }

    // Main List Page with Filters
    @GetMapping("/")
    public String listAnime(
            Model model,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "genreIds", required = false) List<Long> genreIds,
            @RequestParam(name = "ratingValues", required = false) List<String> ratingValues,
            HttpSession session
    ) {
        if (status == null) {
            status = (String) session.getAttribute("selectedStatus");
            if (status == null) {
                status = "Watchlist";
            }
        }
        session.setAttribute("selectedStatus", status);

        List<Anime> all = animeRepo.findAll();
        final String filterStatus = status;

        List<Anime> filtered = all.stream().filter(a -> {
            String s = Optional.ofNullable(a.getStatus()).orElse("").trim();
            return switch (filterStatus) {
                case "Finished" -> s.equalsIgnoreCase("Finished");
                case "Unwatched" -> s.equalsIgnoreCase("Unwatched");
                case "Watching" -> s.equalsIgnoreCase("Watching");
                case "Dropped" -> s.equalsIgnoreCase("Dropped");
                case "Watchlist" -> !s.equalsIgnoreCase("Finished") && !s.equalsIgnoreCase("Dropped");
                case "All" -> true;
                default -> true;
            };
        }).collect(Collectors.toList());

        if (q != null && !q.isBlank()) {
            String needle = q.toLowerCase();
            filtered = filtered.stream()
                    .filter(a -> Optional.ofNullable(a.getTitle()).orElse("").toLowerCase().contains(needle))
                    .collect(Collectors.toList());
        }

        if (genreIds != null && !genreIds.isEmpty()) {
            Set<Long> required = new HashSet<>(genreIds);
            filtered = filtered.stream()
                    .filter(a -> {
                        Set<Long> have = a.getGenres().stream()
                                .map(ag -> ag.getGenre().getId())
                                .collect(Collectors.toSet());
                        return have.containsAll(required);
                    })
                    .collect(Collectors.toList());
        }

        if (ratingValues != null && !ratingValues.isEmpty()) {
            Set<String> requiredRatings = new HashSet<>(ratingValues);
            filtered = filtered.stream()
                    .filter(a -> requiredRatings.contains(Optional.ofNullable(a.getRating()).orElse("")))
                    .collect(Collectors.toList());
        }

        LocalDate today = LocalDate.now();
        Comparator<Anime> cmp = Comparator
                .comparing((Anime a) -> newsRepo.existsByAnime_IdAndDateLessThanEqual(a.getId(), today)).reversed()
                .thenComparing(a -> Optional.ofNullable(a.getTitle()).orElse(""), String.CASE_INSENSITIVE_ORDER);
        filtered.sort(cmp);

        Map<Long, String> iconsMap = new HashMap<>();
        for (Anime a : filtered) {
            String icons = a.getGenres().stream()
                    .sorted(Comparator.comparing(ag -> ag.getGenre().getName(), String.CASE_INSENSITIVE_ORDER))
                    .map(ag -> Optional.ofNullable(ag.getGenre().getIcon()).orElse(""))
                    .collect(Collectors.joining(" "));
            iconsMap.put(a.getId(), icons);
        }

        Map<Long, String> latestNewsMap = new HashMap<>();
        for (Anime a : filtered) {
            newsRepo.findTopByAnime_IdOrderByDateDesc(a.getId())
                    .ifPresent(n -> latestNewsMap.put(a.getId(), n.getContent()));
        }

        model.addAttribute("animes", filtered);
        model.addAttribute("iconsMap", iconsMap);
        model.addAttribute("latestNewsMap", latestNewsMap);
        model.addAttribute("genres", genreRepo.findAll());
        model.addAttribute("statuses", statuses);
        model.addAttribute("ratings", ratings);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedGenreIds", genreIds == null ? List.of() : genreIds);
        model.addAttribute("selectedRatingValues", ratingValues == null ? List.of() : ratingValues);
        model.addAttribute("q", q == null ? "" : q);

        return "index";
    }

    // Add Page
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("anime", new Anime());
        model.addAttribute("genres", genreRepo.findAll());
        model.addAttribute("statuses", statuses.subList(1, statuses.size())); // exclude "All"
        model.addAttribute("ratings", ratings);
        return "add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Anime anime,
                      @RequestParam(name="genreIds", required=false) List<Long> genreIds,
                      HttpSession session) {
        if (genreIds != null) {
            genreIds.forEach(id ->
                    genreRepo.findById(id).ifPresent(g -> anime.getGenres().add(new AnimeGenre(anime, g)))
            );
        }
        animeRepo.save(anime);

        String status = Optional.ofNullable((String) session.getAttribute("selectedStatus")).orElse("Watchlist");
        return "redirect:/?status=" + status;
    }

    // Edit Page
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Anime anime = animeRepo.findById(id).orElseThrow();
        model.addAttribute("anime", anime);
        model.addAttribute("genres", genreRepo.findAll());
        model.addAttribute("statuses", statuses.subList(1, statuses.size())); // exclude "All"
        model.addAttribute("ratings", ratings);
        model.addAttribute("newsList", newsRepo.findByAnime_IdOrderByDateDesc(id));

        List<Long> selectedGenreIds = anime.getGenres().stream()
                .map(ag -> ag.getGenre().getId())
                .toList();
        model.addAttribute("selectedGenreIds", selectedGenreIds);

        return "edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Anime form,
                         @RequestParam(name="genreIds", required=false) List<Long> genreIds,
                         HttpSession session) {
        Anime a = animeRepo.findById(id).orElseThrow();
        a.setTitle(form.getTitle());
        a.setStatus(form.getStatus());
        a.setRating(form.getRating());

        a.getGenres().clear();
        if (genreIds != null) {
            genreIds.forEach(gid -> genreRepo.findById(gid)
                    .ifPresent(g -> a.getGenres().add(new AnimeGenre(a, g))));
        }

        animeRepo.save(a);

        String status = Optional.ofNullable((String) session.getAttribute("selectedStatus")).orElse("Watchlist");
        return "redirect:/?status=" + status;
    }

    // Delete Anime
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @RequestParam(name="confirm2", required=false) Boolean confirm2,
                         HttpSession session) {
        if (Boolean.TRUE.equals(confirm2)) {
            animeRepo.deleteById(id);
            String status = Optional.ofNullable((String) session.getAttribute("selectedStatus")).orElse("Watchlist");
            return "redirect:/?status=" + status;
        }
        return "redirect:/edit/" + id;
    }

    // Add News
    @PostMapping("/edit/{id}/news")
    public String addNews(@PathVariable Long id,
                          @RequestParam("date") String dateIso,
                          @RequestParam("content") String content) {
        Anime a = animeRepo.findById(id).orElseThrow();
        News n = new News(a, LocalDate.parse(dateIso), content);
        newsRepo.save(n);
        return "redirect:/edit/" + id;
    }

    // Update News
    @PostMapping("/edit/{id}/news/{nid}")
    public String updateNews(@PathVariable Long id,
                             @PathVariable Long nid,
                             @RequestParam("date") String dateIso,
                             @RequestParam("content") String content) {
        News n = newsRepo.findById(nid).orElseThrow();
        n.setDate(LocalDate.parse(dateIso));
        n.setContent(content);
        newsRepo.save(n);
        return "redirect:/edit/" + id;
    }

    // Delete News
    @PostMapping("/edit/{id}/news/{nid}/delete")
    public String deleteNews(@PathVariable Long id,
                             @PathVariable Long nid) {
        newsRepo.deleteById(nid);
        return "redirect:/edit/" + id;
    }
}

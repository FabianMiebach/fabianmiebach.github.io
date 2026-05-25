package com.animewatchlist.jobs;

import com.animewatchlist.db.Anime;
import com.animewatchlist.dao.AnimeDao;
import com.animewatchlist.dao.NewsDao;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@EnableScheduling
public class NewsStatusScheduler {

    private final AnimeDao animeRepo;
    private final NewsDao newsRepo;

    public NewsStatusScheduler(AnimeDao animeRepo, NewsDao newsRepo) {
        this.animeRepo = animeRepo;
        this.newsRepo = newsRepo;
    }

    @Scheduled(cron = "0 0 4 * * *") // daily 04:00
    public void applyNews() {
        LocalDate today = LocalDate.now();
        // For all anime that are not Finished/Dropped, if they have news <= today, set to Watching
        List<Anime> animes = animeRepo.findAll();
        for (Anime a : animes) {
            String s = a.getStatus() == null ? "" : a.getStatus();
            if (!s.equalsIgnoreCase("Finished") && !s.equalsIgnoreCase("Dropped")) {
                boolean hasNews = newsRepo.existsByAnime_IdAndDateLessThanEqual(a.getId(), today);
                if (hasNews) {
                    a.setStatus("Watching");
                }
            }
        }
        animeRepo.saveAll(animes);
    }
}


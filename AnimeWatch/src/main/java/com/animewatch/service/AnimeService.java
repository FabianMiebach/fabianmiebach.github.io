package com.animewatch.service;

import com.animewatch.dao.AnimeDao;
import com.animewatch.dao.GenreDao;
import com.animewatch.dao.NewsDao;
import com.animewatch.db.Anime;
import com.animewatch.db.Genre;
import com.animewatch.db.News;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnimeService {

    private final AnimeDao animeDao;
    private final GenreDao genreDao;
    private final NewsDao newsDao;

    public AnimeService(AnimeDao animeDao, GenreDao genreDao, NewsDao newsDao) {
        this.animeDao = animeDao;
        this.genreDao = genreDao;
        this.newsDao = newsDao;
    }

    public List<News> getAllNews() {
        return newsDao.findAll();
    }

    public News getNewsForAnime(Anime anime) {
        if (anime == null || anime.getId() == null) {
            return null;
        }
        return newsDao.findAll().stream()
                .filter(n -> n.getAnime() != null && anime.getId().equals(n.getAnime().getId()))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void saveNews(News news) {
        if (news != null) {
            newsDao.save(news);
        }
    }

    public List<Genre> getAllGenres() {
        return genreDao.findAll();
    }

    @Transactional
    public void saveAnime(Anime anime) {
        animeDao.save(anime);
    }

    @Transactional
    public void deleteAnime(Anime anime) {
        animeDao.delete(anime);
    }

    @Transactional
    public List<Anime> getFilteredAnime(String title, String status, Set<Genre> genres) {
        List<Anime> allAnime = animeDao.findAll();
        LocalDate today = LocalDate.now();

        for (Anime anime : allAnime) {
            News news = getNewsForAnime(anime);
            if (news != null && news.getDate() != null && !today.isBefore(news.getDate())) {
                if (!"Watching".equalsIgnoreCase(anime.getStatus())) {
                    anime.setStatus("Watching");
                    animeDao.save(anime);
                }
            }
        }

        return allAnime.stream()
                .sorted((a1, a2) -> {
                    News n1 = getNewsForAnime(a1);
                    News n2 = getNewsForAnime(a2);

                    boolean a1HasDueNews = (n1 != null && n1.getDate() != null && !today.isBefore(n1.getDate()));
                    boolean a2HasDueNews = (n2 != null && n2.getDate() != null && !today.isBefore(n2.getDate()));

                    if (a1HasDueNews && !a2HasDueNews) return -1;
                    if (!a1HasDueNews && a2HasDueNews) return 1;

                    return a1.getTitle().compareToIgnoreCase(a2.getTitle());
                })
                .collect(Collectors.toList());
    }
}
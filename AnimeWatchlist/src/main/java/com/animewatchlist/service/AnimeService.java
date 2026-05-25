package com.animewatchlist.service;

import com.animewatchlist.dao.AnimeDao;
import com.animewatchlist.dao.GenreDao;
import com.animewatchlist.dao.NewsDao;
import com.animewatchlist.db.Anime;
import com.animewatchlist.db.Genre;
import com.animewatchlist.db.News;
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
                .filter(anime -> {
                    if (title != null && !title.isEmpty() &&
                            !anime.getTitle().toLowerCase().contains(title.toLowerCase())) {
                        return false;
                    }
                    if (status != null && !status.isEmpty() && !"All".equalsIgnoreCase(status) &&
                            !status.equalsIgnoreCase(anime.getStatus())) {
                        return false;
                    }
                    if (genres != null && !genres.isEmpty()) {
                        if (anime.getGenres() == null) {
                            return false;
                        }
                        Set<Long> animeGenreIds = anime.getGenres().stream()
                                .map(ag -> ag != null && ag.getGenre() != null ? ag.getGenre().getId() : null)
                                .filter(id -> id != null)
                                .collect(Collectors.toSet());

                        for (Genre g : genres) {
                            if (!animeGenreIds.contains(g.getId())) {
                                return false;
                            }
                        }
                    }
                    return true;
                })
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
package com.animewatchlist.dao;

import com.animewatchlist.db.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NewsDao extends JpaRepository<News, Long> {
    boolean existsByAnime_IdAndDateLessThanEqual(Long animeId, LocalDate date);
    List<News> findByAnime_IdOrderByDateDesc(Long animeId);
    Optional<News> findTopByAnime_IdOrderByDateDesc(Long animeId);

}

package com.fabowatch.repository;

// package com.fabowatch.repository;
import DB_Tables.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByAnime_IdAndDateLessThanEqual(Long animeId, LocalDate date);
    List<News> findByAnime_IdOrderByDateDesc(Long animeId);
    Optional<News> findTopByAnime_IdOrderByDateDesc(Long animeId);

}

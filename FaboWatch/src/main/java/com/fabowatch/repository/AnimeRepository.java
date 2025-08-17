package com.fabowatch.repository;

import DB_Tables.Anime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimeRepository extends JpaRepository<Anime, Long> { }

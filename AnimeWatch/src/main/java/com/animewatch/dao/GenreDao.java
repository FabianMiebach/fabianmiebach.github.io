package com.animewatch.dao;

import com.animewatch.db.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreDao extends JpaRepository<Genre, Long> {
}

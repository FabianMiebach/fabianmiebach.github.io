package com.animewatchlist.dao;

import com.animewatchlist.db.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreDao extends JpaRepository<Genre, Long> {
}

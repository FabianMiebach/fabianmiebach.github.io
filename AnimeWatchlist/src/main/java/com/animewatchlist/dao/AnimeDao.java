package com.animewatchlist.dao;

import com.animewatchlist.db.Anime;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimeDao extends JpaRepository<Anime, Long> {

    @EntityGraph(attributePaths = {"genres", "genres.genre"})
    @Override
    List<Anime> findAll();
}
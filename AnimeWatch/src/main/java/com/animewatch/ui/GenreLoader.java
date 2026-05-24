package com.animewatch.ui;

import DB_Tables.Genre;
import com.animewatch.repository.GenreRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GenreLoader {

    private final GenreRepository genreRepo;

    public GenreLoader(GenreRepository genreRepo) {
        this.genreRepo = genreRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadGenres() {
        if (genreRepo.count() == 0) {
            genreRepo.save(new Genre("Action", "⚔️"));
            genreRepo.save(new Genre("Adventure", "🗺️"));
            genreRepo.save(new Genre("Art", "🎨"));
            genreRepo.save(new Genre("Beach", "⛱️"));
            genreRepo.save(new Genre("Comedy", "🎭"));
            genreRepo.save(new Genre("Cooking", "🍳"));
            genreRepo.save(new Genre("Cosplay", "🎬"));
            genreRepo.save(new Genre("Dystopia", "🔒"));
            genreRepo.save(new Genre("Ecchi", "👗"));
            genreRepo.save(new Genre("Fantasy", "🧙"));
            genreRepo.save(new Genre("Games", "🎮"));
            genreRepo.save(new Genre("Heartbreaking", "💔"));
            genreRepo.save(new Genre("Highschool", "📚"));
            genreRepo.save(new Genre("Horror", "🕷️"));
            genreRepo.save(new Genre("Isekai", "🌌"));
            genreRepo.save(new Genre("Mafia", "💰"));
            genreRepo.save(new Genre("Magic", "🔮"));
            genreRepo.save(new Genre("Medieval", "🏰"));
            genreRepo.save(new Genre("Monsters", "🐲"));
            genreRepo.save(new Genre("Music", "🎤"));
            genreRepo.save(new Genre("Mystery", "🕵️"));
            genreRepo.save(new Genre("Pirates", "☠️"));
            genreRepo.save(new Genre("Psychological", "🧠"));
            genreRepo.save(new Genre("Romance", "❤️"));
            genreRepo.save(new Genre("Sci-Fi", "🔬"));
            genreRepo.save(new Genre("Skills", "🎲"));
            genreRepo.save(new Genre("Slice of Life", "🍰"));
            genreRepo.save(new Genre("Space", "🪐"));
            genreRepo.save(new Genre("Special", "✨"));
            genreRepo.save(new Genre("Spy", "💣"));
            genreRepo.save(new Genre("Sports", "🏆"));
            genreRepo.save(new Genre("Supernatural", "👻"));
            genreRepo.save(new Genre("Thriller", "🩸"));
            genreRepo.save(new Genre("Time Travel", "⏰"));
            genreRepo.save(new Genre("Vampire", "🦇"));
            genreRepo.save(new Genre("Yandere", "🔪"));
        }
    }
}

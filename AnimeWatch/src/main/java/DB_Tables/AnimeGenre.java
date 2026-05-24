package DB_Tables;


import jakarta.persistence.*;

@Entity
@Table(name = "anime_genre")
public class AnimeGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public AnimeGenre() {}

    public AnimeGenre(Anime anime, Genre genre) {
        this.anime = anime;
        this.genre = genre;
    }

    public Long getId() { return id; }

    public Anime getAnime() { return anime; }
    public void setAnime(Anime anime) { this.anime = anime; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
}

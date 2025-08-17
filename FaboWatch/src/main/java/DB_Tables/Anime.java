package DB_Tables;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "anime")
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String status;
    private String rating;

    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnimeGenre> genres = new ArrayList<>();

    public Anime() {}

    public Anime(String title, String status, String rating) {
        this.title = title;
        this.status = status;
        this.rating = rating;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public List<AnimeGenre> getGenres() { return genres; }
    public void setGenres(List<AnimeGenre> genres) { this.genres = genres; }
}

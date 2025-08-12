package DB_Tables;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String icon;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnimeGenre> animes = new ArrayList<>();

    public Genre() {}

    public Genre(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public List<AnimeGenre> getAnimes() { return animes; }
    public void setAnimes(List<AnimeGenre> animes) { this.animes = animes; }
}

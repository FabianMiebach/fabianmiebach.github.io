package DB_Tables;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    private Long id = 1L; // singleton ID

    @Column(name = "color_theme")
    private String colorTheme;

    @Column(name = "start_page")
    private String startPage;

    public UserSettings() {}

    public UserSettings(String colorTheme, String startPage) {
        this.colorTheme = colorTheme;
        this.startPage = startPage;
    }

    public Long getId() {
        return id;
    }

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }
}

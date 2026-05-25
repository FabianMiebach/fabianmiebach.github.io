package com.animewatchlist.ui;

import com.animewatchlist.db.Anime;
import com.animewatchlist.db.AnimeGenre;
import com.animewatchlist.db.Genre;
import com.animewatchlist.db.News;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.animewatchlist.service.AnimeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route("/AnimeWatchlist")
public class AnimeWatchlistView extends VerticalLayout {
    private final AnimeService animeService;
    private final Grid<Anime> grid = new Grid<>(Anime.class, false);
    private final TextField filterText = new TextField();
    private final MultiSelectComboBox<Genre> genreFilter = new MultiSelectComboBox<>();
    private final ComboBox<String> statusFilter = new ComboBox<>();
    private final Span counter = new Span();
    private final Binder<Anime> binder = new Binder<>(Anime.class);
    private final Binder<News> newsBinder = new Binder<>(News.class);
    private final Dialog editDialog = new Dialog();

    private final TextField titleField = new TextField("Title");
    private final ComboBox<String> statusBox = new ComboBox<>("Status", "Unwatched", "Watching", "Finished", "Dropped");
    private final ComboBox<String> ratingBox = new ComboBox<>("Rating", "Recommended", "Meh", "Not Recommended");
    private final MultiSelectComboBox<Genre> genresBox = new MultiSelectComboBox<>("Genres");
    private final DatePicker newsDatePicker = new DatePicker("Date");
    private final TextArea newsContentField = new TextArea("News");

    private Anime currentAnime;
    private News currentNews;

    public AnimeWatchlistView(AnimeService animeService) {
        this.animeService = animeService;
        initUI();
    }

    public void initUI() {
        setSizeFull();
        configureGrid();
        configureFormDialog();

        Button addAnimeButton = new Button("Add anime", VaadinIcon.PLUS.create());
        addAnimeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addAnimeButton.addClickListener(e -> openAnimeForm(new Anime()));

        HorizontalLayout toolbar = new HorizontalLayout(
                configureFilterText(),
                configureGenreFilter(),
                configureStatusFilter(),
                addAnimeButton
        );
        toolbar.setVerticalComponentAlignment(Alignment.END, addAnimeButton);
        toolbar.setWidthFull();

        add(new H1("AnimeWatchlist"), counter, toolbar, grid);
        updateList();
    }

    private TextField configureFilterText() {
        filterText.setPlaceholder("Search for title...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        return filterText;
    }

    private MultiSelectComboBox<Genre> configureGenreFilter() {
        genreFilter.setPlaceholder("Filter for genre...");
        genreFilter.setItems(animeService.getAllGenres());
        genreFilter.setItemLabelGenerator(g -> g.getIcon() + " " + g.getName());
        genreFilter.setClearButtonVisible(true);
        genreFilter.addValueChangeListener(e -> updateList());
        return genreFilter;
    }

    private ComboBox<String> configureStatusFilter() {
        statusFilter.setPlaceholder("Status...");
        statusFilter.setItems("All", "Unwatched", "Watching", "Finished", "Dropped", "Watchlist");
        statusFilter.setValue("All");
        statusFilter.addValueChangeListener(e -> updateList());
        return statusFilter;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Anime::getTitle).setHeader("Title").setSortable(true).setFlexGrow(3).setResizable(true);
        grid.addColumn(Anime::getStatus).setHeader("Status").setSortable(true).setAutoWidth(true);
        grid.addColumn(Anime::getRating).setHeader("Rating").setSortable(true).setAutoWidth(true);
        grid.addColumn(anime -> {
            News n = animeService.getNewsForAnime(anime);
            return (n != null && n.getDate() != null) ? n.getDate().toString() : "";
        }).setHeader("Date").setSortable(true).setAutoWidth(true);
        grid.addColumn(anime -> {
            News n = animeService.getNewsForAnime(anime);
            return n != null ? n.getContent() : "";
        }).setHeader("News").setAutoWidth(true);

        grid.addComponentColumn(anime -> {
            HorizontalLayout badges = new HorizontalLayout();
            if (anime.getGenres() != null) {
                anime.getGenres().stream()
                        .filter(ag -> ag != null && ag.getGenre() != null)
                        .sorted(Comparator.comparing(ag -> ag.getGenre().getName()))
                        .forEach(ag -> {
                            Span badge = new Span(ag.getGenre().getIcon());
                            badge.getElement().setAttribute("title", ag.getGenre().getName());
                            badges.add(badge);
                        });
            }
            return badges;
        }).setHeader("Genre").setFlexGrow(1);

        grid.addComponentColumn(anime -> {
            Button clearDateBtn = new Button(VaadinIcon.CALENDAR_CLOCK.create());
            clearDateBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            News n = animeService.getNewsForAnime(anime);
            clearDateBtn.setEnabled(n != null && n.getDate() != null);
            clearDateBtn.addClickListener(e -> {
                if (n != null) {
                    n.setDate(null);
                    animeService.saveNews(n);
                    updateList();
                }
            });
            return clearDateBtn;
        }).setWidth("60px");

        grid.addComponentColumn(anime -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> openAnimeForm(anime));
            return editBtn;
        }).setWidth("60px");
    }

    private void configureFormDialog() {
        FormLayout formLayout = new FormLayout();
        genresBox.setItems(animeService.getAllGenres());
        genresBox.setItemLabelGenerator(g -> g.getIcon() + " " + g.getName());
        newsContentField.setHeight("80px");
        formLayout.add(titleField, statusBox, ratingBox, genresBox, newsDatePicker, newsContentField);
        binder.bind(titleField, Anime::getTitle, Anime::setTitle);
        binder.bind(statusBox, Anime::getStatus, Anime::setStatus);
        binder.bind(ratingBox, Anime::getRating, Anime::setRating);
        newsBinder.bind(newsDatePicker, News::getDate, News::setDate);
        newsBinder.bind(newsContentField, News::getContent, News::setContent);
        genresBox.addValueChangeListener(e -> {
            if (currentAnime != null) {
                if (currentAnime.getGenres() == null) currentAnime.setGenres(new ArrayList<>());
                List<AnimeGenre> newGenres = e.getValue().stream()
                        .map(genre -> new AnimeGenre(currentAnime, genre))
                        .collect(Collectors.toList());
                currentAnime.getGenres().clear();
                currentAnime.getGenres().addAll(newGenres);
            }
        });
        Button saveBtn = new Button("Save", e -> saveAnime());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelBtn = new Button("Discard", e -> editDialog.close());
        Button deleteBtn = new Button("Delete", e -> deleteAnime());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveBtn, deleteBtn, cancelBtn);
        editDialog.add(formLayout, actions);
    }

    private void openAnimeForm(Anime anime) {
        this.currentAnime = anime;
        binder.readBean(anime);
        News existing = animeService.getNewsForAnime(anime);
        this.currentNews = (existing != null) ? existing : new News();
        if (existing == null) this.currentNews.setAnime(anime);
        newsBinder.readBean(currentNews);
        if (anime.getGenres() != null) {
            genresBox.setValue(anime.getGenres().stream().map(AnimeGenre::getGenre).collect(Collectors.toSet()));
        } else {
            genresBox.setValue(Collections.emptySet());
        }
        editDialog.open();
    }

    private void saveAnime() {
        if (binder.writeBeanIfValid(currentAnime)) {
            animeService.saveAnime(currentAnime);
            if (newsBinder.writeBeanIfValid(currentNews)) {
                currentNews.setAnime(currentAnime);
                animeService.saveNews(currentNews);
            }
            updateList();
            editDialog.close();
        }
    }

    private void deleteAnime() {
        if (currentAnime != null && currentAnime.getId() != null) {
            animeService.deleteAnime(currentAnime);
            updateList();
        }
        editDialog.close();
    }

    private void updateList() {
        String status = statusFilter.getValue();
        List<Anime> list;
        if ("Watchlist".equals(status)) {
            list = animeService.getFilteredAnime(filterText.getValue(), "All", genreFilter.getValue()).stream()
                    .filter(a -> "Unwatched".equals(a.getStatus()) || "Watching".equals(a.getStatus()))
                    .collect(Collectors.toList());
        } else {
            list = animeService.getFilteredAnime(filterText.getValue(), status, genreFilter.getValue());
        }
        grid.setItems(list);
        counter.setText("Results: " + list.size());
    }
}
package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreRepository {

    void saveGenres(Film film);

    List<Genre> findGenresByFilmId(int filmId);

    void deleteGenres(Film film);
}

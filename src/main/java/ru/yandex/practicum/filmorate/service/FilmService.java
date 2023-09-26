package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmGenreRepository;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;

    @Autowired
    public FilmService(
            @Qualifier("filmRepositoryImpl") FilmRepository filmRepository,
            FilmGenreRepository filmGenreRepository,
            LikeRepository likeRepository,
            UserService userService
    ) {
        this.filmRepository = filmRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    public Film create(Film film) {
        Film savedFilm = filmRepository.save(film);
        filmGenreRepository.saveGenres(film);
        filmGenreRepository.loadGenres(Collections.singletonList(savedFilm));
        return savedFilm;
    }

    public Film findById(int id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка получить несуществующий фильм"));
        filmGenreRepository.loadGenres(Collections.singletonList(film));
        return film;
    }

    public List<Film> findAll() {
        List<Film> films = filmRepository.findAll();
        filmGenreRepository.loadGenres(films);
        return films;
    }

    public Film update(Film film) {
        final int filmId = film.getId();
        filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка обновить несуществующий фильм"));
        filmGenreRepository.deleteGenres(film);
        return create(film);
    }

    public Film addLikeToFilm(int filmId, int userId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка поставить лайк несуществующему фильму"));
        filmGenreRepository.loadGenres(Collections.singletonList(film));

        User user = userService.findById(userId);

        film.addLike(user);
        likeRepository.deleteLikes(film);
        likeRepository.saveLikes(film);
        return film;
    }

    public void removeLikeFromFilm(int filmId, int userId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка убрать лайк у несуществующего фильма"));
        User user = userService.findById(userId);

        likeRepository.deleteLike(film, user);
    }

    public List<Film> getFilmsByLikes(int count) {
        return findAll().stream()
                .sorted(Comparator.comparingInt(Film::getAmountOfLikes).reversed())
                .limit(count).collect(Collectors.toList());
    }

    public void delete(Film film) {
        filmRepository.delete(film);
        filmGenreRepository.deleteGenres(film);
        likeRepository.deleteLikes(film);
    }
}

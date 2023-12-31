package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private static final String ERROR_MESSAGE_SEARCH_FILM =
            "Допустимые значения: director, title. Либо оба значения через запятую.";

    @GetMapping
    public List<Film> getFilms() {
        log.info("Пришел GET-запрос /films");

        List<Film> films = filmService.findAll();
        log.info("Ответ на GET-запрос /films с телом={}", films);
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Пришел GET-запрос /films/{id={}}", id);

        Film film = filmService.findById(id);
        log.info("Ответ на GET-запрос /films/{id={}} с телом={}", id, film);
        return film;
    }

    @GetMapping("/popular")
    public List<Film> getTopFilmsByLikesOrGenreAndYear(
            @RequestParam(value = "count", defaultValue = "10", required = false) int count,
            @RequestParam(value = "genreId", defaultValue = "0", required = false) int genreId,
            @RequestParam(value = "year", defaultValue = "0", required = false) int year
    ) {
        log.info("Пришел GET-запрос /films/popular?count={}&genreId={}&year={}", count, genreId, year);

        List<Film> popularFilms = filmService.findTopFilmsByLikesOrGenreAndYear(genreId, year, count);
        log.info("Ответ на GET-запрос /films/popular?count={}&genreId={}&year={} с телом={}",
                count, genreId, year, popularFilms);
        return popularFilms;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Пришел GET-запрос /films/common?userId={}&friendId={}", userId, friendId);

        List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.info("Ответ на GET-запрос /films/common?userId={}&friendId={} с телом={}", userId,
            friendId, commonFilms);

            return commonFilms;
        }

    @GetMapping("/director/{directorId}")
    public List<Film> getTopFilmsOfDirectorByLikesOrReleaseYear(
            @PathVariable int directorId,
            @RequestParam(value = "sortBy", defaultValue = "") @NotBlank String sortBy
    ) {
        log.info("Пришел GET-запрос /films/director/{directorId={}}?sortBy={}", directorId, sortBy);

        List<Film> directorTopFilms = filmService.getDirectorFilmsByLikesOrYear(directorId, sortBy);
        log.info("Ответ на GET-запрос /films/director/{directorId={}}?sortBy={} с телом={}",
                directorId, sortBy, directorTopFilms
        );
        return directorTopFilms;
    }

    @GetMapping("/search")
    public List<Film> searchFilms(
            @RequestParam @NotBlank String query,
            @RequestParam @Size(min = 1, max = 2, message = ERROR_MESSAGE_SEARCH_FILM) List<String> by) {
        log.info("Пришел GET-запрос /films/search?query={}&by={}", query, by);

        List<Film> foundFilms = filmService.searchFilms(query, by);
        log.info("Ответ на GET-запрос /films/search?query={}&by={} с телом={}", query, by, foundFilms);
        return foundFilms;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Пришел POST-запрос /films с телом={}", film);

        Film savedFilm = filmService.create(film);
        log.info("Фильм film={} успешно создан", savedFilm);
        return savedFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Пришел PUT-запрос /films с телом={}", film);

        Film updatedFilm = filmService.update(film);
        log.info("Фильм film={} успешно обновлен", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Пришел PUT-запрос /films/{id={}}/like/{userId={}}", filmId, userId);

        Film likedFilm = filmService.addLikeToFilm(filmId, userId);
        log.info("Лайк фильму film={} от пользователя с id={} поставлен", likedFilm, userId);
        return likedFilm;
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable("filmId") int filmId) {
        log.info("Пришел DELETE-запрос /films/filmId={}", filmId);

        filmService.deleteFilmById(filmId);
        log.info("Фильм id={} удален", filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Пришел DELETE-запрос /films/{id={}}/like/{userId={}}", filmId, userId);

        filmService.removeLikeFromFilm(filmId, userId);
        log.info("Лайк у фильма id={} от пользователя id={} удален", filmId, userId);
    }
}
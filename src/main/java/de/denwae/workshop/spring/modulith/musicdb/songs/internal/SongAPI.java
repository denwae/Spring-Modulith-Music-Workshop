package de.denwae.workshop.spring.modulith.musicdb.songs.internal;

import de.denwae.workshop.spring.modulith.musicdb.songs.CompleteSongDTO;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface SongAPI {

    CompleteSongDTO createSong(String name, Duration duration, List<UUID> artists);
    CompleteSongDTO findSong(UUID id);
    void deleteSong(UUID id);
}

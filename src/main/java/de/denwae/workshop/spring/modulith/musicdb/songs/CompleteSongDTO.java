package de.denwae.workshop.spring.modulith.musicdb.songs;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public record CompleteSongDTO(
        UUID id,
        String name,
        Duration duration,
        List<SongAlbumDTO> albums,
        List<SongArtistDTO> artists
) {
    public record SongAlbumDTO(
            UUID id,
            String name
    ) { }

    public record SongArtistDTO(
            UUID id,
            String name
    ) { }
}

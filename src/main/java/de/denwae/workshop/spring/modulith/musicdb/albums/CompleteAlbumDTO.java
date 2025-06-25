package de.denwae.workshop.spring.modulith.musicdb.albums;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public record CompleteAlbumDTO(
        UUID id,
        String name,
        Integer releaseYear,
        Duration duration,
        List<AlbumArtistDTO> artists,
        List<AlbumSongDTO> songs
) {
    public record AlbumArtistDTO(
            UUID id,
            String name
    ){ }

    public record AlbumSongDTO(
            UUID id,
            String name,
            Duration duration
    ) { }
}

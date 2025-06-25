package de.denwae.workshop.spring.modulith.musicdb.artists;

import java.util.Set;
import java.util.UUID;

public record CompleteArtistDTO(
        UUID id,
        String name,
        Integer formationYear,
        Status status,
        String biography,
        Set<ArtistAlbumDTO> albums
) {
    public record ArtistAlbumDTO(
            UUID id,
            String name,
            int releaseYear
    ) { }
}

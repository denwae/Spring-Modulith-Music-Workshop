package de.denwae.workshop.spring.modulith.musicdb.artists.internal;

import de.denwae.workshop.spring.modulith.musicdb.artists.CompleteArtistDTO;
import de.denwae.workshop.spring.modulith.musicdb.artists.Status;

import java.util.UUID;

public interface ArtistAPI {

    CompleteArtistDTO createArtist(String name, Integer formationYear, Status status, String biography);
    CompleteArtistDTO findArtist(UUID id);
    void deleteArtist(UUID id);
}

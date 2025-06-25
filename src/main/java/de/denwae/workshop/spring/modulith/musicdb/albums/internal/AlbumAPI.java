package de.denwae.workshop.spring.modulith.musicdb.albums.internal;

import de.denwae.workshop.spring.modulith.musicdb.albums.CompleteAlbumDTO;

import java.util.List;
import java.util.UUID;

public interface AlbumAPI {
    CompleteAlbumDTO createAlbum(String name, Integer releaseYear, List<UUID> songs);
    CompleteAlbumDTO findAlbum(UUID id);
    void deleteAlbum(UUID id);
}

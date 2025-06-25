package de.denwae.workshop.spring.modulith.musicdb;

import de.denwae.workshop.spring.modulith.musicdb.albums.CompleteAlbumDTO;
import de.denwae.workshop.spring.modulith.musicdb.albums.internal.AlbumAPI;
import de.denwae.workshop.spring.modulith.musicdb.artists.CompleteArtistDTO;
import de.denwae.workshop.spring.modulith.musicdb.artists.Status;
import de.denwae.workshop.spring.modulith.musicdb.artists.internal.ArtistAPI;
import de.denwae.workshop.spring.modulith.musicdb.songs.CompleteSongDTO;
import de.denwae.workshop.spring.modulith.musicdb.songs.internal.SongAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class ScenarioTests {

    @Autowired
    private ArtistAPI artistService;
    @Autowired
    private AlbumAPI albumService;
    @Autowired
    private SongAPI songService;

    @Test
    void whenCreatingAnAlbumWithASongAndRetrievingTheArtistTheAlbumWillBeCorrect() {
        assertThatThrownBy(() -> artistService.createArtist("Carnation", 2013, null, null));
        var artist = artistService.createArtist("Carnation", 2013, Status.ACTIVE, null);
        assertThat(artist).isNotNull();
        assertThat(artist.name()).isEqualTo("Carnation");
        assertThat(artist.biography()).isNull();
        assertThat(artist.status()).isEqualTo(Status.ACTIVE);
        assertThat(artist.formationYear()).isEqualTo(2013);
        assertThat(artist.albums().size()).isEqualTo(0);
        var tempSong = new CompleteSongDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Iron Discipline", Duration.ofSeconds(232), List.of(artist.id()))).doesNotThrowAnyException()
        );

        var song1 = tempSong[0];
        assertThat(song1.id()).isNotNull();
        assertThat(song1.name()).isEqualTo("Iron Discipline");
        assertThat(song1.duration()).isEqualTo(Duration.ofSeconds(232));
        assertThat(song1.artists()).size().isEqualTo(1);
        assertThat(song1.artists()).containsExactly(new CompleteSongDTO.SongArtistDTO(artist.id(), artist.name()));

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Sepulcher of Alteration", Duration.ofSeconds(262), List.of(artist.id()))).doesNotThrowAnyException()
        );
        var song2 = tempSong[0];
        assertThat(song2.id()).isNotNull();
        assertThat(song2.name()).isEqualTo("Sepulcher of Alteration");
        assertThat(song2.duration()).isEqualTo(Duration.ofSeconds(262));
        assertThat(song2.artists()).size().isEqualTo(1);
        assertThat(song2.artists()).containsExactly(new CompleteSongDTO.SongArtistDTO(artist.id(), artist.name()));

        var tempAlbum = new CompleteAlbumDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempAlbum[0] = albumService.createAlbum("Where Death Lies", 2020, List.of(song1.id(), song2.id()))).doesNotThrowAnyException()
        );
        var album = tempAlbum[0];
        assertThat(album.id()).isNotNull();
        assertThat(album.name()).isEqualTo("Where Death Lies");
        assertThat(album.duration()).isEqualTo(song1.duration().plus(song2.duration()));
        assertThat(album.songs()).size().isEqualTo(2);
        assertThat(album.songs()).contains(
                new CompleteAlbumDTO.AlbumSongDTO(
                        song1.id(),
                        song1.name(),
                        song1.duration()),
                new CompleteAlbumDTO.AlbumSongDTO(
                        song2.id(),
                        song2.name(),
                        song2.duration()
                )
        );
        assertThat(album.artists()).size().isEqualTo(1);
        assertThat(album.artists()).contains(
                new CompleteAlbumDTO.AlbumArtistDTO(artist.id(), artist.name())
        );

        var retrievedAlbum = albumService.findAlbum(album.id());
        assertThat(retrievedAlbum).isEqualTo(album);

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedSong1 = songService.findSong(song1.id());
                    assertThat(updatedSong1.albums().size()).isEqualTo(1);
                    assertThat(updatedSong1.albums().getFirst().id()).isEqualTo(album.id());
                    assertThat(updatedSong1.albums().getFirst().name()).isEqualTo(album.name());
                }
        );

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedSong2 = songService.findSong(song2.id());
                    assertThat(updatedSong2.albums().size()).isEqualTo(1);
                    assertThat(updatedSong2.albums().getFirst().id()).isEqualTo(album.id());
                    assertThat(updatedSong2.albums().getFirst().name()).isEqualTo(album.name());
                }
        );


        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedArtist = artistService.findArtist(artist.id());
                    assertThat(updatedArtist.albums().size()).isEqualTo(1);
                    assertThat(updatedArtist.albums()).contains(new CompleteArtistDTO.ArtistAlbumDTO(album.id(), album.name(), album.releaseYear()));
                }
        );

    }

    @Test
    void whenCreatingAnAlbumWithSongsFromMultipleArtistsBothArtistWillHaveThatSong() {
        assertThatThrownBy(() -> artistService.createArtist(null, 2017, Status.ACTIVE, null));
        var artist1 = artistService.createArtist("Inhuman Nature", 2017, Status.ACTIVE, null);
        assertThat(artist1).isNotNull();
        assertThat(artist1.name()).isEqualTo("Inhuman Nature");
        assertThat(artist1.biography()).isNull();
        assertThat(artist1.status()).isEqualTo(Status.ACTIVE);
        assertThat(artist1.formationYear()).isEqualTo(2017);
        assertThat(artist1.albums().size()).isEqualTo(0);
        var artist2 = artistService.createArtist("Ninth Realm", 2018, Status.ACTIVE, null);
        assertThat(artist2).isNotNull();
        assertThat(artist2.name()).isEqualTo("Ninth Realm");
        assertThat(artist2.biography()).isNull();
        assertThat(artist2.status()).isEqualTo(Status.ACTIVE);
        assertThat(artist2.formationYear()).isEqualTo(2018);
        assertThat(artist2.albums().size()).isEqualTo(0);

        var tempSong = new CompleteSongDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Take Them By Force", Duration.ofSeconds(247), List.of(artist1.id()))).doesNotThrowAnyException()
        );
        var song1 = tempSong[0];
        assertThat(song1.id()).isNotNull();
        assertThat(song1.name()).isEqualTo("Take Them By Force");
        assertThat(song1.duration()).isEqualTo(Duration.ofSeconds(247));
        assertThat(song1.artists()).size().isEqualTo(1);
        assertThat(song1.artists()).containsExactly(new CompleteSongDTO.SongArtistDTO(artist1.id(), artist1.name()));

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Conquering Eternity", Duration.ofSeconds(228), List.of(artist2.id()))).doesNotThrowAnyException()
        );
        var song2 = tempSong[0];
        assertThat(song2.id()).isNotNull();
        assertThat(song2.name()).isEqualTo("Conquering Eternity");
        assertThat(song2.duration()).isEqualTo(Duration.ofSeconds(228));
        assertThat(song2.artists()).size().isEqualTo(1);
        assertThat(song2.artists()).containsExactly(new CompleteSongDTO.SongArtistDTO(artist2.id(), artist2.name()));

        var tempAlbum = new CompleteAlbumDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempAlbum[0] = albumService.createAlbum("Inhuman Nature/Ninth Realm", 2023, List.of(song1.id(), song2.id()))).doesNotThrowAnyException()
        );
        var album = tempAlbum[0];
        assertThat(album.id()).isNotNull();
        assertThat(album.name()).isEqualTo("Inhuman Nature/Ninth Realm");
        assertThat(album.duration()).isEqualTo(song1.duration().plus(song2.duration()));
        assertThat(album.songs()).size().isEqualTo(2);
        assertThat(album.songs()).contains(
                new CompleteAlbumDTO.AlbumSongDTO(
                        song1.id(),
                        song1.name(),
                        song1.duration()),
                new CompleteAlbumDTO.AlbumSongDTO(
                        song2.id(),
                        song2.name(),
                        song2.duration()
                )
        );
        assertThat(album.artists().size()).isEqualTo(2);
        assertThat(album.artists()).contains(
                new CompleteAlbumDTO.AlbumArtistDTO(artist1.id(), artist1.name()),
                new CompleteAlbumDTO.AlbumArtistDTO(artist2.id(), artist2.name())
        );

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedSong1 = songService.findSong(song1.id());
                    assertThat(updatedSong1.albums().size()).isEqualTo(1);
                    assertThat(updatedSong1.albums().getFirst().id()).isEqualTo(album.id());
                    assertThat(updatedSong1.albums().getFirst().name()).isEqualTo(album.name());
                }
        );

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedSong2 = songService.findSong(song2.id());
                    assertThat(updatedSong2.albums().size()).isEqualTo(1);
                    assertThat(updatedSong2.albums().getFirst().id()).isEqualTo(album.id());
                    assertThat(updatedSong2.albums().getFirst().name()).isEqualTo(album.name());
                }
        );

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedArtist1 = artistService.findArtist(artist1.id());
                    assertThat(updatedArtist1.albums().size()).isEqualTo(1);
                    assertThat(updatedArtist1.albums()).contains(new CompleteArtistDTO.ArtistAlbumDTO(album.id(), album.name(), album.releaseYear()));
                }
        );

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedArtist2 = artistService.findArtist(artist2.id());
                    assertThat(updatedArtist2.albums().size()).isEqualTo(1);
                    assertThat(updatedArtist2.albums()).contains(new CompleteArtistDTO.ArtistAlbumDTO(album.id(), album.name(), album.releaseYear()));
                }
        );
    }

    @Test
    void whenCreatingAnAlbumWithASongFromTwoArtistsTheAlbumWillHaveTwoArtists() {
        var artist1 = artistService.createArtist("Mizmor", 2012, Status.ACTIVE, null);
        assertThat(artist1).isNotNull();
        assertThat(artist1.name()).isEqualTo("Mizmor");
        assertThat(artist1.biography()).isNull();
        assertThat(artist1.status()).isEqualTo(Status.ACTIVE);
        assertThat(artist1.formationYear()).isEqualTo(2012);
        assertThat(artist1.albums().size()).isEqualTo(0);
        var artist2 = artistService.createArtist("Hell", 2006, Status.ACTIVE, null);
        assertThat(artist2).isNotNull();
        assertThat(artist2.name()).isEqualTo("Hell");
        assertThat(artist2.biography()).isNull();
        assertThat(artist2.status()).isEqualTo(Status.ACTIVE);
        assertThat(artist2.formationYear()).isEqualTo(2006);
        assertThat(artist2.albums().size()).isEqualTo(0);

        var tempSong = new CompleteSongDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Begging to Be Lost", Duration.ofSeconds(980), List.of(artist1.id(), artist2.id()))).doesNotThrowAnyException()
        );
        var song = tempSong[0];
        assertThat(song.id()).isNotNull();
        assertThat(song.name()).isEqualTo("Begging to Be Lost");
        assertThat(song.duration()).isEqualTo(Duration.ofSeconds(980));
        assertThat(song.artists()).size().isEqualTo(2);
        assertThat(song.artists()).contains(
                new CompleteSongDTO.SongArtistDTO(artist1.id(), artist1.name()),
                new CompleteSongDTO.SongArtistDTO(artist2.id(), artist2.name())
        );

    }

    @Test
    void whenDeletingAnArtistWithAlbumsAndSongsThenTheSongsAndAlbumsWillStillExist() {
        var artist = artistService.createArtist("Horrendous", 2009, Status.ACTIVE, null);

        var tempSong = new CompleteSongDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Chrysopoeia (The Archaeology of Dawn)", Duration.ofSeconds(436), List.of(artist.id()))).doesNotThrowAnyException()
        );
        var song1 = tempSong[0];
        assertThat(song1.artists().size()).isEqualTo(1);

        var tempAlbum = new CompleteAlbumDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempAlbum[0] = albumService.createAlbum("Ontological Mysterium", 2023, List.of(song1.id()))).doesNotThrowAnyException()
        );
        var album1 = tempAlbum[0];
        assertThat(album1.artists().size()).isEqualTo(1);
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Soothsayer", Duration.ofSeconds(293), List.of(artist.id()))).doesNotThrowAnyException()
        );
        var song2 = tempSong[0];
        assertThat(song2.artists().size()).isEqualTo(1);

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempAlbum[0] = albumService.createAlbum("Idol", 2018, List.of(song1.id(), song2.id()))).doesNotThrowAnyException()
        );
        var album2 = tempAlbum[0];
        assertThat(album2.artists().size()).isEqualTo(1);

        artistService.deleteArtist(artist.id());

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(() -> {
            var updatedSong1 = songService.findSong(song1.id());
            assertThat(updatedSong1.albums().size()).isEqualTo(2);
            assertThat(updatedSong1.artists()).isEmpty();
            var updatedSong2 = songService.findSong(song2.id());
            assertThat(updatedSong2.albums().size()).isEqualTo(1);
            assertThat(updatedSong2.artists()).isEmpty();
            var updatedAlbum1 = albumService.findAlbum(album1.id());
            assertThat(updatedAlbum1.artists()).isEmpty();
            var updatedAlbum2 = albumService.findAlbum(album2.id());
            assertThat(updatedAlbum2.artists()).isEmpty();
        });
    }

    @Test
    void whenDeletingAnAlbumWithSongsThenTheSongsAndArtistWillStillExist() {
        var artist = artistService.createArtist("Horrendous", 2009, Status.ACTIVE, null);

        var tempSong = new CompleteSongDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Chrysopoeia (The Archaeology of Dawn)", Duration.ofSeconds(436), List.of(artist.id()))).doesNotThrowAnyException()
        );
        var song = tempSong[0];
        assertThat(song.artists().size()).isEqualTo(1);

        var tempAlbum = new CompleteAlbumDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempAlbum[0] = albumService.createAlbum("Ontological Mysterium", 2023, List.of(song.id()))).doesNotThrowAnyException()
        );
        var album = tempAlbum[0];
        assertThat(album.artists().size()).isEqualTo(1);

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedArtist = artistService.findArtist(artist.id());
                    assertThat(updatedArtist.albums().size()).isEqualTo(1);
                }
        );

        albumService.deleteAlbum(album.id());

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedArtist2 = artistService.findArtist(artist.id());
                    assertThat(updatedArtist2.albums()).isEmpty();
                    var updatedSong = songService.findSong(song.id());
                    assertThat(updatedSong.albums()).isEmpty();
                }
        );
    }

    @Test
    void whenDeletingASongThenTheAlbumAndArtistWillStillExist() {
        var artist = artistService.createArtist("Horrendous", 2009, Status.ACTIVE, null);
        var tempSong = new CompleteSongDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Chrysopoeia (The Archaeology of Dawn)", Duration.ofSeconds(436), List.of(artist.id()))).doesNotThrowAnyException()
        );
        var song1 = tempSong[0];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempSong[0] = songService.createSong("Neon Leviathan", Duration.ofSeconds(210), List.of(artist.id()))).doesNotThrowAnyException()
        );
        var song2 = tempSong[0];
        var tempAlbum = new CompleteAlbumDTO[1];
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> assertThatCode(() -> tempAlbum[0] = albumService.createAlbum("Ontological Mysterium", 2023, List.of(song1.id(), song2.id()))).doesNotThrowAnyException()
        );
        var album = tempAlbum[0];
        assertThat(album.songs().size()).isEqualTo(2);

        songService.deleteSong(song1.id());

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    assertThatCode(() -> artistService.findArtist(artist.id())).doesNotThrowAnyException();
                    var updatedAlbum = albumService.findAlbum(album.id());
                    assertThat(updatedAlbum.songs().size()).isEqualTo(1);
                    assertThat(updatedAlbum.artists().size()).isEqualTo(1);
                }
        );

        songService.deleteSong(song2.id());

        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(50)).untilAsserted(
                () -> {
                    var updatedAlbum2 = albumService.findAlbum(album.id());
                    assertThat(updatedAlbum2.songs()).isEmpty();
                    assertThat(updatedAlbum2.artists()).isEmpty();
                    var updatedArtist = artistService.findArtist(artist.id());
                    assertThat(updatedArtist.albums()).isEmpty();
                }
        );
    }
}

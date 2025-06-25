package de.denwae.workshop.spring.modulith.musicdb;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModuleTest {
    @Test
    void verifyModuleStructure() {
        ApplicationModules.of(SpringModulithMusicdbApplication.class).verify();
    }
}

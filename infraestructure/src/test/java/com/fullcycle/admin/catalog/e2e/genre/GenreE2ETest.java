package com.fullcycle.admin.catalog.e2e.genre;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.e2e.MockDsl;
import com.fullcycle.admin.catalog.infraestructure.genre.persistance.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@E2ETest
@Testcontainers
public class GenreE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GenreRepository genreRepository;

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("admin_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MY_SQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithValidValues() throws Exception {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var genreId = givenAGenre(expectedName, expectedIsActive, expectedCategories);

        final var genre = genreRepository.findById(genreId.getValue()).get();

        Assertions.assertEquals(expectedName, genre.getName());
        Assertions.assertEquals(expectedIsActive, genre.isActive());
        Assertions.assertTrue(expectedCategories.containsAll(genre.getCategoryIDs()));
        Assertions.assertNotNull(genre.getCreatedAt());
        Assertions.assertNotNull(genre.getUpdatedAt());
        Assertions.assertNull(genre.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithCategories() throws Exception {
        final var filmes = givenACategory("Filmes", null, true);
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes);
        final var genreId = givenAGenre(expectedName, expectedIsActive, expectedCategories);

        final var genre = genreRepository.findById(genreId.getValue()).get();

        Assertions.assertTrue(expectedCategories.containsAll(genre.getCategoryIDs()));
    }
}

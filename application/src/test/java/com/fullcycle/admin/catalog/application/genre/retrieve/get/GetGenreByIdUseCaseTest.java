package com.fullcycle.admin.catalog.application.genre.retrieve.get;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class GetGenreByIdUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultGetGenreByIdUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    void givenAValidId_whenCallsGetGenre_thenReturnGenre() {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456")
        );
        final var genre = Genre.newGenre("Terror", true).addCategories(expectedCategories);
        final var expectedId = genre.getId();
        when(genreGateway.findById(any())).thenReturn(Optional.of(genre));

        final var result = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedId.getValue(), result.id());
        Assertions.assertEquals(expectedName, result.name());
        Assertions.assertEquals(expectedIsActive, result.isActive());
        Assertions.assertEquals(asString(expectedCategories), result.categories());
        Assertions.assertEquals(genre.getCreatedAt(), result.createdAt());
        Assertions.assertEquals(genre.getUpdatedAt(), result.updatedAt());
        Assertions.assertEquals(genre.getDeletedAt(), result.deletedAt());

        Mockito.verify(genreGateway, times(1)).findById(eq(expectedId));
    }

    @Test
    void givenAValidId_whenCallsGetGenreAndDoesNotExists_thenReturnNotFound() {
        final var expectedId = GenreID.from("123");
        final var expectedErrorMessage = "Genre with ID 123 was not found";
        when(genreGateway.findById(any())).thenReturn(Optional.empty());

        final var exception = Assertions.assertThrows(
            NotFoundException.class,
            () -> { useCase.execute(expectedId.getValue()); }
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }
}

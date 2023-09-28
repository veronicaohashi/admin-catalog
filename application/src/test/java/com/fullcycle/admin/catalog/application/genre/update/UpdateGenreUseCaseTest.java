package com.fullcycle.admin.catalog.application.genre.update;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class UpdateGenreUseCaseTest extends UseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultUpdateGenreUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway, genreGateway);
    }

    @Test
    void givenAValidCommand_whenCallsUpdateGenre_thenReturnGenreId() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var command = UpdateGenreCommand.with(expectedId.getValue(), expectedName, expectedIsActive, asString(expectedCategories));
        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(genreGateway, times(1)).update(argThat( updatedGenre ->
                Objects.equals(expectedName, updatedGenre.getName())
                        && Objects.equals(expectedIsActive, updatedGenre.isActive())
                        && Objects.equals(expectedCategories, updatedGenre.getCategories())
                        && Objects.nonNull(updatedGenre.getId())
                        && Objects.nonNull(updatedGenre.getCreatedAt())
                        && Objects.nonNull(updatedGenre.getUpdatedAt())
                        && Objects.isNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    void givenAValidCommandWithCategories_whenCallsUpdateGenre_thenReturnGenreId() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456")
        );
        final var command = UpdateGenreCommand.with(expectedId.getValue(), expectedName, expectedIsActive, asString(expectedCategories));
        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));
        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(genreGateway, times(1)).update(argThat( updatedGenre ->
                Objects.equals(expectedCategories, updatedGenre.getCategories())
        ));
    }

    @Test
    void givenAValidCommandWithInactiveGenre_whenCallsUpdateGenre_thenReturnGenreId() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();
        final var expectedIsActive = false;
        final var command = UpdateGenreCommand.with(expectedId.getValue(), "Terror", expectedIsActive, asString(List.of()));
        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));
        when(genreGateway.update(any())).thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(genreGateway, times(1)).update(argThat(updatedGenre ->
                Objects.equals(expectedIsActive, updatedGenre.isActive())
                        && Objects.nonNull(updatedGenre.getDeletedAt())
        ));
    }

    @Test
    void givenAInvalidEmptyName_whenCallsUpdateGenre_thenReturnDomainException() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();
        final var expectedErrorMessage = "'name' should not be empty";
        final var command = UpdateGenreCommand.with(expectedId.getValue(), " ", true, asString(List.of()));
        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).update(any());
    }

    @Test
    void givenAInvalidNullName_whenCallsUpdateGenre_thenReturnDomainException() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();
        final var expectedErrorMessage = "'name' should not be null";
        final var command = UpdateGenreCommand.with(expectedId.getValue(), null, true, asString(List.of()));
        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).update(any());
    }

    @Test
    void givenAValidCommand_whenCallsUpdateGenreAndSomeCategoriesDoesNotExists_thenReturnNotificationException() {
        final var genre = Genre.newGenre("Terror", true);
        final var expectedId = genre.getId();
        final var series = CategoryID.from("123");
        final var filmes = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");
        final var expectedErrorMessage = "Some categories could not be found: 456, 789";
        final var command = UpdateGenreCommand.with(expectedId.getValue(),"Ação", true, asString(List.of(series, filmes, documentarios)));
        when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(genre)));
        when(categoryGateway.existsByIds(any())).thenReturn(List.of(series));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).existsByIds(any());
        verify(genreGateway, times(0)).update(any());
    }

    @Test
    void givenACommandWithInvalidID_whenCallsUpdateGenre_thenReturnNotFoundException() {
        final var expectedId = "123";
        final var command = UpdateGenreCommand.with(expectedId, "Terror", true, List.of());
        final var expectedErrorMessage = "Genre with ID 123 was not found";
        when(genreGateway.findById(eq(GenreID.from(command.id()))))
                .thenReturn(Optional.empty());

        final var notification = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, notification.getMessage());
        verify(genreGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).update(any());
    }
}

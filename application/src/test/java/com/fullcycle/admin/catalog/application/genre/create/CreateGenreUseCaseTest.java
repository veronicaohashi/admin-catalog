package com.fullcycle.admin.catalog.application.genre.create;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGenreUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultCreateGenreUseCase useCase;

    @Test
    void givenAValidCommand_whenCallsCreateGenre_thenReturnGenreId() {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));
        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(genreGateway, times(1)).create(argThat(genre ->
                Objects.equals(expectedName, genre.getName())
                        && Objects.equals(expectedIsActive, genre.isActive())
                        && Objects.equals(expectedCategories, genre.getCategories())
                        && Objects.nonNull(genre.getId())
                        && Objects.nonNull(genre.getCreatedAt())
                        && Objects.nonNull(genre.getUpdatedAt())
                        && Objects.isNull(genre.getDeletedAt())
        ));
    }

    @Test
    void givenAValidCommandWithCategories_whenCallsCreateGenre_thenReturnGenreId() {
        final var expectedName = "Terror";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456")
        );
        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));
        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);
        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(genreGateway, times(1)).create(argThat(genre ->
                Objects.equals(expectedCategories, genre.getCategories())
        ));
    }

    @Test
    void givenAValidCommandWithInactiveGenre_whenCallsCreateGenre_thenReturnGenreId() {
        final var expectedName = "Terror";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();
        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));
        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        verify(genreGateway, times(1)).create(argThat(genre ->
                Objects.equals(expectedIsActive, genre.isActive())
                        && Objects.nonNull(genre.getDeletedAt())
        ));
    }

    @Test
    void givenAInvalidEmptyName_whenCallsCreateGenre_thenReturnDomainException() {
        final var expectedErrorMessage = "'name' should not be empty";
        final var command = CreateGenreCommand.with(" ", true, asString(List.of()));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(genreGateway, times(0)).create(any());
        verify(categoryGateway, times(0)).existsByIds(any());
    }

    @Test
    void givenAInvalidNullName_whenCallsCreateGenre_thenReturnDomainException() {
        final var expectedErrorMessage = "'name' should not be null";
        final var command = CreateGenreCommand.with(null, true, asString(List.of()));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(genreGateway, times(0)).create(any());
        verify(categoryGateway, times(0)).existsByIds(any());
    }

    @Test
    void givenAValidCommand_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_thenReturnNotificationException() {
        final var series = CategoryID.from("123");
        final var filmes = CategoryID.from("456");
        final var documentarios = CategoryID.from("789");
        final var expectedErrorMessage = "Some categories could not be found: 456, 789";
        final var command = CreateGenreCommand.with("Ação", true, asString(List.of(series, filmes, documentarios)));
        when(categoryGateway.existsByIds(any())).thenReturn(List.of(series));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertNotNull(exception);
        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(genreGateway, times(0)).create(any());
        verify(categoryGateway, times(1)).existsByIds(any());
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
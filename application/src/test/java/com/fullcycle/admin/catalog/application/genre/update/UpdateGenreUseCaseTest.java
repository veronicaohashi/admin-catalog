package com.fullcycle.admin.catalog.application.genre.update;

import com.fullcycle.admin.catalog.application.genre.create.CreateGenreCommand;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateGenreUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultUpdateGenreUseCase useCase;

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

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}

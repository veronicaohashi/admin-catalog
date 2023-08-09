package com.fullcycle.admin.catalog.application.genre.create;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
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

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
package com.fullcycle.admin.catalog.application.video.retrieve.list;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.video.Video;
import com.fullcycle.admin.catalog.domain.video.VideoGateway;
import com.fullcycle.admin.catalog.domain.video.VideoSearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ListVideoUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultListVideoUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    void givenAValidQuery_whenCallsListVideos_shouldReturnVideos() {
        final var expectedVideos = List.of(
                Fixture.video(),
                Fixture.video()
        );
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;
        final var expectedItems = expectedVideos.stream()
                .map(VideoListOutput::from)
                .toList();
        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedVideos);
        final var query = new VideoSearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        when(videoGateway.findAll(any()))
                .thenReturn(expectedPagination);

        final var result = useCase.execute(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedItems, result.items());
        verify(videoGateway, times(1)).findAll(eq(query));
    }

    @Test
    void givenAValidQuery_whenCallsListVideosAndResultIsEmpty_shouldReturnGenres() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var expectedItems = List.<VideoListOutput>of();
        final var expectedPagination = new Pagination<>(expectedPage, expectedPerPage, expectedTotal, List.<Video>of());
        final var query = new VideoSearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        when(videoGateway.findAll(any()))
                .thenReturn(expectedPagination);

        final var result = useCase.execute(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedItems, result.items());
        verify(videoGateway, times(1)).findAll(eq(query));
    }

    @Test
    void givenAValidQuery_whenCallsListVideosAndGatewayThrowsRandomError_shouldReturnException() {
        final var expectedErrorMessage = "Gateway error";
            final var query = new VideoSearchQuery(0, 10, "", "", "");
        when(videoGateway.findAll(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var error = Assertions.assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(query)
        );

        Assertions.assertEquals(expectedErrorMessage, error.getMessage());
        verify(videoGateway, times(1)).findAll(eq(query));

    }
}

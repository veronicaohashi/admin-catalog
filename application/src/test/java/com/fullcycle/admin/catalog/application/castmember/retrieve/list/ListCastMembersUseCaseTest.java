package com.fullcycle.admin.catalog.application.castmember.retrieve.list;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListCastMembersUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultListCastMembersUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    void givenAValidQuery_whenCallsListCastMembers_thenReturnAll() {
        final var members = List.of(
                CastMember.newMember(Fixture.name(), Fixture.CastMember.type()),
                CastMember.newMember(Fixture.name(), Fixture.CastMember.type())
        );
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;
        final var expectedItems = members.stream()
                .map(CastMemberListOutput::from)
                .toList();
        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                members
        );

        when(castMemberGateway.findAll(any()))
                .thenReturn(expectedPagination);

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var response = useCase.execute(query);

        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
        Assertions.assertEquals(expectedItems, response.items());
        verify(castMemberGateway).findAll(eq(query));
    }

    @Test
    void givenAValidQuery_whenCallsListCastMembersAndIsEmpty_thenReturn() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var members = List.<CastMember>of();
        final var expectedItems = List.<CastMemberListOutput>of();
        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                members
        );

        when(castMemberGateway.findAll(any()))
                .thenReturn(expectedPagination);

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var response = useCase.execute(query);

        Assertions.assertEquals(expectedPage, response.currentPage());
        Assertions.assertEquals(expectedPerPage, response.perPage());
        Assertions.assertEquals(expectedTotal, response.total());
        Assertions.assertEquals(expectedItems, response.items());

        verify(castMemberGateway).findAll(eq(query));
    }

    @Test
    void givenAValidQuery_whenCallsListCastMembersAndGatewayThrowsRandomException_thenException() {
        final var expectedErrorMessage = "Gateway error";
        final var query =
                new SearchQuery(0, 10, "", "createdAt", "desc");
        when(castMemberGateway.findAll(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var actualException = Assertions.assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(query)
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        verify(castMemberGateway).findAll(eq(query));
    }
}
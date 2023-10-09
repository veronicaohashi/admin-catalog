package com.fullcycle.admin.catalog.application.castmember.retrieve.get;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetCastMembersByIdUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultGetCastMemberByIdUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    void givenAValidId_whenCallsGetCastMember_thenReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(member));

        final var response = useCase.execute(expectedId.getValue());

        Assertions.assertEquals(expectedId.getValue(), response.id());
        Assertions.assertEquals(expectedName, response.name());
        Assertions.assertEquals(expectedType, response.type());
        Assertions.assertEquals(member.getCreatedAt(), response.createdAt());
        Assertions.assertEquals(member.getUpdatedAt(), response.updatedAt());
        verify(castMemberGateway).findById(eq(expectedId));
    }

    @Test
    void givenAInvalidId_whenCallsGetCastMemberAndDoesNotExists_thenReturnNotFoundException() {
        final var expectedId = CastMemberID.from("123");
        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
        verify(castMemberGateway).findById(eq(expectedId));
    }
}
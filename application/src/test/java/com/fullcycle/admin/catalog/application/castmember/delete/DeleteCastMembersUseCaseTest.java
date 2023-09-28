package com.fullcycle.admin.catalog.application.castmember.delete;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteCastMembersUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultDeleteCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    void givenAValidId_whenCallsDeleteCastMember_thenDeleteIt() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = member.getId();
        doNothing().when(castMemberGateway).deleteById(any());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(castMemberGateway).deleteById(any());
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCastMember_thenBeOk() {
        final var expectedId = CastMemberID.from("123");
        doNothing().when(castMemberGateway).deleteById(any());

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(castMemberGateway).deleteById(any());
    }

    @Test
    void givenAValidId_whenCallsDeleteCastMemberAndGatewayThrowsException_thenReceiveException() {
        final var expectedId = CastMemberID.from("123");
        doThrow(new IllegalStateException("Gateway error"))
            .when(castMemberGateway).deleteById(any());

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        verify(castMemberGateway).deleteById(any());
    }
}
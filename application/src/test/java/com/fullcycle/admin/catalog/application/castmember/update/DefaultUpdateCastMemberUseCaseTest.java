package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DefaultUpdateCastMemberUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultUpdateCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCastMember_thenReturnItsIdentifier() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var expectedId = member.getId();
        final var command = UpdateCastMemberCommand.with(expectedId.getValue(), expectedName, expectedType);
        Mockito.when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(CastMember.with(member)));
        Mockito.when(castMemberGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command);

        Assertions.assertEquals(expectedId.getValue(), response.id());
        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway, times(1)).update(argThat(updatedMember ->
                        Objects.equals(expectedName, updatedMember.getName()) &&
                                Objects.equals(expectedType, updatedMember.getType()) &&
                                Objects.nonNull(updatedMember.getCreatedAt()) &&
                                Objects.nonNull(updatedMember.getUpdatedAt())
                ));
    }

    @Test
    void givenAInvalidName_whenCallsUpdateCastMember_thenThrowsNotificationException() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var id = member.getId();
        final var expectedErrorMessage = "'name' should not be null";
        final var command = UpdateCastMemberCommand.with(id.getValue(), null, Fixture.CastMember.type());
        Mockito.when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(CastMember.with(member)));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    void givenAInvalidType_whenCallsUpdateCastMember_thenThrowsNotificationException() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var id = member.getId();
        final var expectedErrorMessage = "'type' should not be null";
        final var command = UpdateCastMemberCommand.with(id.getValue(), Fixture.name(), null);
        Mockito.when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(CastMember.with(member)));

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    void givenAInvalidId_whenCallsUpdateCastMember_thenThrowsNotFoundException() {
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var command = UpdateCastMemberCommand.with("123", Fixture.name(), null);
        Mockito.when(castMemberGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }
}
package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class CreateCastMemberUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultCreateCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    void givenAValidCommand_whenCallsCreateCastMember_thenReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var command = CreateCastMemberCommand.with(expectedName, expectedType);
        when(castMemberGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var response = useCase.execute(command);

        Assertions.assertNotNull(response.id());
        verify(castMemberGateway).create(argThat(member ->
                        Objects.nonNull(member.getId()) &&
                                Objects.equals(expectedName, member.getName()) &&
                                Objects.equals(expectedType, member.getType()) &&
                                Objects.nonNull(member.getCreatedAt()) &&
                                Objects.nonNull(member.getUpdatedAt())
                ));

    }

    @Test
    void givenAInvalidName_whenCallsCreateCastMember_thenThrowsNotificationException() {
        final var expectedErrorMessage = "'name' should not be null";
        final var command = CreateCastMemberCommand.with(null, Fixture.CastMember.type());

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(castMemberGateway, times(0)).create(any());
    }

    @Test
    void givenAInvalidType_whenCallsCreateCastMember_thenThrowsNotificationException() {
        final var expectedErrorMessage = "'type' should not be null";
        final var command = CreateCastMemberCommand.with(Fixture.name(), null);

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(castMemberGateway, times(0)).create(any());

    }
}

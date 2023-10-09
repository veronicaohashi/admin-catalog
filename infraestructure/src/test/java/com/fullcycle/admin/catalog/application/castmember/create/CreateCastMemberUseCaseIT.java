package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class CreateCastMemberUseCaseIT {

    @Autowired
    private CreateCastMemberUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCommand_whenCallsCreateCastMember_thenReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        final var result = useCase.execute(command);

        final var persistedCastMember= castMemberRepository.findById(result.id()).get();
        Assertions.assertEquals(expectedName, persistedCastMember.getName());
        Assertions.assertEquals(expectedType, persistedCastMember.getType());
        Assertions.assertNotNull(persistedCastMember.getCreatedAt());
        Assertions.assertNotNull(persistedCastMember.getUpdatedAt());
        Assertions.assertEquals(persistedCastMember.getCreatedAt(), persistedCastMember.getUpdatedAt());

        verify(castMemberGateway).create(any());
    }

    @Test
    void givenAInvalidName_whenCallsCreateCastMember_thenThrowsNotificationException() {
        final var expectedErrorMessage = "'name' should not be null";
        final var command = CreateCastMemberCommand.with(null, Fixture.CastMembers.type());

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

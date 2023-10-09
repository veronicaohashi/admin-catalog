package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.exception.NotFoundException;
import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
class UpdateCastMemberUseCaseIT {

    @Autowired
    private UpdateCastMemberUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCastMember_thenReturnItsIdentifier() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var expectedId = member.getId();
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));
        final var command = UpdateCastMemberCommand.with(expectedId.getValue(), expectedName, expectedType);

        useCase.execute(command);

        final var persistedMember = castMemberRepository.findById(expectedId.getValue()).get();
        Assertions.assertEquals(expectedName, persistedMember.getName());
        Assertions.assertEquals(expectedType, persistedMember.getType());
        Assertions.assertEquals(member.getCreatedAt(), persistedMember.getCreatedAt());
        Assertions.assertTrue(member.getUpdatedAt().isBefore(persistedMember.getUpdatedAt()));

        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway).update(any());
    }

    @Test
    void givenAInvalidName_whenCallsUpdateCastMember_thenThrowsNotificationException() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = member.getId();
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));
        final var expectedErrorMessage = "'name' should not be null";
        final var command = UpdateCastMemberCommand.with(expectedId.getValue(), null, Fixture.CastMembers.type());

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    void givenAInvalidType_whenCallsUpdateCastMember_thenThrowsNotificationException() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = member.getId();
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));
        final var expectedErrorMessage = "'type' should not be null";
        final var command = UpdateCastMemberCommand.with(expectedId.getValue(), Fixture.name(), null);

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    void givenAInvalidId_whenCallsUpdateCastMember_thenThrowsNotFoundException() {
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var id = CastMemberID.from("123");
        final var command = UpdateCastMemberCommand.with(id.getValue(), Fixture.name(), null);

        final var exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
        verify(castMemberGateway).findById(eq(id));
        verify(castMemberGateway, times(0)).update(any());
    }
}
package com.fullcycle.admin.catalog.domain.castmember;

import com.fullcycle.admin.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.fullcycle.admin.catalog.domain.castmember.CastMemberType.ACTOR;
import static com.fullcycle.admin.catalog.domain.castmember.CastMemberType.DIRECTOR;

class CastMemberTest {

    @Test
    void givenAValidParams_whenCallsNewMember_thenInstantiateACastMember() {
        final var expectedName = "Vin Diesel";
        final var expectedType = ACTOR;

        final var castmember = CastMember.newMember(expectedName, expectedType);

        Assertions.assertNotNull(castmember);
        Assertions.assertNotNull(castmember.getId());
        Assertions.assertEquals(expectedName, castmember.getName());
        Assertions.assertEquals(expectedType, castmember.getType());
        Assertions.assertNotNull(castmember.getCreatedAt());
        Assertions.assertNotNull(castmember.getUpdatedAt());
    }

    @Test
    void givenAInvalidNullName_whenCallsNewMember_thenReceiveANotification() {
        final var expectedErrorMessage = "'name' should not be null";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(null, ACTOR)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidEmptyName_whenCallsNewMember_thenReceiveANotification() {
        final var expectedErrorMessage = "'name' should not be empty";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> CastMember.newMember("", ACTOR)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNameWithLengthMoreThan255_whenCallsNewMember_thenReceiveANotification() {
        final var invalidName = """
                A certificação de metodologias que nos auxiliam a lidar com a crescente influência da mídia talvez venha
                 a ressaltar a relatividade do processo de comunicação como um todo. Todas estas questões, devidamente 
                 ponderadas, levantam dúvidas sobre se o surgimento do comércio virtual promove a alavancagem do sistema 
                 de participação geral.                                                                                     
                """;
        final var expectedErrorMessage = "'name' must be between 3 and 255 character";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(invalidName, ACTOR)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAInvalidNullType_whenCallsNewMember_thenReceiveANotification() {
        final var expectedErrorMessage = "'type' should not be null";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> CastMember.newMember("Vin Diesel", null)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

    }

    @Test
    void givenAValidCastMember_whenCallUpdate_thenReceiveUpdated() {
        final var expectedName = "Vin Diesel";
        final var expectedType = ACTOR;
        final var castmember = CastMember.newMember("Vin", DIRECTOR);
        final var memberUpdatedAt = castmember.getUpdatedAt();

        castmember.update(expectedName, expectedType);

        Assertions.assertNotNull(castmember.getId());
        Assertions.assertEquals(expectedName, castmember.getName());
        Assertions.assertEquals(expectedType, castmember.getType());
        Assertions.assertNotNull(castmember.getCreatedAt());
        Assertions.assertTrue(castmember.getUpdatedAt().isAfter(memberUpdatedAt));
    }

    @Test
    void givenAValidCastMember_whenCallUpdateWithInvalidNullName_thenReceiveNotification() {
        final var castmember = CastMember.newMember("Vin Diesel", ACTOR);
        final var expectedErrorMessage = "'name' should not be null";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> castmember.update(null, ACTOR)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAValidCastMember_whenCallUpdateWithInvalidEmptyName_thenReceiveNotification() {
        final var castmember = CastMember.newMember("Vin Diesel", ACTOR);
        final var expectedErrorMessage = "'name' should not be empty";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> castmember.update("", ACTOR)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAValidCastMember_whenCallUpdateWithLengthMoreThan255_theReceiveNotification() {
        final var castmember = CastMember.newMember("Vin Diesel", ACTOR);
        final var invalidName = """
                A certificação de metodologias que nos auxiliam a lidar com a crescente influência da mídia talvez venha
                 a ressaltar a relatividade do processo de comunicação como um todo. Todas estas questões, devidamente 
                 ponderadas, levantam dúvidas sobre se o surgimento do comércio virtual promove a alavancagem do sistema 
                 de participação geral.                                                                                     
                """;
        final var expectedErrorMessage = "'name' must be between 3 and 255 character";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> castmember.update(invalidName, ACTOR)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void givenAValidCastMember_whenCallUpdateWithInvalidNullType_thenReceiveNotification() {
        final var castmember = CastMember.newMember("Vin Diesel", ACTOR);
        final var expectedErrorMessage = "'type' should not be null";

        final var exception = Assertions.assertThrows(
                NotificationException.class,
                () -> castmember.update("Vin Diesel", null)
        );

        Assertions.assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

    }
}
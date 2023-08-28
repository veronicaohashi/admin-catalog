package com.fullcycle.admin.catalog.domain.castmember;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CastMemberTest {

    @Test
    void givenAValidParams_whenCallsNewMember_thenInstantiateACastMember() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var castmember = CastMember.newMember(expectedName, expectedType);

        Assertions.assertNotNull(castmember);
        Assertions.assertNotNull(castmember.getId());
        Assertions.assertEquals(expectedName, castmember.getName());
        Assertions.assertEquals(expectedType, castmember.getType());
        Assertions.assertNotNull(castmember.getCreatedAt());
        Assertions.assertNotNull(castmember.getUpdatedAt());
    }
}
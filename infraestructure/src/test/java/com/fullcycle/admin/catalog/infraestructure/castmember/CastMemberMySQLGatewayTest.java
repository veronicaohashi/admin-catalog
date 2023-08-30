package com.fullcycle.admin.catalog.infraestructure.castmember;

import com.fullcycle.admin.catalog.Fixture;
import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@MySQLGatewayTest
class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private CastMemberMySQLGateway castMemberMySQLGateway;

    @Test
    void givenAValidCastMember_whenCallsCreate_thenPersistIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        final var response = castMemberMySQLGateway.create(member);

        Assertions.assertEquals(expectedId, response.getId());
        Assertions.assertEquals(expectedName, response.getName());
        Assertions.assertEquals(expectedType, response.getType());
        Assertions.assertEquals(member.getCreatedAt(), response.getCreatedAt());
        Assertions.assertEquals(member.getUpdatedAt(), response.getUpdatedAt());

        final var persistedMember = castMemberRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedId.getValue(), persistedMember.getId());
        Assertions.assertEquals(expectedName, persistedMember.getName());
        Assertions.assertEquals(expectedType, persistedMember.getType());
        Assertions.assertEquals(member.getCreatedAt(), persistedMember.getCreatedAt());
        Assertions.assertEquals(member.getUpdatedAt(), persistedMember.getUpdatedAt());
    }
}
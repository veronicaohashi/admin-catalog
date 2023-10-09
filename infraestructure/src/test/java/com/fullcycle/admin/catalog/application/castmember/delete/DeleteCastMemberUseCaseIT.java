package com.fullcycle.admin.catalog.application.castmember.delete;

import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
class DeleteCastMemberUseCaseIT {

    @Autowired
    private DeleteCastMemberUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    void givenAValidId_whenCallsDeleteCastMember_thenDeleteIt() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var memberTwo = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = member.getId();
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(memberTwo));

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(1, castMemberRepository.count());
        verify(castMemberGateway).deleteById(eq(expectedId));
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCastMember_thenBeOk() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));
        final var expectedId = CastMemberID.from("123");

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Assertions.assertEquals(1, castMemberRepository.count());
        verify(castMemberGateway).deleteById(eq(expectedId));
    }

    @Test
    void givenAValidId_whenCallsDeleteCastMemberAndGatewayThrowsException_thenReceiveException() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));
        final var expectedId = CastMemberID.from("123");
        doThrow(new IllegalStateException("Gateway error"))
            .when(castMemberGateway).deleteById(any());

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(1, castMemberRepository.count());
        verify(castMemberGateway).deleteById(eq(expectedId));
    }
}
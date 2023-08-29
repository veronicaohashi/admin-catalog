package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}

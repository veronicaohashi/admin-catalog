package com.fullcycle.admin.catalog.infraestructure.castmember.models;

import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;

@JacksonTest
class CastMemberResponseTest {

    @Autowired
    private JacksonTester<CastMemberResponse> json;

    @Test
    void testMarshall() throws IOException {
        final var expectedID = "123";
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type().name();
        final var expectedCreatedAt = Instant.now().toString();
        final var expectedUpdatedAt = Instant.now().toString();

        final var response = new CastMemberResponse(
                expectedID,
                expectedName,
                expectedType,
                expectedCreatedAt,
                expectedUpdatedAt
        );

        final var result = this.json.write(response);

        Assertions.assertThat(result)
                .hasJsonPathValue("$.id", expectedID)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.type", expectedType)
                .hasJsonPathValue("$.created_at", expectedCreatedAt)
                .hasJsonPathValue("$.updated_at", expectedUpdatedAt);
    }
}

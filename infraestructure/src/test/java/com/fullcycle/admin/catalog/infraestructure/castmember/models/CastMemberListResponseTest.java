package com.fullcycle.admin.catalog.infraestructure.castmember.models;

import com.fullcycle.admin.catalog.Fixture;
import com.fullcycle.admin.catalog.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;

@JacksonTest
class CastMemberListResponseTest {

    @Autowired
    private JacksonTester<CastMemberListResponse> json;

    @Test
    void testMarshall() throws IOException {
        final var expectedID = "123";
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type().name();
        final var expectedCreatedAt = Instant.now().toString();
        final var response = new CastMemberListResponse(
                expectedID,
                expectedName,
                expectedType,
                expectedCreatedAt
        );

        final var result = json.write(response);

        Assertions.assertThat(result)
                .hasJsonPathValue("$.id", expectedID)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.type", expectedType)
                .hasJsonPathValue("$.created_at", expectedCreatedAt);
    }
}

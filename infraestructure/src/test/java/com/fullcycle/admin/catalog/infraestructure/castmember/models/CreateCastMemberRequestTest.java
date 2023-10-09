package com.fullcycle.admin.catalog.infraestructure.castmember.models;

import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

@JacksonTest
class CreateCastMemberRequestTest {

    @Autowired
    private JacksonTester<CreateCastMemberRequest> json;

    @Test
    void testUnmarshall() throws IOException {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var request = """
                {
                    "name": "%s",
                    "type": "%s"
                }
                """.formatted(expectedName, expectedType);

        final var result = json.parse(request);

        Assertions.assertThat(result)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("type", expectedType);
    }
}

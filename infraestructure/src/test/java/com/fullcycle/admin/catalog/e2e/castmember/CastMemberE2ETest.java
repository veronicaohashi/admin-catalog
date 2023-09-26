package com.fullcycle.admin.catalog.e2e.castmember;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.Fixture;
import com.fullcycle.admin.catalog.e2e.MockDsl;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@E2ETest
@Testcontainers
class CastMemberE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("admin_videos");

    @DynamicPropertySource
    private static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MY_SQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewCastMemberWithValidValues() throws Exception {
        Assertions.assertEquals(0, castMemberRepository.count());
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var castMemberID = givenACastMember(expectedName, expectedType);
        final var castMember = castMemberRepository.findById(castMemberID.getValue()).get();

        Assertions.assertEquals(expectedName, castMember.getName());
        Assertions.assertEquals(expectedType, castMember.getType());
        Assertions.assertNotNull(castMember.getCreatedAt());
        Assertions.assertNotNull(castMember.getUpdatedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSeeATreatedErrorByCreatingANewCastMemberWithInvalidValues() throws Exception {
        Assertions.assertEquals(0, castMemberRepository.count());
        final var expectedErrorMessage = "'name' should not be null";

        givenACastMemberResult(null, Fixture.CastMember.type())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0].message", Matchers.equalTo(expectedErrorMessage)));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToNavigateThruAllMembers() throws Exception {
        givenACastMember("Vin Diesel", Fixture.CastMember.type());
        givenACastMember("Quentin Tarantino", Fixture.CastMember.type());
        givenACastMember("Jason Momoa", Fixture.CastMember.type());

        listCastMembers(0, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(0)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Jason Momoa")));

        listCastMembers(1, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Quentin Tarantino")));

        listCastMembers(2, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(2)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Vin Diesel")));

        listCastMembers(3, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(0)));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSearchThruAllMembers() throws Exception {
        givenACastMember("Vin Diesel", Fixture.CastMember.type());
        givenACastMember("Quentin Tarantino", Fixture.CastMember.type());
        givenACastMember("Jason Momoa", Fixture.CastMember.type());

        listCastMembers(0, 1, "vin")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(0)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Vin Diesel")));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToSortAllMembersByNameDesc() throws Exception {
        givenACastMember("Vin Diesel", Fixture.CastMember.type());
        givenACastMember("Quentin Tarantino", Fixture.CastMember.type());
        givenACastMember("Jason Momoa", Fixture.CastMember.type());

        listCastMembers(0, 1, "", "name", "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", Matchers.equalTo(0)))
                .andExpect(jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", Matchers.equalTo("Vin Diesel")));
    }
}

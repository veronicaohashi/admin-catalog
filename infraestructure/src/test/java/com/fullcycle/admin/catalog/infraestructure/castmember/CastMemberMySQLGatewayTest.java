package com.fullcycle.admin.catalog.infraestructure.castmember;

import com.fullcycle.admin.catalog.domain.Fixture;
import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.fullcycle.admin.catalog.domain.castmember.CastMemberType.ACTOR;
import static com.fullcycle.admin.catalog.domain.castmember.CastMemberType.DIRECTOR;

@MySQLGatewayTest
class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Autowired
    private CastMemberMySQLGateway castMemberMySQLGateway;

    @Test
    void givenAValidCastMember_whenCallsCreate_thenPersistIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
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

    @Test
    void givenAValidCastMember_whenCallsUpdate_thenRefreshIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = member.getId();
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        final var response = castMemberMySQLGateway.update(
                CastMember.with(member).update(expectedName, expectedType)
        );

        Assertions.assertEquals(expectedId, response.getId());
        Assertions.assertEquals(expectedName, response.getName());
        Assertions.assertEquals(expectedType, response.getType());
        Assertions.assertEquals(member.getCreatedAt(), response.getCreatedAt());
        Assertions.assertTrue(member.getUpdatedAt().isBefore(response.getUpdatedAt()));

        final var persistedMember = castMemberRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedId.getValue(), persistedMember.getId());
        Assertions.assertEquals(expectedName, persistedMember.getName());
        Assertions.assertEquals(expectedType, persistedMember.getType());
        Assertions.assertEquals(member.getCreatedAt(), persistedMember.getCreatedAt());
        Assertions.assertTrue(member.getUpdatedAt().isBefore(response.getUpdatedAt()));
    }

    @Test
    void givenAValidCastMember_whenCallsDeleteById_shouldDeleteIt() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());
        final var expectedId = member.getId();
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        castMemberMySQLGateway.deleteById(expectedId);

        Assertions.assertEquals(0, castMemberRepository.count());
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteById_thenBeIgnored() {
        castMemberMySQLGateway.deleteById(CastMemberID.from("123"));

        Assertions.assertEquals(0, castMemberRepository.count());
    }

    @Test
    void givenAValidCastMember_whenCallsFindById_thenReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        final var response = castMemberMySQLGateway.findById(expectedId).get();

        Assertions.assertEquals(expectedId, response.getId());
        Assertions.assertEquals(expectedName, response.getName());
        Assertions.assertEquals(expectedType, response.getType());
        Assertions.assertEquals(member.getCreatedAt(), response.getCreatedAt());
        Assertions.assertEquals(member.getUpdatedAt(), response.getUpdatedAt());
    }

    @Test
    void givenAnInvalidId_whenCallsFindById_thenReturnEmpty() {
        final var response = castMemberMySQLGateway.findById(CastMemberID.from("123"));

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void givenEmptyCastMembers_whenCallsFindAll_thenReturnEmpty() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = castMemberMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedTotal, result.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "vin,0,10,1,1,Vin Diesel",
            "taran,0,10,1,1,Quentin Tarantino",
            "jas,0,10,1,1,Jason Momoa",
            "har,0,10,1,1,Kit Harington",
            "MAR,0,10,1,1,Martin Scorsese",
    })
    void givenAValidTerm_whenCallsFindAll_thenReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ){
        mockMembers();
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = castMemberMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedItemsCount, result.items().size());
        Assertions.assertEquals(expectedName, result.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Jason Momoa",
            "name,desc,0,10,5,5,Vin Diesel",
            "createdAt,asc,0,10,5,5,Kit Harington",
            "createdAt,desc,0,10,5,5,Martin Scorsese",
    })
    void givenAValidSortAndDirection_whenCallsFindAll_thenReturnSorted(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        mockMembers();
        final var expectedTerms = "";
        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = castMemberMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedItemsCount, result.items().size());
        Assertions.assertEquals(expectedName, result.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Jason Momoa;Kit Harington",
            "1,2,2,5,Martin Scorsese;Quentin Tarantino",
            "2,2,1,5,Vin Diesel",
    })
    void givenAValidPagination_whenCallsFindAll_thenReturnPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedNames
    ) {
        mockMembers();
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = castMemberMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedItemsCount, result.items().size());
        int index = 0;
        for (final var expectedName : expectedNames.split(";")) {
            Assertions.assertEquals(expectedName, result.items().get(index).getName());
            index++;
        }
    }

    private void mockMembers() {
        castMemberRepository.saveAllAndFlush(List.of(
            CastMemberJpaEntity.from(CastMember.newMember("Kit Harington", ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Vin Diesel", ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Quentin Tarantino", DIRECTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Jason Momoa", ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Martin Scorsese", DIRECTOR))
        ));
    }
}
package com.fullcycle.admin.catalog.infraestructure.castmember;

import com.fullcycle.admin.catalog.domain.castmember.CastMember;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CastMemberMySQLGateway implements CastMemberGateway {

    private final CastMemberRepository castMemberRepository;

    public CastMemberMySQLGateway(final CastMemberRepository castMemberRepository) {
        this.castMemberRepository = Objects.requireNonNull(castMemberRepository);
    }

    @Override
    public CastMember create(final CastMember castMember) {
        return save(castMember);
    }

    @Override
    public void deleteById(final CastMemberID id) {
        final var memberID = id.getValue();
        if(castMemberRepository.existsById(memberID)) {
            castMemberRepository.deleteById(memberID);
        }
    }

    @Override
    public Optional<CastMember> findById(final CastMemberID id) {
        return castMemberRepository.findById(id.getValue())
                .map(CastMemberJpaEntity::toAggregate);
    }

    @Override
    public CastMember update(final CastMember castMember) {
        return save(castMember);
    }

    @Override
    public Pagination<CastMember> findAll(SearchQuery searchQuery) {
        return null;
    }

    private CastMember save(CastMember castMember) {
        return castMemberRepository.save(CastMemberJpaEntity.from(castMember)).toAggregate();
    }
}

package com.fullcycle.admin.catalog.domain.castmember;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;

import java.util.List;
import java.util.Optional;

public interface CastMemberGateway {

    CastMember create(CastMember castMember);

    void deleteById(CastMemberID id);

    Optional<CastMember> findById(CastMemberID id);

    CastMember update(CastMember castMember);

    Pagination<CastMember> findAll(SearchQuery searchQuery);

    List<CastMemberID> existsByIds(final Iterable<CastMemberID> ids);
}

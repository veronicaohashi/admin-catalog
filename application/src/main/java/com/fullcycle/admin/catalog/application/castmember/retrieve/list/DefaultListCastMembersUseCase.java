package com.fullcycle.admin.catalog.application.castmember.retrieve.list;

import com.fullcycle.admin.catalog.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;

import java.util.Objects;

public final class DefaultListCastMembersUseCase extends ListCastMembersUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultListCastMembersUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public Pagination<CastMemberListOutput> execute(SearchQuery searchQuery) {
        return castMemberGateway.findAll(searchQuery)
                .map(CastMemberListOutput::from);
    }
}

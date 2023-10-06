package com.fullcycle.admin.catalog.domain.castmember;

import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.utils.IdUtils;

import java.util.Objects;

public class CastMemberID extends Identifier {

    private final String value;

    public CastMemberID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static CastMemberID unique() {
        return CastMemberID.from(IdUtils.uuid());
    }

    public static CastMemberID from(final String id) {
        return new CastMemberID(id);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CastMemberID that = (CastMemberID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}

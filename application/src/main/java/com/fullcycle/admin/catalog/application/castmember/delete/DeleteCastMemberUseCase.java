package com.fullcycle.admin.catalog.application.castmember.delete;

import com.fullcycle.admin.catalog.application.UnitUseCase;

public sealed abstract class DeleteCastMemberUseCase
        extends UnitUseCase<String>
        permits DefaultDeleteCastMemberUseCase {
}

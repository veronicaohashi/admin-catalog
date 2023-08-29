package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.application.UseCase;

public sealed abstract class CreateCastMemberUseCase
        extends UseCase<CreateCastMemberCommand, CreateCastMemberOutput>
        permits DefaultCreateCastMemberUseCase {
}

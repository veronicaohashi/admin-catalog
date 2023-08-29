package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.application.UseCase;

public sealed abstract class UpdateCastMemberUseCase
extends UseCase<UpdateCastMemberCommand, UpdateCastMemberOutput>
permits DefaultUpdateCastMemberUseCase {
}

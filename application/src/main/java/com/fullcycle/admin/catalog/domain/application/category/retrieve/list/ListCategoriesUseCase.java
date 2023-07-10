package com.fullcycle.admin.catalog.domain.application.category.retrieve.list;

import com.fullcycle.admin.catalog.domain.application.UseCase;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> {
}

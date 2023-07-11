package com.fullcycle.admin.catalog.infraestructure.category.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryJpaEntity, String>{

}

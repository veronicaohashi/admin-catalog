package com.fullcycle.admin.catalog.infraestructure.genre.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<GenreJpaEntity, String> {
}

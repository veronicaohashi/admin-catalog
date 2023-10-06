package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoJpaEntity, String> {
}

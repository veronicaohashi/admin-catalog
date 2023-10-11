package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import com.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.video.VideoPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface VideoRepository extends JpaRepository<VideoJpaEntity, String> {

    @Query("""
            select distinct new com.fullcycle.admin.catalog.domain.video.VideoPreview(
                v.id as id,
                v.title as title,
                v.description as description,
                v.createdAt as createdAt,
                v.updatedAt as updatedAt
            )
            from Video v
                left join v.categories categories
                left join v.genres genres
                left join v.castMembers castMembers
            where
                ( :terms is null or UPPER(v.title) like :terms )
            and
                ( :categories is null or categories.id.categoryId in :categories )
            and
                ( :genres is null or genres.id.genreId in :genres )
            and
                ( :castMembers is null or castMembers.id.castMemberId in :castMembers )
            """)
    Page<VideoPreview> findAll(
            @Param("terms") String terms,
            @Param("categories") Set<String> categories,
            @Param("genres") Set<String> genres,
            @Param("castMembers") Set<String> castMembers,
            Pageable page
    );
}

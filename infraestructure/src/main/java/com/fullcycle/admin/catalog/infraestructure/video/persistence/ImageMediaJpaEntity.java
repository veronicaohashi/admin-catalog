package com.fullcycle.admin.catalog.infraestructure.video.persistence;

import com.fullcycle.admin.catalog.domain.video.ImageMedia;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "videos_image_media")
@Entity(name = "ImageMedia")
public class ImageMediaJpaEntity {

    @Id
    private String id;

    @Column(name = "checksum", nullable = false)
    private String checksum;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    public ImageMediaJpaEntity() {}

    private ImageMediaJpaEntity(
            final String id,
            final String checksum,
            final String name,
            final String filePath
    ) {
        this.id = id;
        this.checksum = checksum;
        this.name = name;
        this.filePath = filePath;
    }

    public static ImageMediaJpaEntity from(final ImageMedia media) {
        return new ImageMediaJpaEntity(
                media.id(),
                media.checksum(),
                media.name(),
                media.location()
        );
    }

    public ImageMedia toDomain() {
        return ImageMedia.with(
                getId(),
                getChecksum(),
                getName(),
                getFilePath()
        );
    }

    public String getId() {
        return id;
    }

    public String getChecksum() { return checksum; }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }
}


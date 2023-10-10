package com.fullcycle.admin.catalog;

import com.fullcycle.admin.catalog.infraestructure.castmember.persistence.CastMemberRepository;
import com.fullcycle.admin.catalog.infraestructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalog.infraestructure.genre.persistence.GenreRepository;
import com.fullcycle.admin.catalog.infraestructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class MySQLCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        final var applicationContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                applicationContext.getBean(VideoRepository.class),
                applicationContext.getBean(CastMemberRepository.class),
                applicationContext.getBean(GenreRepository.class),
                applicationContext.getBean(CategoryRepository.class)
        ));
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}

package com.vpolosov.trainee.mergexml.repository;

import com.vpolosov.trainee.mergexml.model.ValidationProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Репозиторий для сущности {@link ValidationProcess}.
 * Наследуется от {@link JpaRepository}.
 *
 * @author Samat Hamzin
 */
public interface ValidationProcessRepository extends JpaRepository<ValidationProcess, UUID> {

    @Modifying
    @Query(value = """
        UPDATE ValidationProcess vp
        SET vp.isSuccess = true,
            vp.totalDocRef = :totalDocRef
        WHERE vp.id = :id
    """)
    int updateToSuccess(@Param("id") UUID id, String totalDocRef);
}
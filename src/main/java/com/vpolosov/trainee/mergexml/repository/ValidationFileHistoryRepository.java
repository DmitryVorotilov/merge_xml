package com.vpolosov.trainee.mergexml.repository;

import com.vpolosov.trainee.mergexml.model.ValidationFileHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Репозиторий для сущности {@link ValidationFileHistory}.
 * Наследуется от {@link JpaRepository}.
 *
 * @author Samat Hamzin
 */
public interface ValidationFileHistoryRepository extends JpaRepository<ValidationFileHistory, UUID>, JpaSpecificationExecutor<ValidationFileHistory> {
    /**
     * Метод для поиска всех провалидированных файлов, отсортированных по дате валидации по убыванию.
     * Использован EntityGraph для решения проблемы N+1.
     *
     * @return Список провалидированных файлов, отсортированных в порядке убывания по дате валидации.
     */
    @EntityGraph(value = "ValidationFileHistory.nodes", type = EntityGraph.EntityGraphType.LOAD)
    Page<ValidationFileHistory> findAllByOrderByValidationDateDesc(Pageable pageable);
}
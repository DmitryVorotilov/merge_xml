package com.vpolosov.trainee.mergexml.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


/**
 * Сущность для истории валидации файлов.
 *
 * @author Samat Hamzin
 */
@Getter
@Setter
@Entity
@Table(name = "validation_file_history")
@NamedEntityGraph(
        name = "ValidationFileHistory.nodes",
        attributeNodes = @NamedAttributeNode("validationProcess")
)
public class ValidationFileHistory {
    /**
     * Id валидации.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "validation_id", nullable = false)
    private UUID validationId;

    /**
     * Имя файла.
     */
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /**
     * Путь к документу.
     */
    @Column(name = "doc_ref", nullable = false)
    private String docRef;

    /**
     * Успех/провал валидации.
     */
    @Column(name = "is_success", nullable = false)
    private Boolean isSuccess;

    /**
     * Поля, непрошедшие проверку.
     */
    @Column(name = "fail_fields", columnDefinition = "TEXT")
    private String failFields;

    /**
     * Дата валидации.
     */
    @Column(name = "validation_date", nullable = false)
    private LocalDateTime validationDate;

    /**
     * Ссылка/внешний ключ к сущности процесса валидации.
     */
    @ManyToOne
    @JoinColumn(name = "validation_process_id")
    private ValidationProcess validationProcess;

    /**
     * Переопределение метода equals для сравнения сущностей.
     *
     * @param o Объект, с которым ведется сравнение.
     * @return boolean равны объекты или не равны.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        ValidationFileHistory that = (ValidationFileHistory) o;
        return getValidationId() != null && Objects.equals(getValidationId(), that.getValidationId());
    }

    /**
     * Переопределение метода hashCode для сущности.
     *
     * @return int Хэш-код.
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this)
                        .getHibernateLazyInitializer()
                        .getPersistentClass()
                        .hashCode() : getClass().hashCode();
    }

    /**
     * Переопределение метода toString.
     *
     * @return String строка представление сущности.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "("
                + "validationId = "
                + validationId
                + ", "
                + "fileName = "
                + fileName
                + ", "
                + "docRef = "
                + docRef
                + ", "
                + "isSuccess = "
                + isSuccess
                + ", "
                + "failFields = "
                + failFields
                + ", "
                + "validationDate = "
                + validationDate
                + ")";
    }
}
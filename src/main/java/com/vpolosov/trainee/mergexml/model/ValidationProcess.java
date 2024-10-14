package com.vpolosov.trainee.mergexml.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность для процесса валидации.
 *
 * @author Samat Hamzin
 */
@Getter
@Setter
@Entity
@Table(name = "validation_process")
public class ValidationProcess {
    /**
     * Id процесса валидации.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Путь к каталогу с файлами.
     */
    @Column(name="dir_ref", nullable = false)
    private String dirRef;

    /**
     * Успех/провал валидации.
     */
    @Column(name="is_succsess", nullable = false)
    private Boolean isSuccess;

    /**
     * Путь к итоговому документу.
     */
    @Column(name="total_doc_ref", nullable = false)
    private String totalDocRef;

    /**
     * Дата валидации.
     */
    @Column(name = "validation_process_date", nullable = false)
    private LocalDateTime validationProcessDate;

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
        ValidationProcess that = (ValidationProcess) o;
        return getId() != null && Objects.equals(getId(), that.getId());
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
                + "id = "
                + id
                + ", "
                + "dirRef = "
                + dirRef
                + ", "
                + "isSuccess = "
                + isSuccess
                + ", "
                + "totalDocRef = "
                + totalDocRef
                + ", "
                + "validationProcessDate = "
                + validationProcessDate
                + ")";
    }
}
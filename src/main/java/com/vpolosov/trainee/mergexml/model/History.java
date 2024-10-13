package com.vpolosov.trainee.mergexml.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * История платежа.
 *
 * @author Ali Takushinov
 * @author Maksim Litvinenko
 */
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "history")
public class History {

    /**
     * Ref Документа.
     */
    @Id
    @Column(name = "doc_ref")
    private String docRef;

    /**
     * Дата совершения платежа.
     */
    @Column(name = "document_date")
    private LocalDate documentDate;

    /**
     * Дата и время загрузки.
     */
    @Column(name = "date_time_upload")
    private LocalDateTime dateTimeUpload;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy h
            ? h.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy h
            ? h.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        History history = (History) o;
        return getDocRef() != null && Objects.equals(getDocRef(), history.getDocRef());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy h
            ? h.getHibernateLazyInitializer().getPersistentClass().hashCode()
            : getClass().hashCode();
    }
}

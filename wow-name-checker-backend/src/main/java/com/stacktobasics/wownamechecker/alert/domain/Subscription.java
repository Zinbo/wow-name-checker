package com.stacktobasics.wownamechecker.alert.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Subscription {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private String email;
    private String name;
    private String region;
    private String realm;

    public Subscription(@NonNull String email, @NonNull String name, @NonNull String realm, @NonNull String region) {
        this.id = UUID.randomUUID();
        this.email = email.toLowerCase();
        this.name = name;
        this.region = region;
        this.realm = realm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Subscription that = (Subscription) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

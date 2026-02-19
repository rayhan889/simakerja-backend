package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(
        name = "staffs",
        indexes = {
                @Index(name = "idx_staffs_nip", columnList = "nip")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_staffs_user", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_staffs_nip", columnNames = "nip")
        }
)
public class StaffModel {
    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_staffs_user")
    )
    private UserModel user;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true, length = 20)
    private String nip;

    public StaffModel() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }
}

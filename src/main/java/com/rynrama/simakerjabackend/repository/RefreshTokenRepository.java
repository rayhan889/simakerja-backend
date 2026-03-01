package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.RefreshTokenModel;
import com.rynrama.simakerjabackend.model.UserModel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, UUID> {
    @Query("""
        SELECT rt FROM RefreshTokenModel rt
            JOIN FETCH rt.user
                WHERE rt.token = :token
    """)
    Optional<RefreshTokenModel> findByToken(@Param("token") String token);

    @Modifying
    @Query("DELETE FROM RefreshTokenModel rt WHERE rt.token = :token")
    void deleteByToken(@Param("token") String token);

    @Modifying
    @Query("DELETE FROM RefreshTokenModel rt WHERE rt.user = :user")
    void deleteAllByUser(@Param("user") UserModel user);
}

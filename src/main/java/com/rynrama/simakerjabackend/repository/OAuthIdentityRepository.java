package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.OauthIdentityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OAuthIdentityRepository extends JpaRepository<OauthIdentityModel, UUID> {
    List<OauthIdentityModel> findByProvider(String provider);

    @Query(
            """
    select oi from OauthIdentityModel oi
        join fetch oi.user 
            where oi.provider = :provider
                and oi.providerUserId = :providerUserId
    """
    )
    Optional<OauthIdentityModel> findByProviderAndProviderUserId(
            @Param("provider") String provider,
            @Param("providerUserId") String providerUserId
    );
}

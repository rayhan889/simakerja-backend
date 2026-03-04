package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {


    Optional<UserModel> findByEmail(String email);

    @Override
    Optional<UserModel> findById(UUID id);

    @Query("""
    select u.email, u.fullName, u.phoneNumber, u.status, u.role from UserModel u
            where 
                u.role <> 'student'
                    and (
                            :search is null or :search = ''
                            or lower(u.fullName) like lower(concat('%', CAST(:search AS string), '%'))
                            or lower(u.email) like lower(concat('%', CAST(:search AS string), '%'))
                        )
    """)
    Page<UserService.CreatedUser> findAllUsers(Pageable pageable, @Param("search") String search);
}

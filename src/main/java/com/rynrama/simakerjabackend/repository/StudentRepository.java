package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.dto.StudentInfo;
import com.rynrama.simakerjabackend.model.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentModel, UUID> {

    @Query("""
    select s from StudentModel s
        join fetch UserModel u on s.user.id = u.id
            where s.user.id = :id
""")
    Optional<StudentModel> findByUserId(UUID id);

    @Query("""
        select
            new com.rynrama.simakerjabackend.dto.StudentInfo(
                u.fullName,
                    u.email,
                        s.nim
             ) from StudentModel s
            join UserModel u on s.user.id = u.id
                where u.status = 'active'
                    and s.nim is not null
                        and s.studyProgram is not null
                            and (
                                    :excludeNim is null or :excludeNim = ''
                                        or s.nim <> :excludeNim
                                )
    """)
    List<StudentInfo> findAllRegisteredStudents(String excludeNim);

    @Query("""
        select (
            u.status = 'active'
                and s.studyProgram is not null
                    and s.nim is not null
            )
                from StudentModel s
                    join UserModel u on s.user.id = u.id
                        where u.id = :id
    """)
    Boolean isStudentValid(UUID userId);
}

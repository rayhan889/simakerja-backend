package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.CreateNewUserRequest;
import com.rynrama.simakerjabackend.model.LecturerModel;
import com.rynrama.simakerjabackend.model.StaffModel;
import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.model.UserRole;
import com.rynrama.simakerjabackend.repository.LecturerRepository;
import com.rynrama.simakerjabackend.repository.StaffRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepo;
    private final StaffRepository  staffRepo;
    private final LecturerRepository lecturerRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepo,
            StaffRepository staffRepo,
            LecturerRepository lecturerRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.staffRepo = staffRepo;
        this.lecturerRepo = lecturerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<CreatedUser> findAllUsers(
            Pageable pageable,
            String search
    ) {
        Pageable sort = findAllUsersSort(pageable);
        return userRepo.findAllUsers(
                sort,
                search
        );
    }

    private Pageable findAllUsersSort(Pageable pageable) {
        Sort requested = pageable.getSort();

        if (requested.isUnsorted()) {
            Sort defaultSort = Sort.by(Sort.Order.desc("u.fullName"));
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        Sort mapped = Sort.unsorted();

        for (Sort.Order order : requested) {
            String key = order.getProperty();

            String normalized = key.trim();

            if (normalized.equals("email")) {
                mapped = mapped.and(JpaSort.unsafe(order.getDirection(), "u.email"));
                continue;
            }

        }

        if (mapped.isUnsorted()) {
            mapped = Sort.by(Sort.Order.desc("u.fullName"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), mapped);
    }

    @Transactional
    public CreatedUser createNewUser(
            CreateNewUserRequest request
    ) {
        log.info("Creating a new user...");

        var user = new UserModel();

        if (request.getRole().equals(UserRole.student)) {
            log.warn("User with role={} cannot be manually created", UserRole.student);
            throw new BadRequestException("Cannot create user with role=student");
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setCreatedAt(Instant.now());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);

        switch (request.getRole()) {
            case staff:
                try {
                    saveStaff(
                            request.getNip(),
                            user
                    );
                } catch (Exception e) {
                    log.error("Error creating user with role=staff. Error: {}", e.getMessage());
                    throw new IllegalArgumentException("Cannot create user with role=staff");
                }
                break;
            case lecturer:
                try {
                    saveLecturer(
                            request.getNidn(),
                            request.getStudyProgram(),
                            user
                    );
                } catch (Exception e) {
                    log.error("Error creating user with role=lecturer. Error: {}", e.getMessage());
                    throw new IllegalArgumentException("Cannot create user with role=lecturer");
                }
        }

        log.info("User created successfully");
        return new CreatedUser(
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.getRole()
        );
    }

    private void saveStaff(String nip, UserModel user) {
        log.info("Saving staff...");

        var staff = new StaffModel();

        staff.setUser(user);
        staff.setNip(nip);

        log.info("Staff saved successfully");
        staffRepo.save(staff);
    }

    private void saveLecturer(String nidn, String studyProgram, UserModel user) {
        log.info("Saving lecturer...");

        var lecturer = new LecturerModel();

        lecturer.setUser(user);
        lecturer.setNidn(nidn);
        lecturer.setStudyProgram(studyProgram);

        log.info("Lecturer saved successfully");
        lecturerRepo.save(lecturer);
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    public record CreatedUser(
      String email,
      String fullName,
      String phoneNumber,
      String status,
      UserRole role
    ){}
}

package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.dto.CreateNewUserRequest;
import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.service.UserService;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins")
@PreAuthorize("hasRole('SUPERADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/new-user")
    public ResponseEntity<GlobalAPIResponse<UserService.CreatedUser>> createUser(
            @Valid @RequestBody CreateNewUserRequest request
    ) {
        var data = userService.createNewUser(request);

        return ResponseEntity.ok(GlobalAPIResponse.success(data));
    }

    @GetMapping("/list-user")
    public ResponseEntity<GlobalAPIResponse<Page<UserService.CreatedUser>>> getAllUsers(
            Pageable pageable,
            @RequestParam(value = "search", required = false)  String search
    ) {
        Page<UserService.CreatedUser> users = userService.findAllUsers(pageable, search);

        return ResponseEntity.ok(GlobalAPIResponse.success(users));
    }
}

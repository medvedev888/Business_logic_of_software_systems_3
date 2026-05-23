package me.ifmo.backend.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.enrollment.AdminCreateEnrollmentRequest;
import me.ifmo.backend.DTO.enrollment.CreateEnrollmentRequest;
import me.ifmo.backend.DTO.enrollment.EnrollmentDTO;
import me.ifmo.backend.mappers.EnrollmentMapper;
import me.ifmo.backend.services.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ENROLLMENT_CREATE')")
    public EnrollmentDTO createEnrollment(@RequestBody @Valid CreateEnrollmentRequest request, Authentication authentication) {
        return enrollmentMapper.toEnrollmentDTO(
                enrollmentService.createEnrollment(authentication.getName(), request.getCourseId())
        );
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ENROLLMENT_CREATE_ALL')")
    public EnrollmentDTO createEnrollmentForUser(@RequestBody @Valid AdminCreateEnrollmentRequest request) {
        return enrollmentMapper.toEnrollmentDTO(
                enrollmentService.createEnrollmentForUser(request.getUserId(), request.getCourseId())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ENROLLMENT_READ_ALL') or @accessService.canAccessEnrollment(#id, authentication)")
    public EnrollmentDTO getEnrollmentById(@PathVariable @Min(1) Long id) {
        return enrollmentMapper.toEnrollmentDTO(enrollmentService.getEnrollmentById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ENROLLMENT_READ_ALL') or @accessService.canAccessUserEnrollments(#userId, authentication)")
    public Page<EnrollmentDTO> getEnrollmentsByUserId(@PathVariable @Min(1) Long userId, Pageable pageable) {
        return enrollmentService.getEnrollmentsByUserId(userId, pageable)
                .map(enrollmentMapper::toEnrollmentDTO);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('ENROLLMENT_READ_ALL')")
    public Page<EnrollmentDTO> getEnrollmentsByCourseId(@PathVariable @Min(1) Long courseId, Pageable pageable) {
        return enrollmentService.getEnrollmentsByCourseId(courseId, pageable)
                .map(enrollmentMapper::toEnrollmentDTO);
    }
}
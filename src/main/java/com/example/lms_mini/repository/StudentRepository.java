package com.example.lms_mini.repository;

import com.example.lms_mini.dto.response.StudentSearchResDTO;
import com.example.lms_mini.entity.Student;
import com.example.lms_mini.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByIdAndStatus(Long id, Status status);

    boolean existsByEmailAndStatus(String email, Status status);

    boolean existsByPhoneNumberAndStatus(String phoneNumber, Status status);

    boolean existsByIdentityNumberAndStatus(String identityNumber, Status status);

    Optional<Student> findByEmailAndStatusAndIdNot(String email, Status status, Long id);

    Optional<Student> findByPhoneNumberAndStatusAndIdNot(String phoneNumber, Status status, Long id);

    Optional<Student> findByIdentityNumberAndStatusAndIdNot(String identityNumber, Status status, Long id);


    @Query("SELECT new com.example.lms_mini.dto.response.StudentSearchResDTO(" +
            "s.id, s.fullName, s.email, s.phoneNumber, s.birthDate, s.gender, s.address, s.status" +
            ") " +
            "FROM Student s " +
            "WHERE 1=1 " +
            "AND (:fullName IS NULL OR s.fullName LIKE %:fullName% ESCAPE '\\') " +
            "AND (:email IS NULL OR s.email LIKE %:email% ESCAPE '\\') " +
            "AND (:phoneNumber IS NULL OR s.phoneNumber LIKE %:phoneNumber% ESCAPE '\\') " +
            "AND (:status IS NULL OR s.status = :status)")
    Page<StudentSearchResDTO> searchStudents(
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("status") Status status,
            Pageable pageable
    );
}

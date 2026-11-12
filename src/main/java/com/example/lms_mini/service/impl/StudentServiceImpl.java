package com.example.lms_mini.service.impl;

import com.example.lms_mini.Utils.EscapeHelper;
import com.example.lms_mini.dto.request.StudentRequestDTO;
import com.example.lms_mini.dto.request.StudentSearchReqDTO;
import com.example.lms_mini.dto.request.StudentUpdateDTO;
import com.example.lms_mini.dto.response.PageResponse;
import com.example.lms_mini.dto.response.StudentBasicResponseDTO;
import com.example.lms_mini.dto.response.StudentDetailsDTO;
import com.example.lms_mini.dto.response.StudentSearchResDTO;
import com.example.lms_mini.entity.Resource;
import com.example.lms_mini.entity.Student;
import com.example.lms_mini.enums.ObjectType;
import com.example.lms_mini.enums.ResourceType;
import com.example.lms_mini.enums.Status;
import com.example.lms_mini.exception.ResourceAlreadyExistsException;
import com.example.lms_mini.exception.ResourceNotFoundException;
import com.example.lms_mini.mapper.StudentMapper;
import com.example.lms_mini.repository.ResourceRepository;
import com.example.lms_mini.repository.StudentRepository;
import com.example.lms_mini.service.FileStorageService;
import com.example.lms_mini.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final FileStorageService fileStorageService;
    private final StudentMapper studentMapper;
    private final ResourceRepository resourceRepository;

    public StudentServiceImpl(StudentRepository studentRepository, FileStorageService fileStorageService, StudentMapper studentMapper, ResourceRepository resourceRepository) {
        this.studentRepository = studentRepository;
        this.fileStorageService = fileStorageService;
        this.studentMapper = studentMapper;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public StudentDetailsDTO getStudentDetails(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("student.notfound"));

        StudentDetailsDTO response = studentMapper.toDetailsDTO(student, null);

        Resource avatarResource = resourceRepository
                .findByObjectIdAndObjectTypeAndIsPrimaryTrue(student.getId(), ObjectType.STUDENT)
                .orElse(null);

        if (avatarResource != null) {
            String fullUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/")
                    .path(avatarResource.getUrl())
                    .toUriString();

            response.setAvatarUrl(fullUrl);
        }

        return response;
    }

    @Override
    public PageResponse<?> searchStudents(StudentSearchReqDTO dto, Pageable pageable) {

        String escapedName = EscapeHelper.escapeLike(dto.getFullName());
        String escapedEmail = EscapeHelper.escapeLike(dto.getEmail());
        String escapedPhone = EscapeHelper.escapeLike(dto.getPhoneNumber());

        Page<?> studentPage = studentRepository.searchStudents(
                escapedName,
                escapedEmail,
                escapedPhone,
                dto.getStatus(),
                pageable
        );
        return PageResponse.builder()
                .data(studentPage.getContent())
                .currentPage(studentPage.getNumber())
                .pageSize(studentPage.getSize())
                .totalElements(studentPage.getTotalElements())
                .totalPages(studentPage.getTotalPages())
                .hasNext(studentPage.hasNext())
                .hasPrevious(studentPage.hasPrevious())
                .build();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public StudentBasicResponseDTO createStudent(StudentRequestDTO studentRequestDTO, MultipartFile avatarImage) {

        // Valid unique fields
        if(studentRepository.existsByEmailAndStatus(studentRequestDTO.getEmail(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("student.email.exists");
        }
        if(studentRepository.existsByPhoneNumberAndStatus(studentRequestDTO.getPhoneNumber(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("student.phone_number.exists");
        }
        if(studentRepository.existsByIdentityNumberAndStatus(studentRequestDTO.getIdentityNumber(), Status.ACTIVE)) {
            throw new ResourceAlreadyExistsException("student.identity_number.exists");
        }

        // Save student
        Student student =  studentMapper.toEntity(studentRequestDTO);
        student = studentRepository.save(student);

        String savedAvatarUrl = null;

        // Store avatar image
        if(avatarImage != null && !avatarImage.isEmpty()) {
            savedAvatarUrl = saveStudentAvatar(avatarImage, student.getId());
        }

        return studentMapper.toBasicResponseDTO(student, savedAvatarUrl);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public StudentBasicResponseDTO updateStudent(Long id, StudentUpdateDTO studentRequestDTO, MultipartFile avatarImage) {

        // Check if student is active
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("student.notfound"));

        // Valid unique fields
        if(!existingStudent.getEmail().equals(studentRequestDTO.getEmail())) {
            studentRepository.findByEmailAndStatusAndIdNot(studentRequestDTO.getEmail(), Status.ACTIVE, id)
                    .ifPresent(s -> { throw new ResourceAlreadyExistsException("student.email.exists"); });
        }

        if(!existingStudent.getPhoneNumber().equals(studentRequestDTO.getPhoneNumber())) {
            studentRepository.findByPhoneNumberAndStatusAndIdNot(studentRequestDTO.getPhoneNumber(), Status.ACTIVE, id)
                    .ifPresent(s -> { throw new ResourceAlreadyExistsException("student.phone_number.exists"); });
        }

        if(!existingStudent.getIdentityNumber().equals(studentRequestDTO.getIdentityNumber())) {
            studentRepository.findByIdentityNumberAndStatusAndIdNot(studentRequestDTO.getIdentityNumber(), Status.ACTIVE, id)
                    .ifPresent(s -> { throw new ResourceAlreadyExistsException("student.identity_number.exists");});
        }

        // Update student fields
        existingStudent = studentMapper.updateEntityFromDto(studentRequestDTO, existingStudent);
        existingStudent = studentRepository.save(existingStudent);

        String updatedAvatarUrl = null;

        // Handle old avatar
        Resource existingAvatar = resourceRepository
                .findByObjectIdAndObjectTypeAndIsPrimaryTrue(existingStudent.getId(), ObjectType.STUDENT)
                .orElse(null);

        // Handle avatar image update
        if(avatarImage != null && !avatarImage.isEmpty()) {

            if(existingAvatar != null) {
                existingAvatar.setIsPrimary(false);
                existingAvatar.setStatus(Status.DELETE);
                resourceRepository.save(existingAvatar);
            }

            // Store new avatar
            updatedAvatarUrl = saveStudentAvatar(avatarImage, existingStudent.getId());

        } else {
            if(existingAvatar != null) {
                updatedAvatarUrl = existingAvatar.getUrl();
            }
        }
        return studentMapper.toBasicResponseDTO(existingStudent, updatedAvatarUrl);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void softDelete(Long studentId) {
        Student student = studentRepository.findByIdAndStatus(studentId, Status.ACTIVE)
                        .orElseThrow(() -> new ResourceNotFoundException("student.notfound"));

        student.setStatus(Status.DELETE);
        studentRepository.save(student);
    }

    private String saveStudentAvatar(MultipartFile avatarImage, Long studentId) {
        String avatarUrl = fileStorageService.storeFile(avatarImage);
        Resource newAvatar = new Resource();
        newAvatar.setObjectId(studentId);
        newAvatar.setUrl(avatarUrl);
        newAvatar.setObjectType(ObjectType.STUDENT);
        newAvatar.setResourceType(ResourceType.AVATAR);
        newAvatar.setIsPrimary(true);

        resourceRepository.save(newAvatar);
        return avatarUrl;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public StreamingResponseBody exportStudents(StudentSearchReqDTO dto) {

        String escapedName = EscapeHelper.escapeLike(dto.getFullName());
        String escapedEmail = EscapeHelper.escapeLike(dto.getEmail());
        String escapedPhone = EscapeHelper.escapeLike(dto.getPhoneNumber());

        return outputStream -> {
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
                Sheet sheet = workbook.createSheet("Danh sách học viên");

                String[] HEADERS = { "ID", "Họ và Tên", "Email", "SĐT", "Ngày sinh", "Giới tính", "Địa chỉ", "Trạng thái" };
                Row headerRow = sheet.createRow(0);
                CellStyle headerStyle = createHeaderStyle(workbook);

                for (int col = 0; col < HEADERS.length; col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(HEADERS[col]);
                    cell.setCellStyle(headerStyle);
                }

                CellStyle dateCellStyle = workbook.createCellStyle();
                dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));

                int page = 0;
                final int BATCH_SIZE = 1000;
                int currentRowIndex = 1;

                while (true) {
                    Pageable pageable = PageRequest.of(page, BATCH_SIZE);

                    Page<StudentSearchResDTO> studentPage = studentRepository.searchStudents(
                            escapedName,
                            escapedEmail,
                            escapedPhone,
                            dto.getStatus(),
                            pageable
                    );

                    List<StudentSearchResDTO> students = studentPage.getContent();

                    if (students.isEmpty()) {
                        break;
                    }

                    for (StudentSearchResDTO student : students) {
                        Row row = sheet.createRow(currentRowIndex++);

                        row.createCell(0).setCellValue(student.getId());
                        row.createCell(1).setCellValue(student.getFullName());
                        row.createCell(2).setCellValue(student.getEmail());
                        row.createCell(3).setCellValue(student.getPhoneNumber());

                        Cell dobCell = row.createCell(4);
                        if (student.getBirthDate() != null) {
                            dobCell.setCellValue(java.sql.Date.valueOf(student.getBirthDate()));
                            dobCell.setCellStyle(dateCellStyle);
                        } else {
                            dobCell.setCellValue("");
                        }

                        String genderStr = (student.getGender() != null) ? student.getGender().name() : "";
                        row.createCell(5).setCellValue(genderStr);

                        row.createCell(6).setCellValue(student.getAddress());

                        String statusStr = (student.getStatus() != null) ? student.getStatus().name() : "";
                        row.createCell(7).setCellValue(statusStr);
                    }

                    page++;
                }

                workbook.write(outputStream);

                workbook.dispose();

            } catch (IOException e) {
                throw new RuntimeException("student.export.failure");
            }
        };
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}

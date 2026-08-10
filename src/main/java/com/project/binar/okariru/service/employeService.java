package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.employeRequest;
import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.entity.employeEntity;
import com.project.binar.okariru.repository.employeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface employeService {

    Page<employeResponse.employeGetResponse> findAll(String keyword, int page, int size);

    List<employeResponse.employeGetResponse> getAllEmployeeService();

    employeResponse.employeGetResponse getEmployeeServiceUserName(String name);

    employeResponse.employeAddResponse addEmploye(employeRequest.employeAddRequest addRequest);

    void updateEmployeEmailPass(Integer id, String email, String password);

    String deleteEmployee(Integer id);

    //Pagination

}

package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.EmployeRequest;
import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.repository.EmployeRepository;
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


public interface EmployeService {

    Page<EmployeResponse.employeGetResponse> findAll(String keyword, int page, int size);

    List<EmployeResponse.employeGetResponse> getAllEmployeeService();

    EmployeResponse.employeGetResponse getEmployeeServiceUserName(String name);

    EmployeResponse.employeAddResponse addEmploye(EmployeRequest.employeAddRequest addRequest);

    void updateEmployeEmailPass(Integer id, String email, String password);

    String deleteEmployee(Integer id);

    //Pagination

}

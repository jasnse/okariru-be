package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.entity.employeEntity;
import com.project.binar.okariru.repository.employeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class employeService {

    private final employeRepository employeRepository;

    //getAll Employee
    public List<employeResponse.employeGetResponse> getAllEmployeeService() {
        return employeRepository.findAll()
                .stream()
                .map(employe -> new employeResponse.employeGetResponse(
                        employe.getEmployeeId(),
                        employe.getUserName(),
                        employe.getNip(),
                        employe.getJoinedDate()
                ))
                .toList();
    }

    //get employee by username
    public employeResponse.employeGetResponse getEmployeeServiceUserName(String name) {
        employeEntity employe = employeRepository.findByUserName(name)
                .orElseThrow(() -> new EntityNotFoundException("Employee dengan username " + name + " tidak ditemukan"));
    return new employeResponse.employeGetResponse (
            employe.getEmployeeId(),
            employe.getUserName(),
            employe.getNip(),
            employe.getJoinedDate()
    );

    }
}

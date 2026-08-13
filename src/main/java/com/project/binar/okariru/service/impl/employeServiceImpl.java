package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.EmployeRequest;
import com.project.binar.okariru.dto.EmployeResponse;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.service.EmployeService;
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

@Service
@RequiredArgsConstructor
public class EmployeServiceImpl implements EmployeService {

    private final EmployeRepository employeRepository;

    @Override
    public Page<EmployeResponse.employeGetResponse> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<EmployeEntity> employeePage = employeRepository.searchEmployees(keyword, pageable);

        return employeePage.map(employee -> new EmployeResponse.employeGetResponse(
                employee.getEmployeeId(),
                employee.getUserName(),
                employee.getNip(),
                employee.getJoinedDate(),
                employee.getUpdatedAt()
        ));
    }

    @Override
    //getAll Employee
    public List<EmployeResponse.employeGetResponse> getAllEmployeeService() {
        return employeRepository.findAll()
                .stream()
                .map(employe -> new EmployeResponse.employeGetResponse(
                        employe.getEmployeeId(),
                        employe.getUserName(),
                        employe.getNip(),
                        employe.getJoinedDate(),
                        employe.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    //get employee by username
    public EmployeResponse.employeGetResponse getEmployeeServiceUserName(String name) {
        EmployeEntity employe = employeRepository.findByUserName(name)
                .orElseThrow(() -> new EntityNotFoundException("Employee dengan username " + name + " tidak ditemukan"));
        return new EmployeResponse.employeGetResponse (
                employe.getEmployeeId(),
                employe.getUserName(),
                employe.getNip(),
                employe.getJoinedDate(),
                employe.getUpdatedAt()
        );

    }
    @Override
    //add Employe
    public EmployeResponse.employeAddResponse addEmploye(EmployeRequest.employeAddRequest addRequest){
        EmployeEntity employe = new EmployeEntity();
        employe.setUserName(addRequest.username);
        employe.setEmail(addRequest.email);
        employe.setPassword(addRequest.password);
        employe.setNip(addRequest.nip);
        employe.setJoinedDate(LocalDate.now());

        EmployeEntity saved = employeRepository.save(employe);
        return new EmployeResponse.employeAddResponse(
                saved.getEmployeeId(),
                saved.getUserName(),
                saved.getNip(),
                saved.getJoinedDate());
    }

    //update employee by id
    @Override
    @Transactional
    public void updateEmployeEmailPass(Integer id, String email, String password){
        Optional<EmployeEntity> employeeOpt = employeRepository.findById(id);

        if (employeeOpt.isEmpty()) {
            throw new EntityNotFoundException("Employee id tidak ditemukan");
        }

        EmployeEntity employeeUpdate = employeeOpt.get();
        employeeUpdate.setEmail(email);
        employeeUpdate.setPassword(password);
        employeeUpdate.setUpdatedAt(LocalDate.now());
        employeRepository.save(employeeUpdate);
    }

    //delete Employe by ID
    @Override
    public String deleteEmployee(Integer id) {
        EmployeEntity employe = employeRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Employee id: " + id + " " + "tidak ditemukan" ));
        employeRepository.delete(employe);

        return "Employee dengan ID: " + id + " " + "Telah di hapus";
    }
}

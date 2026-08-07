package com.project.binar.okariru.service;

import com.project.binar.okariru.dto.employeRequest;
import com.project.binar.okariru.dto.employeResponse;
import com.project.binar.okariru.entity.employeEntity;
import com.project.binar.okariru.repository.employeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    //add Employe
    public employeResponse.employeGetResponse addEmploye(employeRequest.employeAddRequest addRequest){
        employeEntity employe = new employeEntity();
        employe.setUserName(addRequest.username);
        employe.setEmail(addRequest.email);
        employe.setPassword(addRequest.password);
        employe.setNip(addRequest.nip);
        employe.setJoinedDate(addRequest.joinedDate);

        employeEntity saved = employeRepository.save(employe);
        return new employeResponse.employeGetResponse(
                saved.getEmployeeId(),
                saved.getUserName(),
                saved.getNip(),
                saved.getJoinedDate() );
    }

    //update employee by id
    @Transactional
    public void updateEmployeEmailPass(Integer id, String email, String password){
        Optional<employeEntity> employeeOpt = employeRepository.findById(id);

        if (employeeOpt.isEmpty()) {
            throw new EntityNotFoundException("Employee id tidak ditemukan");
        }

        employeEntity employeeUpdate = employeeOpt.get();
        employeeUpdate.setEmail(email);
        employeeUpdate.setPassword(password);
        employeRepository.save(employeeUpdate);
    }

    //delete Employe by ID
    public String deleteEmployee(Integer id) {
         employeEntity employe = employeRepository.findById(id)
                 .orElseThrow(()-> new EntityNotFoundException("Employee id: " + id + " " + "tidak ditemukan" ));
         employeRepository.delete(employe);

         return "Employee dengan ID: " + id + " " + "Telah di hapus";
    }
}

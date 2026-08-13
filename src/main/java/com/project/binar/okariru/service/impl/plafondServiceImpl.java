package com.project.binar.okariru.service.impl;

import com.project.binar.okariru.dto.PlafondRequest;
import com.project.binar.okariru.dto.PlafondResponse;
import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.EmployeEntity;
import com.project.binar.okariru.entity.PlafondEntity;
import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.repository.EmployeRepository;
import com.project.binar.okariru.repository.PlafondRepository;
import com.project.binar.okariru.service.PlafondService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlafondServiceImpl implements PlafondService {

    private final PlafondRepository plafondRepository;
    private final CustomerRepository customerRepository;
    private final EmployeRepository employeRepository;

    @Override
    public List<PlafondResponse.getPlafondResponse> getAllPlafond() {
        return plafondRepository.findAll()
                .stream()
                .map(plafond -> new PlafondResponse.getPlafondResponse(
                        plafond.getPlafondId(),
                        plafond.getUser().getCustomerId(),
                        plafond.getTotalPlafond(),
                        plafond.getDeskripsiPlafond(),
                        plafond.getCreatedBy() != null ? plafond.getCreatedBy().getEmployeeId() : null,
                        plafond.getCreatedAt(),
                        plafond.getUpdatedBy() != null ? plafond.getUpdatedBy().getEmployeeId() : null,
                        plafond.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public PlafondResponse.getPlafondResponse getPlafondById(Integer id) {
        PlafondEntity plafond = plafondRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("plafond dengan Id " + id + " tidak ditemukan"));
        return new PlafondResponse.getPlafondResponse(
                plafond.getPlafondId(),
                plafond.getUser().getCustomerId(),
                plafond.getTotalPlafond(),
                plafond.getDeskripsiPlafond(),
                plafond.getCreatedBy() != null ? plafond.getCreatedBy().getEmployeeId() : null,
                plafond.getCreatedAt(),
                plafond.getUpdatedBy() != null ? plafond.getUpdatedBy().getEmployeeId() : null,
                plafond.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public PlafondResponse.getPlafondResponse addPlafond(PlafondRequest.plafondAddRequest addRequest) {
        CustomerEntity user = customerRepository.findById(addRequest.userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + addRequest.userId + " tidak ditemukan"));

        PlafondEntity plafond = new PlafondEntity();
        plafond.setUser(user);
        plafond.setTotalPlafond(addRequest.totalPlafond);
        plafond.setDeskripsiPlafond(addRequest.deskripsiPlafond);
        plafond.setCreatedAt(LocalDate.now());

        if (addRequest.createdBy != null) {
            EmployeEntity createdBy = employeRepository.findById(addRequest.createdBy)
                    .orElseThrow(() -> new EntityNotFoundException("Employee dengan id " + addRequest.createdBy + " tidak ditemukan"));
            plafond.setCreatedBy(createdBy);
        }

        PlafondEntity saved = plafondRepository.save(plafond);
        return new PlafondResponse.getPlafondResponse(
                saved.getPlafondId(),
                saved.getUser().getCustomerId(),
                saved.getTotalPlafond(),
                saved.getDeskripsiPlafond(),
                saved.getCreatedBy() != null ? saved.getCreatedBy().getEmployeeId() : null,
                saved.getCreatedAt(),
                saved.getUpdatedBy() != null ? saved.getUpdatedBy().getEmployeeId() : null,
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updatePlafond(Integer id, Integer userId, Integer totalPlafond, String deskripsiPlafond, Integer updatedBy) {
        Optional<PlafondEntity> plafondOpt = plafondRepository.findById(id);

        if (plafondOpt.isEmpty()) {
            throw new EntityNotFoundException("plafond id tidak ditemukan");
        }

        CustomerEntity user = customerRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer dengan id " + userId + " tidak ditemukan"));

        PlafondEntity plafondUpdate = plafondOpt.get();
        plafondUpdate.setUser(user);
        plafondUpdate.setTotalPlafond(totalPlafond);
        plafondUpdate.setDeskripsiPlafond(deskripsiPlafond);
        plafondUpdate.setUpdatedAt(LocalDate.now());

        if (updatedBy != null) {
            EmployeEntity updatedByEntity = employeRepository.findById(updatedBy)
                    .orElseThrow(() -> new EntityNotFoundException("Employee dengan id " + updatedBy + " tidak ditemukan"));
            plafondUpdate.setUpdatedBy(updatedByEntity);
        }

        plafondRepository.save(plafondUpdate);
    }

    @Override
    public String deletePlafond(Integer id) {
        PlafondEntity plafondDelete = plafondRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("plafond id: " + id + " tidak ditemukan"));

        plafondRepository.delete(plafondDelete);

        return "plafond dengan ID: " + id + " Telah di hapus";
    }
}

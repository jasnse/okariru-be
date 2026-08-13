package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {
    List<NotificationEntity> findByCustomer_CustomerId(int customerCustomerId);
}

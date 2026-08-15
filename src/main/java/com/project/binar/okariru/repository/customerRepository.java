package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {

    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByNik(String nik);
    boolean existsByNoRekening(String noRekening);

    boolean existsByUserNameAndCustomerIdNot(String userName, Integer customerId);
    boolean existsByEmailAndCustomerIdNot(String email, Integer customerId);
    boolean existsByNikAndCustomerIdNot(String nik, Integer customerId);
    boolean existsByNoRekeningAndCustomerIdNot(String noRekening, Integer customerId);
}

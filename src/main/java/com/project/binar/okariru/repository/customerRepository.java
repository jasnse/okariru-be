package com.project.binar.okariru.repository;

import com.project.binar.okariru.entity.CustomerEntity;
import com.project.binar.okariru.entity.EmployeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {

    @Query("SELECT c FROM CustomerEntity c WHERE " +
            "(:keyword IS NULL OR LOWER(c.userName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CustomerEntity> searchCustomer(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByNik(String nik);
    boolean existsByNoRekening(String noRekening);

    boolean existsByUserNameAndCustomerIdNot(String userName, Integer customerId);
    boolean existsByEmailAndCustomerIdNot(String email, Integer customerId);
    boolean existsByNikAndCustomerIdNot(String nik, Integer customerId);
    boolean existsByNoRekeningAndCustomerIdNot(String noRekening, Integer customerId);


    Optional<CustomerEntity> findByUserName(String userName);

}

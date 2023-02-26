package com.example.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;


@RepositoryRestResource(collectionResourceRel = "payments",path = "payments") //data-rest eklentisini eklemiştik.Ve  RepositoryRestResource ile bu domain objesini dışarıya Rest olarak açabiliyoruz.
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    Optional<Payment> findByOrderId(int id);
}

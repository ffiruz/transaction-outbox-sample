package com.example.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(collectionResourceRel = "orders",path = "orders") //data-rest eklentisini eklemiştik.Ve  RepositoryRestResource ile bu domain objesini dışarıya Rest olarak açabiliyoruz.
public interface OrderRepository  extends JpaRepository<Order,Integer> {
}

/*
RepositoryRestResource:Elimizde olan bu Order Domain objesini dışarıya rest olarak açabiliyoruz.Rest kurallarına uyarak.
 */

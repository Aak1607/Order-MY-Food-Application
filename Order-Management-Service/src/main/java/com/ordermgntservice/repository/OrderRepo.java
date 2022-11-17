package com.ordermgntservice.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.ordermgntservice.model.Order;

@RepositoryRestResource(collectionResourceRel = "orders")
public interface OrderRepo extends PagingAndSortingRepository<Order, String> {
   

    @RestResource(rel = "create")
    public Order save(@Param("order") Order order);
}

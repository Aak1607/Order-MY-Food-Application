package com.ordermgntservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ordermgntservice.model.Order;
import com.ordermgntservice.repository.OrderRepo;

@Service
public class OrderServiceImpl implements OrderService{
	 private OrderRepo orderRepository;

	    @Autowired
	    public OrderServiceImpl(OrderRepo orderRepository) {
	        this.orderRepository = orderRepository;
	    }

	    @Override
	    public Order createOrder(Order order) {
	        order.setOrderTime(System.currentTimeMillis());
	        order.setTotalPrice(order.getItems().stream().mapToInt(e -> e.getPrice() * e.getQuantity()).sum());
	        return orderRepository.save(order);
	    }

}

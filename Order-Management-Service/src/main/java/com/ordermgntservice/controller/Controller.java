package com.ordermgntservice.controller;
import com.ordermgntservice.model.*;
import com.ordermgntservice.repository.*;
import com.ordermgntservice.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class Controller {
	 OrderService orderService;

	    @Autowired
	    public Controller(OrderService orderService) {
	        this.orderService = orderService;
	    }

	    @RequestMapping(value = "/restaurants/{rid}/orders", method = RequestMethod.POST)
	    @ResponseStatus(value = HttpStatus.CREATED)
	    public Order createOder(@RequestBody Order order) {
	        return orderService.createOrder(order);
	    }
}

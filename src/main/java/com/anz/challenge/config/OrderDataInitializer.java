package com.anz.challenge.config;

import com.anz.challenge.model.Order;
import com.anz.challenge.service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class OrderDataInitializer {

	@Autowired
	private OrderService service;

	@PostConstruct
	public void init() {

		List<Order> orderList = IntStream.rangeClosed(1, 10).mapToObj(i -> {
			Order order = new Order();
			if (i <= 3) {
				order.setDescription("PRODUCT - CREATED - " + i);
				order.setStatus(Order.Status.CREATED);
			} else if (i <= 6) {
				order.setDescription("PRODUCT - COMPLETED - " + i);
				order.setStatus(Order.Status.COMPLETED);
			} else {
				order.setDescription("PRODUCT - CANCELLED - " + i);
				order.setStatus(Order.Status.CANCELLED);
			}

			return order;
		}).toList();

		service.createBulkOrders(orderList);
	}

}

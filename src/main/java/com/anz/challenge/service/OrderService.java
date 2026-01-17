package com.anz.challenge.service;

import com.anz.challenge.model.Order;
import com.anz.challenge.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final NotificationService notificationService;

    @Transactional
    public Order createOrder(Order order) {
        Order saved = repository.save(order);
        notificationService.notifyStatusChange(saved.getId(), saved.getStatus().name());
        return saved;
    }

    public Optional<Order> getOrder(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Order updateStatus(Long id, Order.Status status) {
        Order order = repository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        Order updated = repository.save(order);
        notificationService.notifyStatusChange(updated.getId(), updated.getStatus().name());
        return updated;
    }

    public List<Order> searchOrders() {
        return repository.findAll();
    }
}

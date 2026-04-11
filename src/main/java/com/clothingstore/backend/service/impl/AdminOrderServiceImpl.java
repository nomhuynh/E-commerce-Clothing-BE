package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.admin.*;
import com.clothingstore.backend.dto.order.OrderResponse;
import com.clothingstore.backend.entity.Order;
import com.clothingstore.backend.entity.OrderItem;
import com.clothingstore.backend.entity.Payment;
import com.clothingstore.backend.entity.enums.OrderStatus;
import com.clothingstore.backend.repository.OrderItemRepository;
import com.clothingstore.backend.repository.OrderRepository;
import com.clothingstore.backend.repository.PaymentRepository;
import com.clothingstore.backend.service.AdminOrderService;
import com.clothingstore.backend.service.OrderService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public AdminOrderListPayload listOrders(int page, int limit, String status, String startDate, String endDate) {
        int p = Math.max(1, page);
        int l = Math.min(100, Math.max(1, limit));

        OrderStatus st = null;
        if (status != null && !status.isBlank()) {
            st = OrderStatus.valueOf(status.trim().toUpperCase());
        }
        LocalDateTime start = parseStart(startDate);
        LocalDateTime end = parseEndExclusive(endDate);

        Specification<Order> spec = buildSpecification(st, start, end);
        Page<Order> result = orderRepository.findAll(
                spec,
                PageRequest.of(p - 1, l, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<AdminOrderSummaryDto> rows = result.getContent().stream()
                .map(o -> AdminOrderSummaryDto.builder()
                        .orderId(o.getId())
                        .orderCode(o.getOrderCode())
                        .customerName(o.getCustomerName())
                        .createdAt(o.getCreatedAt())
                        .totalAmount(o.getTotalAmount())
                        .orderStatus(o.getOrderStatus())
                        .itemCount(orderItemRepository.countByOrder_Id(o.getId()))
                        .build())
                .toList();

        return AdminOrderListPayload.builder()
                .orders(rows)
                .page(p)
                .totalPages(Math.max(1, result.getTotalPages()))
                .totalItems(result.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOrderDetailPayload getOrderDetail(String orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return AdminOrderDetailPayload.builder()
                .order(toDetailDto(order))
                .build();
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(String orderId, String status) {
        return orderService.updateStatus(orderId, status);
    }

    private static Specification<Order> buildSpecification(OrderStatus status, LocalDateTime start, LocalDateTime endExclusive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("orderStatus"), status));
            }
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
            }
            if (endExclusive != null) {
                predicates.add(cb.lessThan(root.get("createdAt"), endExclusive));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static LocalDateTime parseStart(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return LocalDate.parse(raw.trim()).atStartOfDay();
    }

    /** Kết thúc ngày (độc quyền): createdAt < ngày sau 00:00 */
    private static LocalDateTime parseEndExclusive(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return LocalDate.parse(raw.trim()).plusDays(1).atStartOfDay();
    }

    private AdminOrderDetailDto toDetailDto(Order order) {
        List<AdminOrderItemRowDto> itemRows = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem oi : order.getItems()) {
                itemRows.add(AdminOrderItemRowDto.builder()
                        .orderItemId(oi.getId())
                        .productName(oi.getProductName())
                        .sku(oi.getSku())
                        .quantity(oi.getQuantity())
                        .unitPrice(oi.getUnitPrice())
                        .totalPrice(oi.getTotalPrice())
                        .build());
            }
        }

        List<AdminPaymentRowDto> payments = new ArrayList<>();
        paymentRepository.findByOrderId(order.getId()).ifPresent(p -> payments.add(toPaymentRow(p)));

        return AdminOrderDetailDto.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .orderStatus(order.getOrderStatus())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .subtotalAmount(order.getSubtotalAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(itemRows)
                .payments(payments)
                .build();
    }

    private static AdminPaymentRowDto toPaymentRow(Payment p) {
        return AdminPaymentRowDto.builder()
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .amount(p.getAmount())
                .transactionCode(p.getTransactionCode())
                .paymentTime(p.getPaymentTime())
                .build();
    }
}

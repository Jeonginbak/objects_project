package com.objects.marketbridge.mock;

import com.objects.marketbridge.order.domain.Order;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BaseFakeOrderRepository {

    @Getter
    private static final BaseFakeOrderRepository instance = new BaseFakeOrderRepository();

    private Long autoGeneratedId = 0L;
    private List<Order> data = new ArrayList<>();

    protected Long increaseId() {
        return ++autoGeneratedId;
    }

    public void clear() {
        data.clear();
    }
}

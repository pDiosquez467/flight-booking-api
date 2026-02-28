package com.pdiosquez.flight_booking_api.domain.util.impl;

import com.pdiosquez.flight_booking_api.domain.util.DomainValidator;

import java.util.Collection;

public class DefaultDomainValidator implements DomainValidator {

    private static final DefaultDomainValidator INSTANCE = new DefaultDomainValidator();

    private DefaultDomainValidator() {}

    public static DefaultDomainValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public void notNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void notBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void isPositive(Number value, String message) {
        if (value == null || value.doubleValue() <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void isGreaterThan(Number value, Number threshold, String message) {
        if (value == null || threshold == null || value.doubleValue() <= threshold.doubleValue()) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void isGreaterOrEqualThan(Number value, Number threshold, String message) {
        if (value == null || threshold == null || value.doubleValue() < threshold.doubleValue()) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}

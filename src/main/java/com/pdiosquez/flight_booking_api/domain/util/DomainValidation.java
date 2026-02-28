package com.pdiosquez.flight_booking_api.domain.util;

import com.pdiosquez.flight_booking_api.domain.util.impl.DefaultDomainValidator;

import java.util.Collection;

public class DomainValidation {
    private static final DomainValidator validator =
            DefaultDomainValidator.getInstance();

    public static void notNull(Object value, String message) {
        validator.notNull(value, message);
    }

    public static void notBlank(String value, String message) {
        validator.notBlank(value, message);
    }

    public static void isPositive(Number value, String message) {
        validator.isPositive(value, message);
    }

    public static void isGreaterThan(Number value, Number threshold, String message) {
        validator.isGreaterThan(value, threshold, message);
    }

    public static void isGreaterOrEqualThan(Number value, Number threshold, String message) {
        validator.isGreaterOrEqualThan(value, threshold, message);
    }

    public static void notEmpty(Collection<?> collection, String message) {
        validator.notEmpty(collection, message);
    }
}


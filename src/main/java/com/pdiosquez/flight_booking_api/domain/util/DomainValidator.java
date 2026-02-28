package com.pdiosquez.flight_booking_api.domain.util;

import java.util.Collection;

public interface DomainValidator {

    void notNull(Object value, String message);

    void notBlank(String value, String message);

    void isPositive(Number value, String message);

    void isGreaterThan(Number value, Number threshold, String message);

    void isGreaterOrEqualThan(Number value, Number threshold, String message);

    void notEmpty(Collection<?> collection, String message);
}

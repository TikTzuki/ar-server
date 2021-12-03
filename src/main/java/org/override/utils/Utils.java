package org.override.utils;

import java.util.Optional;

public class Utils {
    public static Optional<Double> parseDouble(String value) {
        try {
            return value == null | value.isEmpty() ? Optional.empty() : Optional.of(Double.valueOf(value));
        } catch (Exception e) {
            return Optional.of(0D);
        }
    }

    public static Optional<Integer> parseInteger(String value) {
        try {
            return value == null | value.isEmpty() ? Optional.empty() : Optional.of(Integer.valueOf(value));
        } catch (Exception e) {
            return Optional.of(0);
        }
    }
}

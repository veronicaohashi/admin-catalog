package com.fullcycle.admin.catalog.domain.utils;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {

    private CollectionUtils() {}

    public static <IN, OUT>Set<OUT> mapTo(final Set<IN> ids, Function<IN, OUT> mapper) {
        return ids.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }
}

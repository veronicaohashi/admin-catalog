package com.fullcycle.admin.catalog.infraestructure.utils;

public final class SqlUtils {

    private SqlUtils() {
    }

    public static String like(final String term) {
        if(term == null) return null;
        return upper("%" + term + "%");
    }

    private static String upper(final String term) {
        if (term == null) return null;
        return term.toUpperCase();
    }
}

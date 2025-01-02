package com.mixfa.football_management.misc;

import java.util.List;
import java.util.Map;

public interface ValidationErrors {
    Map<String, String> errorIdToMessageMap();

    default List<MySQLTrigger> triggers() {
        return List.of();
    }

    static String makeErrorId(String tableName, String errorId) {
        return tableName + "." + errorId;
    }
}

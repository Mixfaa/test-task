package com.mixfa.football_management.misc;

import java.util.List;
import java.util.Map;

/**
 * Interface helps  triggers to maintain database integrity
 */
public interface DbValidation {
    Map<String, String> errorIdToMessageMap();

    default List<MySQLTrigger> triggers() {
        return List.of();
    }

    static String makeErrorId(String tableName, String errorId) {
        return STR."\{tableName}.\{errorId}";
    }
}

package com.mixfa.football_management.misc;

import java.util.List;
import java.util.Map;

/**
 * Interface helps setup triggers to maintain database integrity
 */
public interface DbValidation {
    /**
     * Error id is used as constraint name, to determine error message
     * error id = table_name.constraint_name
     */
    Map<String, String> errorIdToMessageMap();

    default List<MySQLTrigger> triggers() {
        return List.of();
    }

    static String makeErrorId(String tableName, String errorId) {
        return STR."\{tableName}.\{errorId}";
    }
}

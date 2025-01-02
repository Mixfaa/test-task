package com.mixfa.football_management.service;

import com.mixfa.football_management.misc.MySQLTrigger;
import com.mixfa.football_management.misc.ValidationErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DBLayerValidation implements CommandLineRunner {
    private final Map<String, String> errorIdToMsgMap;
    private final JdbcTemplate jdbcTemplate;
    private final List<ValidationErrors> validationErrors;
    private final static String UNKNOWN_ERROR = "Unknown error";

    public DBLayerValidation(List<ValidationErrors> validationErrors, JdbcTemplate jdbcTemplate) {
        this.validationErrors = validationErrors;
        this.jdbcTemplate = jdbcTemplate;
        this.errorIdToMsgMap = new HashMap<>();
        for (ValidationErrors errors : validationErrors)
            errorIdToMsgMap.putAll(errors.errorIdToMessageMap());
    }

    public String resolveErrorMessage(String errorId) {
        if (errorId == null) return UNKNOWN_ERROR;
        return errorIdToMsgMap.getOrDefault(errorId, UNKNOWN_ERROR);
    }

    private void setupTrigger(MySQLTrigger trigger) {
        try {
            jdbcTemplate.execute(STR."DROP TRIGGER IF EXISTS \{trigger.name()};");
            jdbcTemplate.execute(trigger.code());
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
        }
    }

    @Override
    public void run(String... args) throws Exception {
        for (ValidationErrors errors : validationErrors)
            errors.triggers().forEach(this::setupTrigger);
    }
}

package com.mixfa.football_management;

import com.mixfa.football_management.exception.factory.DebugExceptionsFactory;
import com.mixfa.football_management.exception.factory.ExceptionFactory;
import com.mixfa.football_management.exception.factory.FastExceptionsFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableTransactionManagement
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class FootballManagementApplication {
    @Bean
    public ExceptionFactory exceptionFactory(@Value("${exception.use-debug-factory}") boolean useDebugFactory) {
        return useDebugFactory ? new DebugExceptionsFactory() : new FastExceptionsFactory();
    }

    public static void main(String[] args) {
        SpringApplication.run(FootballManagementApplication.class, args);
    }

}

package ru.chelenkov.paymentservice.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class DatabaseHealthCheck implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) {
        dbHealthCheck();
        redisHealthCheck();
    }

    private void dbHealthCheck() {
        try {
            String database = jdbcTemplate.queryForObject("select current_database()", String.class);
            log.info("PASSED. Connection to PostgreSQL to database: {}", database);

            String version = jdbcTemplate.queryForObject("select version()", String.class);
            log.info("PASSED. PostgreSQL version: {}", version);

        } catch (Exception e) {
            log.error("FAILED. Connection PostgreSQL:{}", e.getMessage());
        }
    }

    private void redisHealthCheck() {
        try {
            String key = "health:check";
            String value = "OK";

            redisTemplate.opsForValue().set(key, value);
            String resultQuery = (String) redisTemplate.opsForValue().get(key);
            Optional<String> resultQueryOpt = Optional.ofNullable(resultQuery);

            resultQueryOpt.ifPresentOrElse(x -> {
                if (x.equals("OK")) {
                    log.info("PASSED. Redis health check OK");
                    redisTemplate.delete(key);
                }
            }, () -> log.info("FAILED. Redis health check"));

        } catch (Exception e) {
            log.error("FAILED. Redis health check:{}", e.getMessage());
        }
    }
}

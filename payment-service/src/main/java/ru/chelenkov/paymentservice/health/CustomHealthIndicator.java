package ru.chelenkov.paymentservice.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {
    private final static String UP = "UP";
    private final static String DOWN = "DOWN";
    private final static String SQL_CURRENT_DATABASE = "select current_database()";
    private final static String SQL_VERSION = "select version()";
    private final static String SQL_COUNT_ACTIVE_CONNECTION =
            "SELECT count(*) "
                    + " FROM pg_stat_activity "
                    + "WHERE datname = current_database()";


    private final RedisTemplate<String, Object> redisTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();

        var isPostgreAlive = checkPostgreSqlHealth(details);
        var isRedisAlive = checkRedisHealth(details);

        if (isPostgreAlive && isRedisAlive) {
            return Health.up()
                    .withDetails(details)
                    .build();
        } else {
            return Health.down()
                    .withDetails(details)
                    .build();
        }
    }

    private boolean checkRedisHealth(Map<String, Object> details) {
        try {
            String keyCheck = "health:ping";

            redisTemplate.opsForValue().set(keyCheck, "pong");
            String resultCheck = (String) redisTemplate.opsForValue().get(keyCheck);


            redisTemplate.delete(keyCheck);

            if (resultCheck == null) {
                details.put("redis.status", DOWN);
                details.put("redis.error", "Result query is null");
                return false;
            }

            if (resultCheck.equals("pong")) {
                details.put("redis.status", UP);
                details.put("redis.responseTime", "<1");
                return true;
            } else {
                details.put("redis.status", DOWN);
                details.put("redis.error", String.format("Unexpected result %s", resultCheck));
                return false;
            }

        } catch (Exception e) {
            details.put("redis.status", DOWN);
            details.put("redis.error", e.getMessage());
            return false;
        }
    }

    private boolean checkPostgreSqlHealth(Map<String, Object> details) {
        try {

            String dataBase = jdbcTemplate.queryForObject(SQL_CURRENT_DATABASE, String.class);
            String version = jdbcTemplate.queryForObject(SQL_VERSION, String.class);
            Integer connections = jdbcTemplate.queryForObject(SQL_COUNT_ACTIVE_CONNECTION, Integer.class);

            details.put("postgresql.status", UP);
            details.put("postgresql.database", dataBase);
            details.put("postgresql.version", version);
            details.put("postgresql.connections", connections);

            return true;

        } catch (Exception e) {
            details.put("postgresql.status", DOWN);
            details.put("postgresql.error", e.getMessage());

            return false;
        }
    }
}

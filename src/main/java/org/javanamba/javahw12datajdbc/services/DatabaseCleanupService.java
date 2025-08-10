package org.javanamba.javahw12datajdbc.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseCleanupService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void clearDatabase() {
        // Отключение проверки внешних ключей
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        // Получение списка всех таблиц в базе данных
        List<String> tables = jdbcTemplate.queryForList("SHOW TABLES", String.class);

        // Очистка каждой таблицы
        for (String table : tables) {
            if (!table.equals("flyway_schema_history")) {
                jdbcTemplate.execute("TRUNCATE TABLE " + table);
            }
        }

        // Включение проверки внешних ключей
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}

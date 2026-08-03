package com.missioncontrol.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource(@Value("${app.db.path}") String dbPath) throws Exception {
        Path path = Path.of(dbPath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setJdbcUrl("jdbc:sqlite:" + dbPath);
        dataSource.setMaximumPoolSize(1);
        return dataSource;
    }
}

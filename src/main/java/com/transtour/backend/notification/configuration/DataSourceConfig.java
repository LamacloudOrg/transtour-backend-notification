package com.transtour.backend.notification.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://mysql-notification:3307/notification?useSSL=false");
        dataSourceBuilder.username("transtourRoot");
        dataSourceBuilder.password("transtourRoot");
        return dataSourceBuilder.build();
    }
}
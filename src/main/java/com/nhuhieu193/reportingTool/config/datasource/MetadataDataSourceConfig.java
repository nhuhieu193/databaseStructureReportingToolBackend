package com.nhuhieu193.reportingTool.config.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MetadataDataSourceConfig {

    @Bean(name = "metadataDataSource")
    @ConfigurationProperties(prefix = "metadata.datasource")
    public DataSource metadataDataSource() {
        return DataSourceBuilder.create().build();
    }
}

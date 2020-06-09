package com.scalar.ist.api.config;

import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.service.TransactionModule;
import com.scalar.db.service.TransactionService;
import java.io.IOException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class AppConfig {
  public static final String NAMESPACE = "ist";
  private final String scalarDbConfigFilePath;

  public AppConfig(@Value("${scalardb.config}") String scalarDbConfigFilePath) {
    this.scalarDbConfigFilePath = scalarDbConfigFilePath;
  }

  @Bean
  @Scope("singleton")
  DistributedTransactionManager createScalarDBTransactionManager() throws IOException {
    DatabaseConfig scalarDBConfig =
        new DatabaseConfig(new URL(scalarDbConfigFilePath).openConnection().getInputStream());
    return Guice.createInjector(new TransactionModule(scalarDBConfig))
        .getInstance(TransactionService.class);
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
        .protocols(Sets.newHashSet("https"))
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("IST API")
        .version("1.0.0")
        .contact(
            new Contact("Scalar Engineering Team", null, "engineering-service@scalar-labs.com"))
        .build();
  }
}

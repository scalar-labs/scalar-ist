package com.scalar.ist.api.config;

import com.scalar.ist.api.exceptionhandling.RestExceptionHandler;
import com.scalar.ist.api.security.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configure security parameters for HTTP requests */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

  private final RestExceptionHandler restExceptionHandler;
  private final JwtValidationFilter jwtValidationFilter;

  @Autowired
  public WebSecurityConfig(
      RestExceptionHandler restExceptionHandler, JwtValidationFilter jwtValidationFilter) {
    this.restExceptionHandler = restExceptionHandler;
    this.jwtValidationFilter = jwtValidationFilter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().formLogin().disable();
    http.cors();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.requiresChannel().anyRequest().requiresSecure();
    // By default all request need to be authenticated excepts the one whitelisted
    // in the "configure(WebSecurity web)" method below
    http.authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .anonymous()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(restExceptionHandler)
        .accessDeniedHandler(restExceptionHandler)
        .and()
        .addFilterBefore(jwtValidationFilter, BasicAuthenticationFilter.class);
  }

  @Override
  public void configure(WebSecurity web) {
    // White list requests
    web.ignoring()
        // Swagger ui
        .antMatchers(
            HttpMethod.GET,
            "/v2/api-docs",
            "/swagger-ui.html",
            "/webjars/springfox-swagger-ui/**",
            "/swagger-resources/**");
  }

  // Global coors configuration
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedMethods(HttpMethod.GET.name());
  }
}

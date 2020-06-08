package com.scalar.ist.api.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.scalar.ist.api.exceptionhandling.RestExceptionHandler;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class JwtValidationFilterTest {
  private static final String ISSUER = "Auth0";
  @Mock RestExceptionHandler restExceptionHandler;
  JwtValidationFilter jwtValidationFilter;
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;
  @Mock FilterChain filterChain;

  @Before
  public void setup() {
    jwtValidationFilter = spy(new JwtValidationFilter(restExceptionHandler, ISSUER));
  }

  @Test
  public void doFilterInternal_emptyAuthorizationHeader_ShouldNotThrowException()
      throws ServletException, IOException {
    // Prepare
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");

    // Act
    jwtValidationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(restExceptionHandler, never()).handleAuthenticationError(any(), any(), any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void doFilterInternal_validJwt_ShouldAuthenticatePrincipal()
      throws ServletException, IOException {
    // Arrange
    String companyId = "foo org";
    String subject = UUID.randomUUID().toString();
    Date issuedAt = new Date();
    Date expirationDate = new Date(3600 * 1000 + issuedAt.getTime());
    String jwt =
        Jwts.builder()
            .claim(JwtValidationFilter.COMPANY_ID_CLAIM, companyId)
            .setSubject(subject)
            .signWith(SignatureAlgorithm.HS256, JwtValidationFilter.TEMPORARY_JWT_SECRET)
            .setIssuer(ISSUER)
            .setIssuedAt(issuedAt)
            .setNotBefore(issuedAt)
            .setExpiration(expirationDate)
            .compact();
    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + jwt);

    // Act
    jwtValidationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .isEqualTo(Principal.builder().companyId(companyId).holderId(subject).build());
    verifyNoInteractions(restExceptionHandler);
    verify(filterChain).doFilter(request, response);
  }
}

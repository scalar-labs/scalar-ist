package com.scalar.ist.api.security;

import com.google.common.annotations.VisibleForTesting;
import com.scalar.ist.api.exceptionhandling.RestExceptionHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MissingClaimException;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validate and authenticates request using a JWT authentication scheme
 */
@Component
public class JwtValidationFilter extends OncePerRequestFilter {
  @VisibleForTesting static final String COMPANY_ID_CLAIM = "company_id";
  // TODO Use a real secret when the final JWT design gets decided
  @VisibleForTesting
  static final String TEMPORARY_JWT_SECRET = "K7d$j=0x:8^nV?aC-8u2f]As/u?N56eywSb";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final RestExceptionHandler restExceptionHandler;
  private final String jwtIssuer;

  @Autowired
  JwtValidationFilter(
      RestExceptionHandler restExceptionHandler, @Value("${jwt.issuer}") String jwtIssuer) {
    this.restExceptionHandler = restExceptionHandler;
    this.jwtIssuer = jwtIssuer;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    try {
      Optional<String> jwtToken = getJwtToken(request);
      jwtToken.ifPresent(
          jwt -> {
            assertJwtValidity(jwt);
            authenticateWithJwtToken(jwt, request);
          });
      // Since Servlet filter are not part of the spring framework, thrown exception will not be
      // caught by the Spring global exception handler class "RestExceptionHandler"
      // So we need to call the restExceptionHandler explicitly
    } catch (Exception e) {
      restExceptionHandler.handleAuthenticationError(request, response, e);
      return;
    }

    chain.doFilter(request, response);
  }

  /**
   * Authenticate the user using a JWT
   *
   * @param request the incoming http request
   */
  private void authenticateWithJwtToken(String jwt, HttpServletRequest request) {
    Claims jwtClaims = getAllClaimsFromToken(jwt);
    String subjectId = jwtClaims.getSubject();
    String companyId = jwtClaims.get(COMPANY_ID_CLAIM, String.class);

    if (companyId == null || companyId.equals("")) {
      throw new AuthenticationException("The company_id claim is missing from the jwt");
    }
    setAuthenticatedPrincipal(request, subjectId, companyId);
  }

  private Claims getAllClaimsFromToken(String jwtToken) {
    return Jwts.parser()
        .requireIssuer(jwtIssuer)
        .setSigningKey(TEMPORARY_JWT_SECRET)
        .parseClaimsJws(jwtToken)
        .getBody();
  }

  private void assertJwtValidity(String jwt) {
    try {
      if (getAllClaimsFromToken(jwt) == null) {
        throw new AuthenticationException("The JWT body is missing");
      }
    } catch (IllegalArgumentException e) {
      throw new AuthenticationException("Error getting subject id from jwt token", e);
    } catch (ExpiredJwtException e) {
      throw new AuthenticationException("Error validating the jwt token", e);
    } catch (IncorrectClaimException e) {
      throw new AuthenticationException("Error validating the jwt token claim", e);
    } catch (MissingClaimException e) {
      throw new AuthenticationException("Error getting the jwt token claim", e);
    }
  }

  /**
   * Set the security context with the authenticated user holderId and companyId which then can be accessed directly
   * by the controller layer
   * @param request the incoming http request
   * @param holderId the authenticated user holderId
   * @param companyId the authencicated user companyId
   */
  private void setAuthenticatedPrincipal(
      HttpServletRequest request, String holderId, String companyId) {
    Principal principal = Principal.builder().holderId(holderId).companyId(companyId).build();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(principal, null, null);
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    logger.info("Authorized user '{}', setting security context", principal);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  /**
   * Return the JWT string present in the Authorization header
   * @param request the incoming request
   * @return an optional JWT
   */
  private Optional<String> getJwtToken(HttpServletRequest request) {
    String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
      return Optional.of(requestHeader.substring(7));
    }

    return Optional.empty();
  }
}

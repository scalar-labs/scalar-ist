package com.scalar.ist.api.mock.security_context;

import com.scalar.ist.api.security.Principal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockAuthenticatedUserFactory implements WithSecurityContextFactory<MockAuthenticatedUser> {
  @Override
  public SecurityContext createSecurityContext(MockAuthenticatedUser mockAuthenticatedUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Principal principal =
        Principal.builder().holderId(mockAuthenticatedUser.holderId()).companyId(mockAuthenticatedUser.companyId()).build();
    Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, null);
    context.setAuthentication(auth);
    return context;
  }
}

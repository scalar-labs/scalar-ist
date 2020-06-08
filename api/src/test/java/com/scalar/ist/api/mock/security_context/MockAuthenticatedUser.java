package com.scalar.ist.api.mock.security_context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Annotation that will set the security context with an authenticated user for testing purpose.
 * Adding this annotation on a test class or method is equivalent to a user
 * having been authenticated.
 *
 * @see <a
 * href="https://docs.spring.io/spring-security/site/docs/current/reference/html/test.html#test-method-withsecuritycontext">Spring
 * security documentation</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockAuthenticatedUserFactory.class)
public @interface MockAuthenticatedUser {
  String holderId();
  String companyId();
}

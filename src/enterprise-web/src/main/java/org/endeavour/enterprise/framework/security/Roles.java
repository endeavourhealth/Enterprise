package org.endeavour.enterprise.framework.security;

import org.endeavourhealth.enterprise.core.entity.EndUserRole;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Roles {
    EndUserRole[] value() default {};
}

package org.endeavour.enterprise.authentication;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;

@Provider
public class AuthenticationFilterFeature implements DynamicFeature
{
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext)
    {
        final String PACKAGE_NAME_PREFIX = "org.endeavour.enterprise";

        if ((resourceInfo.getResourceClass().getName().startsWith(PACKAGE_NAME_PREFIX)))
        {
            if (!containsUnsecuredAnnotation(resourceInfo.getResourceMethod().getAnnotations()))
            {
                AuthenticationFilter authenticationFilter = new AuthenticationFilter();
                featureContext.register(authenticationFilter);
            }
        }
    }

    private static boolean containsUnsecuredAnnotation(Annotation[] annotations)
    {
        for (Annotation annotation : annotations)
            if (annotation.annotationType().equals(Unsecured.class))
                return true;

        return false;
    }
}

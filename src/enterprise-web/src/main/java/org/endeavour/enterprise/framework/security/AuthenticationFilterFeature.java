package org.endeavour.enterprise.framework.security;

import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;

@Provider
public class AuthenticationFilterFeature implements DynamicFeature
{
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {


        String packageName = (String) featureContext.getConfiguration().getProperties().get(ServerProperties.PROVIDER_PACKAGES);

        boolean usePackageFilter = true;
        if ((packageName == null) || (packageName.trim().equals(""))) {
            usePackageFilter = false;

            // raise error
        }

        if ((!usePackageFilter) || (resourceInfo.getResourceClass().getName().startsWith(packageName))) {

            Class filterClass = getFilterClass(resourceInfo.getResourceMethod().getAnnotations());
            if (filterClass != null) {
                featureContext.register(filterClass);
            }

            /*if (!containsUnsecuredAnnotation(resourceInfo.getResourceMethod().getAnnotations())) {

                featureContext.register(AbstractAuthenticationFilter.class);
            }*/
        }
    }

    private static Class getFilterClass(Annotation[] annotations) {

        //default is the "user" filter
        Class ret = UserAuthenticationFilter.class;

        for (Annotation annotation : annotations) {
            Class clz = annotation.annotationType();
            if (clz.equals(Unsecured.class)) {
                //if we have the unsecured annotation, then we don't want to apply a filter at all
                ret = null;
            } else if (clz.equals(RequiresSuperUser.class)) {
                ret = SuperUserAuthenticationFilter.class;
            } else if (clz.equals(RequiresAdmin.class)) {
                ret = AdminAuthenticationFilter.class;
            }
        }

        return ret;
    }

    /*private static boolean containsUnsecuredAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations)
            if (annotation.annotationType().equals(Unsecured.class))
                return true;

        return false;
    }*/
}

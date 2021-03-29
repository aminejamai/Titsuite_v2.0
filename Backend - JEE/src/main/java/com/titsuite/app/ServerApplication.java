package com.titsuite.app;

import com.titsuite.diplomas.DiplomaResource;
import com.titsuite.jobs.JobsResource;
import com.titsuite.offers.OffersResource;
import com.titsuite.filters.AuthenticationFilter;
import com.titsuite.utils.DebugMapper;
import com.titsuite.users.UserResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ServerApplication extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(
            AuthenticationFilter.class, DebugMapper.class, UserResource.class, DiplomaResource.class,
            OffersResource.class, JobsResource.class
        ));
    }
}
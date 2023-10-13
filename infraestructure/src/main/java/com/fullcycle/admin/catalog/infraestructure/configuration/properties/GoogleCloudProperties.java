package com.fullcycle.admin.catalog.infraestructure.configuration.properties;

public class GoogleCloudProperties {
//    private static final Logger log = LoggerFactory.getLogger(GoogleCloudProperties.class);
    private String credentials;
    private String projectId;

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

//    @Override
//    public String toString() {
//        return "GoogleCloudProperties{" +
//                "credentials='" + credentials + '\'' +
//                ", projectId='" + projectId + '\'' +
//                '}';
//    }
}

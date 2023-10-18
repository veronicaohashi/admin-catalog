package com.fullcycle.admin.catalog.infraestructure.configuration.properties.storage;

public class StorageProperties {
    private String locationPattern;
    private String filenamePattern;

    public StorageProperties() {}

    public String getLocationPattern() {
        return locationPattern;
    }

    public void setLocationPattern(String locationPattern) {
        this.locationPattern = locationPattern;
    }

    public String getFilenamePattern() {
        return filenamePattern;
    }

    public void setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;
    }
}

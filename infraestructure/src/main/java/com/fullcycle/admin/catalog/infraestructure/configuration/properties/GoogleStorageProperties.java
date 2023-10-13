package com.fullcycle.admin.catalog.infraestructure.configuration.properties;

public class GoogleStorageProperties {
//    private static final Logger log = LoggerFactory.getLogger(GoogleStorageProperties.class);
    private String bucket;
    private int connectTimeout;
    private int readTimeout;
    private int retryDelay;
    private int retryMaxAttempts;
    private int retryMaxDelay;
    private double retryMultiplier;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    public int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public void setRetryMaxAttempts(int retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
    }

    public int getRetryMaxDelay() {
        return retryMaxDelay;
    }

    public void setRetryMaxDelay(int retryMaxDelay) {
        this.retryMaxDelay = retryMaxDelay;
    }

    public double getRetryMultiplier() {
        return retryMultiplier;
    }

    public void setRetryMultiplier(double retryMultiplier) {
        this.retryMultiplier = retryMultiplier;
    }

//    @Override
//    public String toString() {
//        return "GoogleStorageProperties{" +
//                "bucket='" + bucket + '\'' +
//                ", connectTimeout=" + connectTimeout +
//                ", readTimeout=" + readTimeout +
//                ", retryDelay=" + retryDelay +
//                ", retryMaxAttempts=" + retryMaxAttempts +
//                ", retryMaxDelay=" + retryMaxDelay +
//                ", retryMultiplier=" + retryMultiplier +
//                '}';
//    }
}

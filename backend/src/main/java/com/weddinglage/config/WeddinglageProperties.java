package com.weddinglage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weddinglage")
public class WeddinglageProperties {

    private String tokenPepper = "dev-pepper-change-me";
    private String adminEmail = "admin@example.com";
    private String adminPassword = "change-me-admin";
    private int maxFailedLogins = 5;
    private int lockoutMinutes = 15;
    private int rsvpRateLimitPerMinute = 30;
    private final Minio minio = new Minio();

    public String getTokenPepper() {
        return tokenPepper;
    }

    public void setTokenPepper(String tokenPepper) {
        this.tokenPepper = tokenPepper;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public int getMaxFailedLogins() {
        return maxFailedLogins;
    }

    public void setMaxFailedLogins(int maxFailedLogins) {
        this.maxFailedLogins = maxFailedLogins;
    }

    public int getLockoutMinutes() {
        return lockoutMinutes;
    }

    public void setLockoutMinutes(int lockoutMinutes) {
        this.lockoutMinutes = lockoutMinutes;
    }

    public int getRsvpRateLimitPerMinute() {
        return rsvpRateLimitPerMinute;
    }

    public void setRsvpRateLimitPerMinute(int rsvpRateLimitPerMinute) {
        this.rsvpRateLimitPerMinute = rsvpRateLimitPerMinute;
    }

    public Minio getMinio() {
        return minio;
    }

    public static class Minio {
        private String endpoint = "http://localhost:9000";
        private String accessKey = "minio";
        private String secretKey = "change-me-too";
        private String bucket = "wedding-photos";
        private String region = "us-east-1";

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }
}

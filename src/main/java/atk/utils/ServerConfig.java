package atk.utils;


import atk.SharedObjects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.IOException;

public class ServerConfig {
    public static ServerConfig readConfig(File file) throws IOException {
        return SharedObjects.objectMapper.readValue(file, ServerConfig.class);
    }

    @JsonCreator
    public ServerConfig(@JsonProperty("twilioSid") String dbUri,
                        @JsonProperty("twilioAuthToken") String dbUser,
                        @JsonProperty("dbPassword") String dbPassword,
                        @JsonProperty("twilioSid") String twilioSid,
                        @JsonProperty("twilioAuthToken") String twilioAuthToken) {
        this.dbUri = dbUri;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.twilioSid = twilioSid;
        this.twilioAuthToken = twilioAuthToken;
    }

    public final String dbUri;
    public final String dbUser;
    public final String dbPassword;
    public final String twilioSid;
    public final String twilioAuthToken;
}
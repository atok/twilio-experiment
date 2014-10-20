package atk.utils;


import atk.SharedObjects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.IOException;

public class LocalConfig {
    public static LocalConfig readConfig(File file) throws IOException {
        return SharedObjects.objectMapper.readValue(file, LocalConfig.class);
    }

    @JsonCreator
    public LocalConfig(@JsonProperty("twilioSid") String twilioSid,
                       @JsonProperty("twilioAuthToken") String twilioAuthToken) {
        this.twilioSid = twilioSid;
        this.twilioAuthToken = twilioAuthToken;
    }

    public final String twilioSid;
    public final String twilioAuthToken;
}

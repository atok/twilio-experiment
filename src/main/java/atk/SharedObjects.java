package atk;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

public class SharedObjects {
    public static final ObjectMapper objectMapper; // THREAD SAFE
    static {
        objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
    }


    public static final MustacheFactory mustacheFactory = new DefaultMustacheFactory(); // THREAD SAFE
}

package atk;

import atk.test.TestConfig;
import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class FirstTests {

    @Before
    public void setup() {
        ConsoleAppender console = new ConsoleAppender(); //create appender
        //configure the appender
        String PATTERN = "%d [%p|%c|%C{1}] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.WARN);
        console.activateOptions();
        //add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(console);
    }

    @Test
    public void indexTest() throws UnirestException {
        HttpResponse<String> jsonResponse = Unirest.get(TestConfig.BASE + "/json").asString();
        int id2 = JsonPath.read(jsonResponse.getBody(), "$.student.id");
        assertEquals(id2, 123);
    }

    @Test
    public void notImplemented() throws UnirestException {
        HttpResponse<String> response = Unirest.post(TestConfig.BASE + "/r/student").asString();
        assertEquals(405, response.getCode());
    }

    @Test
    public void exceptionError() throws UnirestException {
        HttpResponse<String> response = Unirest.delete(TestConfig.BASE + "/r/student").asString();
        assertEquals(500, response.getCode());
    }
}

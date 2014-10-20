package atk;

import atk.handlers.ErrorHandler;
import atk.handlers.TwilioClientHandler;
import atk.utils.LocalConfig;
import atk.utils.ServerConfig;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

public class ExperimentServer {

    public static void main(final String[] args) throws IOException {

        AccessLogReceiver logReceiver = new AccessLogReceiver() {
            @Override
            public void logMessage(String message) {
//                System.out.println(message);
            }
        };

        ServerConfig config = ServerConfig.readConfig(new File("config.json"));
        LocalConfig localConfig = LocalConfig.readConfig(new File("local.json"));

        final DataSource db = newDataSource(config.dbUri, config.dbUser, config.dbPassword);

        FileResourceManager fileResourceManager = new FileResourceManager(new File("static/"), 100);

        ResourceHandler resourceHandler = Handlers.resource(fileResourceManager)
                .addWelcomeFiles("index.html");
        resourceHandler.setCanonicalizePaths(true);
        resourceHandler.setCacheTime(6000);
        resourceHandler.setCachable(value -> true);

        HttpHandler routingHandlers = Handlers.path()
                .addExactPath("/twilioClient", new TwilioClientHandler(resourceHandler, localConfig.twilioSid, localConfig.twilioAuthToken))
                .addExactPath("/", resourceHandler)
                .addPrefixPath("/static", resourceHandler)
                .addPrefixPath("/exception", exchange -> {
                    throw new RuntimeException("test exception");
                })
                ;

        Undertow server = Undertow.builder()
                .addHttpListener(9090, "0.0.0.0")
                .setHandler(new ErrorHandler(new AccessLogHandler(routingHandlers, logReceiver, "common", ExperimentServer.class.getClassLoader())))
                .build();
        server.start();
    }

    static DataSource newDataSource(String uri, String user, String password) {
        GenericObjectPool connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(256);
        connectionPool.setMaxIdle(256);
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(uri, user, password);
        //
        // This constructor modifies the connection pool, setting its connection
        // factory to this.  (So despite how it may appear, all of the objects
        // declared in this method are incorporated into the returned result.)
        //
        new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }
}

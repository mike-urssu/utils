package logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingForJava {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void printLogs() {
        log.trace("trace");
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");
    }

    public static void main(String[] args) {
        new LoggingForJava().printLogs();
    }
}

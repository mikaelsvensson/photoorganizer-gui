package info.photoorganizer.gui.shared;

import java.text.MessageFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging
{
    private static boolean initialized = false;
    public synchronized static Logger getLogger(Class<?> c)
    {
        if (!initialized)
        {
            Logger rootLogger = Logger.getLogger("");
            //rootLogger.setLevel(Level.ALL);
            
            for (Handler handler : rootLogger.getHandlers())
            {
                handler.setLevel(Level.ALL);
                if (handler instanceof ConsoleHandler)
                {
                    ConsoleHandler consoleHandler = (ConsoleHandler) handler;
                    consoleHandler.setFormatter(new Formatter() {

                        @Override
                        public String format(LogRecord record)
                        {
                            String loggerLabel = record.getLoggerName();
                            loggerLabel = loggerLabel.substring(loggerLabel.lastIndexOf('.')+1);
                            return String.format("%5d %-30s %s\n", record.getSequenceNumber(), loggerLabel, record.getMessage());
                        }
                        
                    });
                }
            }
            
            Logger poLogger = Logger.getLogger("info.photoorganizer");
            poLogger.setLevel(Level.ALL);
            
            initialized = true;
        }
        return Logger.getLogger(c.getName());
    }
}

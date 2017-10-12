package com.minecolonies.rankup.qsml;

import org.slf4j.Logger;
import uk.co.drnaylor.quickstart.LoggerProxy;

public class RankupLoggerProxy implements LoggerProxy
{
    private final Logger logger;

    public RankupLoggerProxy(Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void info(String message)
    {
        this.logger.info(message);
    }

    @Override
    public void warn(String message)
    {
        this.logger.warn(message);
    }

    @Override
    public void error(String message)
    {
        this.logger.error(message);
    }
}

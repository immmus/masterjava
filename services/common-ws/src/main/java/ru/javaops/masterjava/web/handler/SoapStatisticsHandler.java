package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import ru.javaops.masterjava.web.Statistics;

public class SoapStatisticsHandler extends SoapBaseHandler {
    private final static String START = "startTime";
    private final static String PAYLOAD = "payload";

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        if (isOutbound(context)) {
            setStatistics(context, Statistics.RESULT.SUCCESS);
        } else {
            final long startTime = System.currentTimeMillis();
            final String payload = context.getMessage().getPayloadLocalPart();
            context.put(START, startTime);
            context.put(PAYLOAD, payload);
        }
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        setStatistics(context, Statistics.RESULT.FAIL);
        return false;
    }
    private void setStatistics(MessageHandlerContext context, Statistics.RESULT result) {
        Statistics.count((String) context.get(PAYLOAD), (long) context.get(START), result);
    }
}

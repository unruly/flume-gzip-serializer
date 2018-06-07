package co.unruly.flume;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.serialization.EventSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class GzipSerializer implements EventSerializer {

    private final static Logger logger = LoggerFactory.getLogger(GzipSerializer.class);

    private final boolean appendNewline;
    private GZIPOutputStream gStream;

    GzipSerializer(OutputStream stream, Context context) {

        // Default behaviour is to append a new-line, but we must honour the appendNewline parameter if specified

        this.appendNewline = context.getBoolean(
            FlumeParameters.APPEND_NEWLINE,
            FlumeParameters.APPEND_NEWLINE_DEFAULT
        );

        try {
            this.gStream = new GZIPOutputStream(stream);
        } catch (IOException e) {
            logger.warn(e.toString());
        }
    }

    @Override
    public void write(Event event) throws IOException {
        gStream.write(event.getBody());

        if (appendNewline) {
            gStream.write('\n');
        }
    }

    @Override
    public void beforeClose() throws IOException {
        gStream.finish();
    }

    @Override
    public boolean supportsReopen() { return false; }

    @Override
    public void afterCreate() throws IOException { logger.debug(getClass() + "::afterCreate"); }

    @Override
    public void afterReopen() throws IOException { logger.debug(getClass() + "::afterReopen"); }

    @Override
    public void flush() throws IOException { logger.debug(getClass() + "::flush"); }

    public static class Builder implements EventSerializer.Builder {
        @Override
        public EventSerializer build(Context context, OutputStream out) {
            return new GzipSerializer(out, context);
        }
    }
}
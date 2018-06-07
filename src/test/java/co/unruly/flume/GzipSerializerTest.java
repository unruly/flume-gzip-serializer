package co.unruly.flume;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import static org.apache.commons.io.IOUtils.toByteArray;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GzipSerializerTest {

    @Test
    public void shouldGzipData() throws IOException {
        String input = "this-is-some-content";
        Event event = eventWithContent(input);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        writeEventToSerializer(event, outputStream, new Context());

        assertThat(decompress(outputStream.toByteArray()), is(input+"\n"));
    }

    @Test
    public void shouldGzipData_withoutNewlines_ifAppendNewLineIsFalse() throws IOException {
        String input = "this-is-some-content";
        Event event = eventWithContent(input);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Context context = new Context();
        context.put("appendNewline","false");

        writeEventToSerializer(event, outputStream, context);

        assertThat(decompress(outputStream.toByteArray()), is(input));
    }

    private Event eventWithContent(String content) {
        Event event = new SimpleEvent();
        event.setBody(content.getBytes());
        return event;
    }

    private String decompress(byte[] compressed) throws IOException {
        GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressed));

        return new String(toByteArray(gzipInputStream), "UTF-8");
    }

    private void writeEventToSerializer(Event event, OutputStream outputStream, Context context) throws IOException {
        GzipSerializer gzipSerializer = new GzipSerializer(outputStream, context);
        gzipSerializer.write(event);
        gzipSerializer.beforeClose();
    }

}
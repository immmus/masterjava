package ru.javaops.masterjava.service.mail.util;

import org.apache.commons.io.IOUtils;
import ru.javaops.masterjava.service.mail.Attachment;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.*;

public class Attachments {
    public static Attachment getAttachment(String name, InputStream inputStream) {
        return new Attachment(name, new DataHandler(new InputStreamDataSource(inputStream)));
    }
    public static Attachment getAttachment(String name, byte[] data) {
        return new Attachment(name, new DataHandler(new InputStreamDataSource(data)));
    }

    //    http://stackoverflow.com/questions/2830561/how-to-convert-an-inputstream-to-a-datahandler
    //    http://stackoverflow.com/a/10783565/548473
    private static class InputStreamDataSource implements DataSource {
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        InputStreamDataSource(InputStream inputStream) {
            if (inputStream != null) {
                try {
                    byte[] bytes = IOUtils.toByteArray(inputStream);
//                    int nRead;
//                    byte[] data = new byte[16384];
//                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//                        buffer.write(data, 0, nRead);
//                    }
                    buffer.write(bytes, 0, bytes.length - 1);
                    buffer.flush();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalArgumentException("InputStream is null");
            }
        }
        InputStreamDataSource(byte[] data) {
            if (data.length > 0) {
                try {
                    buffer.write(data, 0, data.length - 1);
                    buffer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(buffer.toByteArray());
        }

        @Override
        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public String getName() {
            return "";
        }
    }
}

package ru.javaops.masterjava.service.mail.util;

import ru.javaops.masterjava.service.mail.Attachment;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.*;

public class Attachments {
    public static Attachment getAttachment(String name, InputStream inputStream) {
        return new Attachment(name, new DataHandler(new InputStreamDataSource(inputStream)));
    }

    //    http://stackoverflow.com/questions/2830561/how-to-convert-an-inputstream-to-a-datahandler
    //    http://stackoverflow.com/a/10783565/548473
    private static class InputStreamDataSource implements DataSource {
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        public InputStreamDataSource(InputStream inputStream) {
            if (inputStream != null) {
                try {
                    int nRead;
                    byte[] data = new byte[16384];
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    buffer.flush();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new IllegalArgumentException("InputStream is null");
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

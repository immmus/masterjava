package ru.javaops.masterjava.service.mail.utils;

import ru.javaops.masterjava.service.mail.Attachment;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.*;

public class Attachments {
    public static Attachment create(String name, InputStream is){
        return new Attachment(name, new DataHandler(new InputStreamDataSource(is, name)));
    }
    private static class InputStreamDataSource implements DataSource {
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private final String name;

        public InputStreamDataSource(InputStream inputStream, String name) {
            this.name = name;
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

        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public InputStream getInputStream(){
            return new ByteArrayInputStream(buffer.toByteArray());
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException("Read-only data");
        }
    }
}

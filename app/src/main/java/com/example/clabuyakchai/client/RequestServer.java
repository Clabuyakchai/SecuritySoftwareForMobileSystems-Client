package com.example.clabuyakchai.client;

import android.content.Intent;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Clabuyakchai on 31.03.2018.
 */

public class RequestServer {

    private static final Uri ENDPOINT = Uri.parse("https://secure-waters-60346.herokuapp.com/api/stuff").buildUpon().build();
    private static final String IDDEVICE = "id";
    private static final String ENCDATA = "encdata";

    public byte[] requestServer(String urlspec) throws IOException {
        URL url = new URL(urlspec);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlspec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }

            out.close();

            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String startMethod(String encdata, String idDevice) throws IOException {
        byte[] byteResponse = requestServer(buildUrl(encdata, idDevice));
        return new String(byteResponse);
    }

    private String buildUrl(String encadata, String idDevice){
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter(IDDEVICE, idDevice)
                .appendQueryParameter(ENCDATA, encadata);

        return builder.toString();
    }
}

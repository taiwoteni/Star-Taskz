package com.theteam.taskz.domain.repositories;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {
    private static final String PROTOCOL_CHARSET = "utf-8";
    private final Response.Listener<String> mListener;
    private final Map<String, String> mHeaders;
    private final Map<String, String> mStringParts;
    private final Map<String, DataPart> mFileParts;
    private static final String BOUNDARY = "apiclient-" + System.currentTimeMillis();
    private static final String MULTIPART_FORM_DATA = "multipart/form-data;boundary=" + BOUNDARY;

    public MultipartRequest(String url, Map<String, String> headers, int method, Map<String, String> stringParts,
                            Map<String, DataPart> fileParts, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mHeaders = headers != null ? headers : new HashMap<>();
        this.mStringParts = stringParts;
        this.mFileParts = fileParts;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Adding text part
            if (mStringParts != null) {
                for (Map.Entry<String, String> entry : mStringParts.entrySet()) {
                    buildTextPart(bos, entry.getKey(), entry.getValue());
                }
            }

            // Adding file part
            if (mFileParts != null) {
                for (Map.Entry<String, DataPart> entry : mFileParts.entrySet()) {
                    buildFilePart(bos, entry.getKey(), entry.getValue());
                }
            }

            // End of multipart/form-data
            bos.write(("--" + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new com.android.volley.ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    private void buildTextPart(ByteArrayOutputStream bos, String parameterName, String parameterValue) throws IOException {
        bos.write(("--" + BOUNDARY + "\r\n").getBytes(StandardCharsets.UTF_8));
        bos.write(("Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n").getBytes(StandardCharsets.UTF_8));
        bos.write(("Content-Type: text/plain; charset=" + PROTOCOL_CHARSET + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        bos.write((parameterValue + "\r\n").getBytes(StandardCharsets.UTF_8));
    }

    private void buildFilePart(ByteArrayOutputStream bos, String parameterName, DataPart dataFile) throws IOException {
        bos.write(("--" + BOUNDARY + "\r\n").getBytes(StandardCharsets.UTF_8));
        bos.write(("Content-Disposition: form-data; name=\"" + parameterName + "\"; filename=\"" + dataFile.getFileName() + "\"\r\n").getBytes(StandardCharsets.UTF_8));
        bos.write(("Content-Type: " + dataFile.getType() + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        bos.write(dataFile.getContent());
        bos.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String fileName, byte[] content) {
            this(fileName, content, "application/octet-stream");
        }

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}



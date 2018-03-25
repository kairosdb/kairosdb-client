package org.kairosdb.client;

import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.builder.QueryTagBuilder;
import org.kairosdb.client.builder.RollupBuilder;
import org.kairosdb.client.builder.RollupTask;
import org.kairosdb.client.response.ErrorResponse;
import org.kairosdb.client.response.GetResponse;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.QueryTagResponse;
import org.kairosdb.client.response.Response;
import org.kairosdb.client.response.RollupResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Base code used to send metrics to Kairos or query Kairos.
 */
public abstract class AbstractClient implements Client
{
    private String url;
    private JsonMapper mapper;
    private DataPointTypeRegistry typeRegistry;

    /**
     * Creates a client
     *
     * @param url url to the KairosDB server
     * @throws MalformedURLException if url is malformed
     */
    AbstractClient(String url)
            throws MalformedURLException
    {
        this.url = checkNotNullOrEmpty(url);
        new URL(url); // validate url

        typeRegistry = new DataPointTypeRegistry();
        mapper = new JsonMapper(typeRegistry);
    }

    @Override
    public GetResponse getMetricNames()
            throws IOException
    {
        return get(url + "/api/v1/metricnames");
    }

    @Override
    public GetResponse getTagNames()
            throws IOException
    {
        return get(url + "/api/v1/tagnames");
    }

    @Override
    public GetResponse getTagValues()
            throws IOException
    {
        return get(url + "/api/v1/tagvalues");
    }

    @Override
    public GetResponse getStatus()
            throws IOException
    {
        return get(url + "/api/v1/health/status");
    }

    @Override
    public QueryTagResponse queryTag(QueryTagBuilder builder)
            throws IOException
    {
        ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/datapoints/query/tags");
        int responseCode = clientResponse.getStatusCode();

        InputStream stream = clientResponse.getContentStream();
        return new QueryTagResponse(mapper, responseCode, stream);
    }

    @Override
    public QueryResponse query(QueryBuilder builder)
            throws IOException
    {
        ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/datapoints/query");
        int responseCode = clientResponse.getStatusCode();

        InputStream stream = clientResponse.getContentStream();
        return new QueryResponse(mapper, responseCode, stream);
    }

    @Override
    public RollupResponse createRollup(RollupBuilder builder)
            throws IOException
    {
        ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/rollups");
        int responseCode = clientResponse.getStatusCode();

        InputStream stream = clientResponse.getContentStream();
        RollupTaskResponse response = mapper.fromJson(Response.getBody(stream), RollupTaskResponse.class);
        response.setStatusCode(responseCode);

        if (response.getStatusCode() == 200 && !Strings.isNullOrEmpty(response.getUrl())) {
            ClientResponse rollupResponse = queryData(url + response.getUrl());
            responseCode = rollupResponse.getStatusCode();

            try (InputStream contentStream = rollupResponse.getContentStream()) {
                RollupTask rollup = mapper.fromJson(Response.getBody(contentStream), RollupTask.class);
                return new RollupResponse(rollup, responseCode);
            }
        }
        else {
            try (InputStreamReader reader = new InputStreamReader(stream)) {
                ErrorResponse errorResponse = mapper.fromJson(reader, ErrorResponse.class);
                return new RollupResponse(responseCode, errorResponse.getErrors());
            }
        }
    }

    public RollupResponse getRollupTask(String id)
            throws IOException
    {
        ClientResponse clientResponse = queryData(url + "/api/v1/rollups/" + id);
        int responseCode = clientResponse.getStatusCode();
        InputStream stream = clientResponse.getContentStream();

        if (responseCode == 200) {
            String body = Response.getBody(stream);
            RollupTask task = mapper.fromJson(body, RollupTask.class);
            return new RollupResponse(task, responseCode);
        }
        else {
            try (InputStreamReader reader = new InputStreamReader(stream)) {
                ErrorResponse errorResponse = mapper.fromJson(reader, ErrorResponse.class);
                return new RollupResponse(responseCode, errorResponse.getErrors());
            }
        }
    }

    @Override
    public RollupResponse getRollupTasks()
            throws IOException
    {
        ClientResponse clientResponse = queryData(url + "/api/v1/rollups");
        int responseCode = clientResponse.getStatusCode();
        InputStream stream = clientResponse.getContentStream();

        if (responseCode == 200) {
            String body = Response.getBody(stream);
            Type listType = new TypeToken<ArrayList<RollupTask>>(){}.getType();
            List<RollupTask> tasks = mapper.fromJson(body, listType);
            return new RollupResponse(tasks, responseCode);
        }
        else {
            try (InputStreamReader reader = new InputStreamReader(stream)) {
                ErrorResponse errorResponse = mapper.fromJson(reader, ErrorResponse.class);
                return new RollupResponse(responseCode, errorResponse.getErrors());
            }
        }
    }

    @Override
    public Response deleteRollup(String id)
            throws IOException
    {
        checkNotNullOrEmpty(id);

        ClientResponse response = delete(url + "/api/v1/rollups/" + id);
        return getResponse(response);
    }

    @Override
    public Response pushMetrics(MetricBuilder builder)
            throws IOException
    {
        checkNotNull(builder);
        if (builder.isCompressionEnabled()) {
            ClientResponse clientResponse = postCompressedData(builder.build(), url + "/api/v1/datapoints");
            return getResponse(clientResponse);
        }
        else {
            ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/datapoints");
            return getResponse(clientResponse);
        }
    }

    @Override
    public Response deleteMetric(String name)
            throws IOException
    {
        checkNotNullOrEmpty(name);

        ClientResponse response = delete(url + "/api/v1/metric/" + name);
        return getResponse(response);
    }

    @Override
    public Response delete(QueryBuilder builder)
            throws URISyntaxException, IOException
    {
        checkNotNull(builder);
        ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/datapoints/delete");

        return getResponse(clientResponse);
    }

    @Override
    public void registerCustomDataType(String groupType, Class dataPointClass)
    {
        typeRegistry.registerCustomDataType(groupType, dataPointClass);
    }

    @Override
    public Class getDataPointValueClass(String groupType)
    {
        return typeRegistry.getDataPointValueClass(groupType);
    }

    private Response getResponse(ClientResponse clientResponse)
            throws IOException
    {
        Response response = new Response(clientResponse.getStatusCode());
        InputStream stream = clientResponse.getContentStream();
        if (stream != null) {
            try (InputStreamReader reader = new InputStreamReader(stream)) {
                ErrorResponse errorResponse = mapper.fromJson(reader, ErrorResponse.class);
                response.addErrors(errorResponse.getErrors());
            }
        }
        return response;
    }

    private GetResponse get(String url)
            throws IOException
    {
        ClientResponse clientResponse = queryData(url);
        int responseCode = clientResponse.getStatusCode();

        if (responseCode == 200) {
            if (url.contains("health/status")) {
                return new GetResponse(responseCode);
            }
        }

        if (responseCode >= 400) {
            return new GetResponse(responseCode);
        }
        else {
            InputStream stream = clientResponse.getContentStream();
            if (stream == null) {
                throw new IOException("Could not get content stream.");
            }

            return new GetResponse(responseCode, readNameQueryResponse(stream));
        }
    }

    private List<String> readNameQueryResponse(InputStream stream)
            throws IOException
    {
        List<String> list = new ArrayList<>();

        try (JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"))) {
            reader.beginObject();
            String container = reader.nextName();
            if (container.equals("results")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    list.add(reader.nextString());
                }
                reader.endArray();
                reader.endObject();
            }
        }

        return list;
    }

    protected abstract ClientResponse postData(String json, String url)
            throws IOException;

    protected abstract ClientResponse postCompressedData(String json, String url)
            throws IOException;

    protected abstract ClientResponse queryData(String url)
            throws IOException;

    protected abstract ClientResponse delete(String url)
            throws IOException;


    @SuppressWarnings("unused")
    private class RollupTaskResponse extends Response
    {
        private String id;
        private String name;
        private RollupAttributes attributes;

        String getUrl()
        {
            return attributes.url;
        }
    }

    private class RollupAttributes
    {
        private final String url;

        public RollupAttributes(String url)
        {
            this.url = checkNotNull(url, "url cannot be null");
        }
    }
}

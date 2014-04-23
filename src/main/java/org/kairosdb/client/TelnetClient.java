package org.kairosdb.client;

import org.kairosdb.client.builder.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Communicates with KairosDB using the Telnet protocol. Only pushing of metrics is supported. Querying must be done
 * using the HTTP client.
 *
 * The socket is opened in the constructor and left open until the close() method is called. Note that no response is
 * returned. This allows data to flow more quickly. If you need to guarantee arrival of metrics then use the HTTP
 * client.
 */
public class TelnetClient
{
	private Socket socket;
	private PrintWriter writer;

	public TelnetClient(String host, int port) throws IOException
	{
		socket = new Socket(host, port);
		writer = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
	}

	/**
	 * Sends metrics from the builder to the Kairos server.
	 *
	 * @param builder metrics builder
	 */
	public void pushMetrics(MetricBuilder builder)
	{
		List<Metric> metrics = builder.getMetrics();
		for (Metric metric : metrics)
		{
			StringBuilder tags = new StringBuilder();
			for (Map.Entry<String, String> tag : metric.getTags().entrySet())
			{
				tags.append(tag.getKey()).append("=").append(tag.getValue());
			}

			for (DataPoint dataPoint : metric.getDataPoints())
			{
				writer.println("put" + " " + metric.getName() + " " + dataPoint.getTimestamp() + " " +
						(dataPoint.isInteger() ? ((LongDataPoint) dataPoint).getValue() : ((DoubleDataPoint) dataPoint).getValue()) +
						" " + tags.toString());
			}
		}
		writer.flush();
	}

	/**
	 * Closes the socket.
	 *
	 * @throws IOException if the socket could not be closed.
	 */
	public void shutdown() throws IOException
	{
		socket.close();
	}
}

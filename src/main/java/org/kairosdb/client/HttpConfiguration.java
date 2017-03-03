package org.kairosdb.client;

public class HttpConfiguration
{
	private int connectionTimeout;
	private int socketTimeout;

	/**
	 * The time to establish the connection with the remote host in seconds.
	 * @return timeout value
	 */
	public int getConnectionTimeout()
	{
		return connectionTimeout;
	}

	/**
	 * The time to establish the connection with the remote host in seconds.
	 * @param connectionTimeout timeout value
	 */
	public void setConnectionTimeout(int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * The time waiting for data – after the connection is established;
	 * maximum time of inactivity between two data packets.
	 * @return timeout value
	 */
	public int getSocketTimeout()
	{
		return socketTimeout;
	}

	/**
	 * The time waiting for data – after the connection is established;
	 * maximum time of inactivity between two data packets.
	 *
	 * @param socketTimeout timeout value
	 */
	public void setSocketTimeout(int socketTimeout)
	{
		this.socketTimeout = socketTimeout;
	}
}

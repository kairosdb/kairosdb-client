package org.kairosdb.client;

import com.proofpoint.http.client.UnexpectedResponseException;
import org.kairosdb.client.builder.*;
import org.kairosdb.client.response.JsonResponseHandler;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.QueryTagResponse;

import java.io.Closeable;
import java.util.List;

public interface Client extends Closeable
{
	/**
	 * Create a new roll-up.
	 * @param builder roll-up builder
	 * @return newly created roll-up task
	 * @throws UnexpectedResponseException if the operation fails
	 */
	RollupTask createRollupTask(RollupBuilder builder);

	/**
	 * Delete the roll-up. Throws an exception if the delete fails.
	 * @param id identifier of the roll-up
	 * @throws UnexpectedResponseException if the operation fails
	 */
	void deleteRollupTask(String id);

	/**
	 * Returns a list of all roll-up tasks.
	 * @return list of roll-up tasks
	 * @throws UnexpectedResponseException if the operation fails
	 */
	List<RollupTask> getRollupTasks();

	/**
	 * Returns the roll-up.
	 * @param id roll-up identifier
	 * @return roll-up or null if no roll-ups match the specified identifier
	 * @throws UnexpectedResponseException if the operation fails
	 */
	RollupTask getRollupTask(String id);

	/**
	 * Returns a list of all metric names.
	 *
	 * @return list of all metric names
	 * @throws UnexpectedResponseException if the operation fails
	 */
	Object getMetricNames();

	/**
	 * Returns status of Kairos Instance.
	 *
	 * @return status of Kairos instance
	 * @throws UnexpectedResponseException if the operation fails
	 */
	List<String> getStatus();

	/**
	 * Returns a status code fo 204 if all is healthy.
	 *
	 * @return status of Kairos instance
	 * @throws UnexpectedResponseException if the operation fails
	 */
	int getStatusCheck();

	/**
	 * Queries KairosDB using the query built by the builder.
	 *
	 * @param builder query builder
	 * @param handler response handler
	 * @return query response
	 * @throws UnexpectedResponseException if the operation fails
	 */
	<T> T query(QueryBuilder builder, JsonResponseHandler<T> handler);

	/**
	 * Queries KairosDB using the query built by the builder.
	 *
	 * @param builder query builder
	 * @return query response
	 * @throws UnexpectedResponseException if the operation fails
	 */
	QueryResponse query(QueryBuilder builder);

	/**
	 * Queries KairosDB tags using the query built by the builder.
	 *
	 * @param builder query tag builder
	 * @return query response
	 * @throws UnexpectedResponseException if the operation fails
	 */
	QueryTagResponse queryTags(QueryTagBuilder builder);

	/**
	 * Queries KairosDB tags using the query built by the builder.
	 *
	 * @param builder query tag builder
	 * @return query response
	 * @throws UnexpectedResponseException if the operation fails
	 */
	<T> T queryTags(QueryTagBuilder builder, JsonResponseHandler<T> handler);

	/**
	 * Sends metrics from the builder to the KairosDB server.
	 *
	 * @param builder metrics builder
	 * @throws UnexpectedResponseException if the operation fails
	 */
	void pushMetrics(MetricBuilder builder);

	/**
	 * Deletes a metric. This is the metric and all its data points.
	 * An exception is thrown if this operation fails.
	 *
	 * @param name the metric to delete
	 * @throws UnexpectedResponseException if the operation fails
	 */
	void deleteMetric(String name);

	/**
	 * Deletes data in KairosDB using the query built by the builder.
	 * An exception is thrown if this operation fails.
	 *
	 * @param builder query builder
	 * @throws UnexpectedResponseException if the operation fails
	 */
	void delete(QueryBuilder builder);

	/**
	 * Returns the version string for the KairosDB server.
	 * @return KairosDB version
	 */
	String getVersion();

	/**
	 * Registers a new custom data type. The assumption is that this data type already exists on the server. The
	 * dataPointValueClass is used to serialize and deserialize the custom type. This is simply a POJO.
	 *
	 * @param groupType           type used to deserialize the json on the client
	 * @param dataPointValueClass class that is the value of a data point
	 */
	void registerCustomDataType(String groupType, Class dataPointValueClass);

	/**
	 * Returns the data point value class for the given group type or null if one is not registered for the group type
	 *
	 * @param groupType group type
	 * @return data point class associated with the group type
	 */
	Class getDataPointValueClass(String groupType);
}
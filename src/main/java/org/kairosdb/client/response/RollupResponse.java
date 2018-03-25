package org.kairosdb.client.response;

import com.google.common.collect.ImmutableList;
import org.kairosdb.client.builder.Rollup;
import org.kairosdb.client.builder.RollupTask;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class RollupResponse extends Response
{
    private List<RollupTask> rollupTasks;

    public RollupResponse(RollupTask rollupTask, int responseCode)
    {
        super(responseCode);
        checkNotNull(rollupTask, "rollupTask cannot be null");
        rollupTasks =  new ArrayList<>();
        rollupTasks.add(rollupTask);
    }

    public RollupResponse(List<RollupTask> rollupTasks, int responseCode)
    {
        super(responseCode);
        checkNotNull(rollupTasks, "rollupTasks cannot be null");
        this.rollupTasks = new ArrayList<>(rollupTasks);
    }

    public RollupResponse(int responseCode, List<String> errors)
    {
        super(responseCode);
        addErrors(errors);
    }

    public ImmutableList<RollupTask> getRollupTasks()
    {
        return ImmutableList.copyOf(rollupTasks);
    }
}

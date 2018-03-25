package org.kairosdb.client.builder;

import static com.google.common.base.Preconditions.checkState;

public class BuilderUtils
{
    private BuilderUtils()
    {
    }

    public static void validateTimes(Long startAbsolute, Long endAbsolute, RelativeTime startRelative, RelativeTime endRelative)
    {
        checkState(startAbsolute != null || startRelative != null, "Start time must be specified");

        if (endAbsolute != null)
        {
            if (startAbsolute != null)
                TimeValidator.validateEndTimeLaterThanStartTime(startAbsolute, endAbsolute);
            else
                TimeValidator.validateEndTimeLaterThanStartTime(startRelative, endAbsolute);
        }
        else if (endRelative != null)
        {
            if (startAbsolute != null)
                TimeValidator.validateEndTimeLaterThanStartTime(startAbsolute, endRelative);
            else
                TimeValidator.validateEndTimeLaterThanStartTime(startRelative, endRelative);
        }
    }
}

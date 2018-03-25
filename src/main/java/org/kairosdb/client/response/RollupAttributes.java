package org.kairosdb.client.response;

import static com.google.common.base.Preconditions.checkNotNull;

public class RollupAttributes
{
    private final String url;

    public RollupAttributes(String url)
    {
        this.url = checkNotNull(url, "url cannot be null");
    }

    public String getUrl()
    {
        return url;
    }
}

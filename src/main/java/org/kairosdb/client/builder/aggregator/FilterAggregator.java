package org.kairosdb.client.builder.aggregator;

import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.Aggregator;

import java.util.Objects;

public class FilterAggregator extends Aggregator {

    @SerializedName("filter_op")
    private String op;
    @SerializedName("threshold")
    private String threshold;

    private static final transient String FILTER = "filter";

    public FilterAggregator(Operation operation, double threshold) {
        super(FILTER);
        this.op = operation.getText();
        this.threshold = Objects.toString(threshold);
    }

    public enum Operation
    {
        LTE("lte"), LT("lt"), GTE("gte"), GT("gt"), EQUAL("eq");

        private String text;

        Operation(String text) {this.text = text;}

        private String getText() {
            return text;
        }
    }

    public String getOperation() {
        return op;
    }

    public String getThreshold() {
        return threshold;
    }
}

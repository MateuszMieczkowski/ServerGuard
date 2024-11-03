package com.mmieczkowski.serverguard.alert.model.groupby;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embeddable;

@Embeddable
@DiscriminatorColumn(name = "group_by")
public class GroupBy {

    public static GroupBy create(String aggregateFunction) {
        return switch (aggregateFunction) {
            case "avg" -> new GroupByAvg();
            case "min" -> new GroupByMin();
            case "max" -> new GroupByMax();
            default -> throw new IllegalArgumentException("Invalid group by: " + aggregateFunction);
        };
    }

    public String getGroupBy(){
        return "";
    }
}

package com.mmieczkowski.serverguard.alert.model.groupby;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;

@Embeddable
@DiscriminatorValue("avg")
public class GroupByAvg extends GroupBy {
    @Override
    public String getGroupBy() {
        return "avg";
    }
}

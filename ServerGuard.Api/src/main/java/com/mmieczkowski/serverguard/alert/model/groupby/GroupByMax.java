package com.mmieczkowski.serverguard.alert.model.groupby;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;

@Embeddable
@DiscriminatorValue("max")
public class GroupByMax extends GroupBy {
    @Override
    public String getGroupBy() {
        return "max";
    }
}

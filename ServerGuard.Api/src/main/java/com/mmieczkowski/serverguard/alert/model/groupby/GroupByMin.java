package com.mmieczkowski.serverguard.alert.model.groupby;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;

@Embeddable
@DiscriminatorValue("min")
public class GroupByMin extends GroupBy {
    @Override
    public String getGroupBy() {
        return "min";
    }
}

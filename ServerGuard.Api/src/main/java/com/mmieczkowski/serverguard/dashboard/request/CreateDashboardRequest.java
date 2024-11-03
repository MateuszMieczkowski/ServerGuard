package com.mmieczkowski.serverguard.dashboard.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateDashboardRequest(@Size(min=3, max = 100) String name, @Valid @NotEmpty  List<CreateGraphRequest> graphs) {
}

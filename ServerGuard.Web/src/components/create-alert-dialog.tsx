import React, { useEffect, useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  Button,
  DialogActions,
  Divider,
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  CircularProgress,
  Alert,
  Snackbar,
} from "@mui/material";
import {
  getAgentAvailableMetrics,
  GetAgentAvailableMetricsResponse,
} from "../api/agents-service";
import { createAlert, CreateAlertRequest } from "../api/alerts-service";

interface CreateAlertDialogProps {
  resourceGroupId: string;
  agentId: string;
  open: boolean;
  setOpen: (open: boolean) => void;
  onCreate: () => void;
}

export const CreateAlertDialog: React.FC<CreateAlertDialogProps> = ({
  resourceGroupId,
  agentId,
  open,
  setOpen,
  onCreate,
}) => {
  const handleClose = async () => {
    setOpen(false);
  };
  const [availableMetrics, setAvailableMetrics] =
    React.useState<GetAgentAvailableMetricsResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const showErrorSnackbar = (message: string) => {
    setSnackbarMessage(message);
    setSnackbarOpen(true);
  };
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [newAlert, setNewAlert] = useState<CreateAlertRequest>({
    name: "",
    metric: {
      sensorName: "",
      metricName: "",
      type: "",
    },
    operator: "",
    groupBy: "",
    threshold: 0,
    duration: "",
  });

  const [metricUnit, setMetricUnit] = useState<string>("");

  useEffect(() => {
    setLoading(true);
    getAgentAvailableMetrics(resourceGroupId, agentId)
      .then((response) => {
        setAvailableMetrics(response);
      })
      .catch(() => {
        setLoading(false);
      });
    setLoading(false);
  }, []);

  const handleCreateDialog = async () => {
    setIsSubmitting(true);
    if (!newAlert.name) {
      showErrorSnackbar("Alert name is required");
      setIsSubmitting(false);
      return;
    }
    if (!newAlert.metric.sensorName) {
      showErrorSnackbar("Sensor is required");
      setIsSubmitting(false);
      return;
    }
    if (!newAlert.metric.metricName) {
      showErrorSnackbar("Metric is required");
      setIsSubmitting(false);
      return;
    }
    if (!newAlert.metric.type) {
      showErrorSnackbar("Metric type is required");
      setIsSubmitting(false);
      return;
    }
    if (!newAlert.operator) {
      showErrorSnackbar("Operator is required");
      setIsSubmitting(false);
      return;
    }
    if (!newAlert.threshold) {
      showErrorSnackbar("Threshold is required");
      setIsSubmitting(false);
      return;
    }
    if (!newAlert.duration) {
      showErrorSnackbar("Duration is required");
      setIsSubmitting(false);
      return;
    }
    if (!newAlert.groupBy) {
      showErrorSnackbar("Group by is required");
      setIsSubmitting(false);
      return;
    }
    createAlert(resourceGroupId, agentId, newAlert)
      .then(() => {
        setNewAlert({
          name: "",
          metric: {
            sensorName: "",
            metricName: "",
            type: "",
          },
          operator: "",
          groupBy: "",
          threshold: 0,
          duration: "",
        });
        onCreate();
        handleClose();
        setIsSubmitting(false);
      })
      .catch((error) => {
        showErrorSnackbar(`Failed to create alert: ${error.message}`);
        setIsSubmitting(false);
      });
  };

  const durations = [
    { value: "PT1M", label: "1 minute" },
    { value: "PT5M", label: "5 minutes" },
    { value: "PT15M", label: "15 minutes" },
    { value: "PT30M", label: "30 minutes" },
    { value: "PT1H", label: "1 hour" },
    { value: "PT6H", label: "6 hours" },
    { value: "PT12H", label: "12 hours" },
  ];

  return (
    <>
      <Dialog
        open={open}
        onClose={handleClose}
        PaperProps={{
          component: "form",
        }}
      >
        <DialogTitle width={200}>New Alert</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            fullWidth
            margin="dense"
            variant="outlined"
            id="alertName"
            name="name"
            label="Alert name"
            type="text"
            value={newAlert?.name ?? ""}
            onChange={(e) => {
              setNewAlert((prev) => {
                return { ...prev, name: e.target.value };
              });
            }}
          />
          <Divider sx={{ my: 2 }} />
          {loading && <CircularProgress />}
          {!loading && (
            <Box>
              <FormControl sx={{ width: 250, mt: 2 }}>
                <InputLabel id="sensor-select-label">Sensor</InputLabel>
                <Select
                  labelId="sensor-select-label"
                  id="sensor-select"
                  value={newAlert?.metric.sensorName ?? ""}
                  label="Sensor"
                  onChange={(e) => {
                    setNewAlert((prev) => {
                      return {
                        ...prev,
                        metric: { ...prev.metric, sensorName: e.target.value },
                      };
                    });
                  }}
                >
                  {availableMetrics?.sensors.map((sensor) => (
                    <MenuItem value={sensor.name}>{sensor.name}</MenuItem>
                  ))}
                </Select>
              </FormControl>
              <FormControl sx={{ width: 250, mt: 2 }}>
                <InputLabel id="metric-select-label">Metric</InputLabel>
                <Select
                  labelId="metric-select-label"
                  id="metric-select"
                  value={newAlert?.metric.metricName ?? ""}
                  label="Metric"
                  onChange={(e) => {
                    setNewAlert((prev) => {
                      return {
                        ...prev,
                        metric: { ...prev.metric, metricName: e.target.value },
                      };
                    });
                  }}
                >
                  {availableMetrics?.sensors
                    .find((x) => x.name === newAlert.metric.sensorName)
                    ?.metrics.map((metric) => (
                      <MenuItem value={metric.name}>{metric.name}</MenuItem>
                    ))}
                </Select>
              </FormControl>
              <FormControl sx={{ width: 250, mt: 2 }}>
                <InputLabel id="metricType-select-label">
                  Metric Type
                </InputLabel>
                <Select
                  labelId="metricType-select-label"
                  id="metricType-select"
                  value={newAlert?.metric.type ?? ""}
                  label="Metric Type"
                  onChange={(e) => {
                    const metric = availableMetrics?.sensors
                      .find(
                        (sensor) => sensor.name === newAlert.metric.sensorName
                      )
                      ?.metrics.find(
                        (metric) => metric.name == newAlert.metric.metricName
                      )
                      ?.types.find((type) => type.name === e.target.value);
                    if (metric) {
                      setMetricUnit(metric.unit);
                    }
                    setNewAlert((prev) => {
                      return {
                        ...prev,
                        metric: { ...prev.metric, type: e.target.value },
                      };
                    });
                  }}
                >
                  {availableMetrics?.sensors
                    .find(
                      (sensor) => sensor.name === newAlert.metric.sensorName
                    )
                    ?.metrics.find(
                      (metric) => metric.name == newAlert.metric.metricName
                    )
                    ?.types?.map((metricType) => (
                      <MenuItem value={metricType.name}>
                        {metricType.name}
                      </MenuItem>
                    ))}
                </Select>
              </FormControl>
              <TextField
                autoFocus
                fullWidth
                margin="dense"
                variant="outlined"
                id="threshold"
                name="threshold"
                label={"Threshold " + metricUnit}
                type="number"
                value={newAlert?.threshold ?? 0}
                onChange={(e) => {
                  setNewAlert((prev) => {
                    return { ...prev, threshold: parseFloat(e.target.value) };
                  });
                }}
              />
              <FormControl sx={{ width: 250, mt: 2 }}>
                <InputLabel id="operator-select-label">Operator</InputLabel>
                <Select
                  labelId="operator-select-label"
                  id="operator-select"
                  value={newAlert?.operator ?? ""}
                  label="Operator"
                  onChange={(e) => {
                    setNewAlert((prev) => {
                      return { ...prev, operator: e.target.value };
                    });
                  }}
                >
                  <MenuItem value={">"}>{">"}</MenuItem>
                  <MenuItem value={"<"}>{"<"}</MenuItem>
                  <MenuItem value={">="}>{">="}</MenuItem>
                  <MenuItem value={"<="}>{"<="}</MenuItem>
                </Select>
              </FormControl>
              <FormControl sx={{ width: 250, mt: 2 }}>
                <InputLabel id="duration-select-label">Duration</InputLabel>
                <Select
                  labelId="duration-select-label"
                  id="duration-select"
                  value={
                    durations.find((x) => x.value === newAlert?.duration)
                      ?.value ?? ""
                  }
                  label="Duration"
                  onChange={(e) => {
                    setNewAlert((prev) => {
                      console.log(e.target.value);
                      return {
                        ...prev,
                        duration: e.target.value,
                      };
                    });
                  }}
                >
                  {durations.map((duration) => (
                    <MenuItem value={duration.value}>{duration.label}</MenuItem>
                  ))}
                </Select>
              </FormControl>
              <FormControl sx={{ width: 250, mt: 2 }}>
                <InputLabel id="groupby-select-label">Group by</InputLabel>
                <Select
                  labelId="groupby-select-label"
                  id="groupby-select"
                  value={newAlert?.groupBy ?? ""}
                  label="Group By"
                  onChange={(e) => {
                    setNewAlert((prev) => {
                      return { ...prev, groupBy: e.target.value };
                    });
                  }}
                >
                  <MenuItem value={"AVG"}>{"AVG"}</MenuItem>
                  <MenuItem value={"MIN"}>{"MIN"}</MenuItem>
                  <MenuItem value={"MAX"}>{"MAX"}</MenuItem>
                </Select>
              </FormControl>
            </Box>
          )}
          <DialogActions>
            <Button color="primary" onClick={handleClose}>
              Cancel
            </Button>
            <Button
              color="primary"
              onClick={handleCreateDialog}
              disabled={isSubmitting}
            >
              Create
            </Button>
          </DialogActions>
        </DialogContent>
      </Dialog>
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      >
        <Alert onClose={() => setSnackbarOpen(false)} severity="error">
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </>
  );
};

import React, { useEffect, useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  Button,
  DialogActions,
  Divider,
  Typography,
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  IconButton,
  CircularProgress,
  Alert,
  Snackbar,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import { createDashboard, Graph } from "../api/dashboards-service";
import {
  getAgentAvailableMetrics,
  GetAgentAvailableMetricsResponse,
} from "../api/agents-service";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import DeleteIcon from "@mui/icons-material/Delete";

interface CreateDashboardDialogProps {
  resourceGroupId: string;
  agentId: string;
  open: boolean;
  setOpen: (open: boolean) => void;
  onCreate: () => void;
}

const colors = [
  { name: "Red", value: "#003f5c" },
  { name: "Green", value: "#444e86" },
  { name: "Blue", value: "#955196" },
  { name: "Yellow", value: "#dd5182" },
  { name: "Magenta", value: "#ff6e54" },
  { name: "Cyan", value: "#ffa600" },
];

export const CreateDashboardDialog: React.FC<CreateDashboardDialogProps> = ({
  resourceGroupId,
  agentId,
  open,
  setOpen,
  onCreate,
}) => {
  const handleClose = async () => {
    setDashboardName("");
    setGraphs([]);
    setOpen(false);
  };
  const [dashboardName, setDashboardName] = useState<string>("");
  const [graphs, setGraphs] = useState<Graph[]>([]);
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

  const handleAddGraphClick = () => {
    setGraphs([
      ...graphs,
      {
        sensorName: "",
        metricName: "",
        metricType: "",
        index: graphs.length,
        lineColor: "",
        unit: "",
      },
    ]);
  };

  const handleCreateDashboard = async () => {
    if (!dashboardName) {
      showErrorSnackbar("Dashboard name is required.");
      return;
    }
    if (dashboardName.length < 5 || dashboardName.length > 50) {
      showErrorSnackbar("Dashboard name must be between 5 and 50 characters.");
      return;
    }
    if (graphs.length === 0) {
      showErrorSnackbar("At least one graph is required.");
      return;
    }
    if (
      graphs.some(
        (x) => !x.sensorName || !x.metricName || !x.metricType || !x.lineColor
      )
    ) {
      showErrorSnackbar(
        "All graphs must have a sensor name, metric name, and metric type."
      );
      return;
    }
    setIsSubmitting(true);
    try {
      await createDashboard(resourceGroupId, agentId, {
        name: dashboardName,
        graphs,
      });
      onCreate();
      setIsSubmitting(false);
      handleClose();
    } catch (error) {
      showErrorSnackbar("Failed to create dashboard.");
      setIsSubmitting(false);
      return;
    }
    onCreate();
    handleClose();
    setIsSubmitting(false);
  };

  const moveGraph = (index: number, direction: "up" | "down") => {
    console.log(index, direction);
    if (
      (index === 0 && direction === "up") ||
      (index === graphs.length - 1 && direction === "down")
    )
      return;
    const newIndex = direction === "up" ? index - 1 : index + 1;
    const newGraphs = graphs.map((graph) => {
      if (graph.index === index) {
        return { ...graph, index: newIndex };
      }
      if (graph.index === newIndex) {
        return { ...graph, index: index };
      }
      return graph;
    });
    newGraphs.sort((a, b) => a.index - b.index);
    setGraphs(newGraphs);
  };

  const handleDeleteGraph = (index: number) => {
    const newGraphs = graphs.filter((graph) => graph.index !== index);
    newGraphs.forEach((graph, i) => {
      graph.index = i;
    });
    setGraphs(newGraphs);
  };

  return (
    <>
      <Dialog
        open={open}
        onClose={handleClose}
        PaperProps={{
          component: "form",
        }}
      >
        <DialogTitle width={200}>New Dashboard</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            fullWidth
            margin="dense"
            variant="outlined"
            id="dashboardName"
            name="name"
            label="Dashoard name"
            type="text"
            onChange={(e) => setDashboardName(e.target.value)}
          />
          <Divider sx={{ my: 2 }} />
          <Box sx={{ display: "flex" }}>
            <Typography variant="h6">Graphs</Typography>
            <IconButton onClick={handleAddGraphClick}>
              <AddIcon />
            </IconButton>
          </Box>
          {loading && <CircularProgress />}
          {!loading &&
            graphs.map((graph) => {
              return (
                <Box>
                  <FormControl sx={{ width: 250, mt: 2 }}>
                    <InputLabel id="sensor-select-label">Sensor</InputLabel>
                    <Select
                      labelId="sensor-select-label"
                      id="sensor-select"
                      value={graph.sensorName ?? ""}
                      label="Sensor"
                      onChange={(e) => {
                        setGraphs((prev) => {
                          return prev.map((g) => {
                            if (g.index === graph.index) {
                              return { ...g, sensorName: e.target.value };
                            }
                            return g;
                          });
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
                      value={graph.metricName ?? ""}
                      label="Metric"
                      onChange={(e) => {
                        setGraphs((prev) => {
                          return prev.map((g) => {
                            if (g.index === graph.index) {
                              return { ...g, metricName: e.target.value };
                            }
                            return g;
                          });
                        });
                      }}
                    >
                      {availableMetrics?.sensors
                        .find((x) => x.name === graph.sensorName)
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
                      value={graph.metricType ?? ""}
                      label="Metric Type"
                      onChange={(e) => {
                        setGraphs((prev) => {
                          return prev.map((g) => {
                            if (g.index === graph.index) {
                              return { ...g, metricType: e.target.value };
                            }
                            return g;
                          });
                        });
                      }}
                    >
                      {availableMetrics?.sensors
                        .find((sensor) => sensor.name === graph.sensorName)
                        ?.metrics.find(
                          (metric) => metric.name == graph.metricName
                        )
                        ?.types?.map((metricType) => (
                          <MenuItem value={metricType.name}>
                            {metricType.name}
                          </MenuItem>
                        ))}
                    </Select>
                  </FormControl>
                  <FormControl sx={{ width: 250, mt: 2 }}>
                    <InputLabel id="color-select-label">Line Color</InputLabel>
                    <Select
                      labelId="color-select-label"
                      id="color-select"
                      label="Line Color"
                      value={graph.lineColor}
                      onChange={(e) => {
                        const newColor = e.target.value;
                        setGraphs((prev) =>
                          prev.map((g) =>
                            g.index === graph.index
                              ? { ...g, lineColor: newColor }
                              : g
                          )
                        );
                      }}
                    >
                      {colors.map((color) => (
                        <MenuItem key={color.value} value={color.value}>
                          <Box
                            sx={{
                              width: 180,
                              height: 20,
                              backgroundColor: color.value,
                              display: "inline-block",
                              marginRight: 1,
                            }}
                          />
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                  <Box
                    sx={{
                      mt: 2,
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  >
                    <IconButton
                      sx={{ mt: 3, ml: 2 }}
                      onClick={() => moveGraph(graph.index, "up")}
                    >
                      <ArrowUpwardIcon />
                    </IconButton>
                    <IconButton
                      sx={{ mt: 3, ml: 1 }}
                      onClick={() => moveGraph(graph.index, "down")}
                    >
                      <ArrowDownwardIcon />
                    </IconButton>
                    <IconButton
                      sx={{ mt: 3, ml: 1 }}
                      onClick={() => handleDeleteGraph(graph.index)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </Box>
                  <Divider sx={{ my: 2 }} />
                </Box>
              );
            })}

          <DialogActions>
            <Button color="primary" onClick={handleClose}>
              Cancel
            </Button>
            <Button
              color="primary"
              onClick={handleCreateDashboard}
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

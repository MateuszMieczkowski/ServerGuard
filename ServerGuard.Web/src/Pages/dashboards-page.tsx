import { useParams } from "react-router-dom";
import {
  Container,
  Typography,
  Stack,
  Button,
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  CircularProgress,
  IconButton,
  FormControlLabel,
  Switch,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { useEffect, useState } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import {
  Dashboard,
  DashboardListItem,
  DataPoint,
  deleteDashboard,
  getDashboard,
  getDashboards,
  getGraphData,
} from "../api/dashboards-service";
import { DateTimePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import { renderTimeViewClock } from "@mui/x-date-pickers/timeViewRenderers";
import { CreateDashboardDialog } from "../components/create-dashboard-dialog";
import { UpdateDashboardDialog } from "../components/update-dashboard-dialog";
import { deleteConfirmationDialog } from "../components/delete-confirmation-dialog";
import { Client, StompSubscription } from "@stomp/stompjs";
import getConfig from "../config";

const DashboardsPage = () => {
  const config = getConfig();
  const { resourceGroupId, agentId } = useParams();
  const [dashboards, setDashboards] = useState<DashboardListItem[]>([]);
  const [selectedDashboardId, setSelectedDashboardId] = useState<string | null>(
    null
  );
  const [selectedDashboard, setSelectedDashboard] = useState<Dashboard | null>(
    null
  );
  const [loading, setLoading] = useState(true);
  const [graphDataLoading, setGraphDataLoading] = useState<{
    [key: string]: boolean;
  }>({});
  const [graphData, setGraphData] = useState<{ [key: number]: DataPoint[] }>(
    {}
  );
  const [dataFrom, setDataFrom] = useState(
    dayjs().subtract(6, "hours").subtract(dayjs().minute(), "minutes")
  );
  const [dataTo, setDataTo] = useState(
    dayjs().subtract(dayjs().minute(), "minutes")
  );
  const [aggretationType, setAggregationType] = useState("AVG");
  const [openCreateDashboardDialog, setOpenCreateDashboardDialog] =
    useState(false);

  const [openUpdateDashboardDialog, setOpenUpdateDashboardDialog] =
    useState(false);

  const [refreshDashboards, setRefreshDashboards] = useState(false);

  const [deleteConfirmation, setDeleteConfirmation] = useState(false);

  //TODO: SEt to false
  const [liveMode, setLiveMode] = useState(false);
  const [_, setStompSubscriptions] = useState<StompSubscription[]>([]);
  const [liveModeMetrics, setLiveModeMetrics] = useState<WsMetrics[]>([]);

  //TODO: set url from config
  const [stompClient] = useState<Client>(() => {
    var client = new Client({
      brokerURL: config.webSocketUrl,
      connectHeaders: {
        Authorization: localStorage.getItem("authToken") || "",
      },
    });
    client.activate();
    return client;
  });

  interface WsMetrics {
    time: string;
    sensorMetrics: SensorMetric[];
  }

  interface SensorMetric {
    name: string;
    metrics: Metric[];
  }

  interface Metric {
    name: string;
    value: number;
    type: string;
  }

  useEffect(() => {
    if (!liveMode) {
      return;
    }
    if (agentId) {
      const subscripion = stompClient.subscribe(
        `/topic/agents/${agentId}/metrics`,
        (message) => {
          if (liveMode) {
            const wsMetrics: WsMetrics = JSON.parse(message.body);
            setLiveModeMetrics((prevState) => [...prevState, wsMetrics]);
          }
        }
      );
      setStompSubscriptions((prevState) => [...prevState, subscripion]);
    }
  }, [agentId, liveMode]);

  useEffect(() => {
    if (!liveMode) {
      return;
    }
    // if (liveModeMetrics.length > 0) {
    //   selectedDashboard?.graphs.forEach((graph, _) => {
    //     const metrics = liveModeMetrics.filter((wsMetric) => {
    //       wsMetric.sensorMetrics.forEach((sensorMetric) => {
    //         if (
    //           sensorMetric.name === graph.sensorName &&
    //           sensorMetric.metrics.some(
    //             (metric) => metric.name === graph.metricName
    //           )
    //         ) {
    //           return true;
    //         }
    //       });
    //     });
    //   });
    // }
  }, [liveModeMetrics]);

  useEffect(() => {
    setSelectedDashboardId(null);
    setSelectedDashboard(null);
    setGraphData({});
    setLoading(true);
    if (resourceGroupId && agentId) {
      getDashboards(resourceGroupId, agentId).then((response) => {
        setDashboards(response);
      });
    } else {
      setLoading(false);
    }
  }, [resourceGroupId, agentId, refreshDashboards]);

  useEffect(() => {
    setLoading(true);
    if (selectedDashboardId) {
      getDashboard(
        resourceGroupId as string,
        agentId as string,
        selectedDashboardId
      )
        .then((response) => {
          setSelectedDashboard(response);
          setLoading(false);
        })
        .catch(() => {
          setLoading(false);
        });
    } else {
      setLoading(false);
    }
  }, [selectedDashboardId]);

  const handleSelectDashboardChange = (dashboardId: string) => {
    setSelectedDashboardId(dashboardId);
    setGraphData({});
  };

  const loadGraphData = async (graphIndex: number) => {
    setGraphDataLoading((prevState) => ({
      ...prevState,
      [graphIndex]: true,
    }));
    getGraphData(
      resourceGroupId as string,
      agentId as string,
      selectedDashboardId as string,
      dataFrom.toISOString().replace("Z", ""),
      dataTo.toISOString().replace("Z", ""),
      graphIndex,
      200,
      aggretationType
    )
      .then((response) => {
        {
          setGraphData((prevState) => ({
            ...prevState,
            [graphIndex]: response,
          }));
          setGraphDataLoading((prevState) => ({
            ...prevState,
            [graphIndex]: false,
          }));
        }
      })
      .catch(() => {
        setGraphDataLoading((prevState) => ({
          ...prevState,
          [graphIndex]: false,
        }));
      });
  };

  const handleLoadButtonClick = () => {
    if (selectedDashboard) {
      selectedDashboard.graphs.forEach((_, index) => {
        loadGraphData(index);
      });
    }
  };

  const handleDeleteDashboard = () => {
    if (selectedDashboard) {
      deleteDashboard(resourceGroupId!, agentId!, selectedDashboardId!).then(
        () => {
          setSelectedDashboardId(null);
          setSelectedDashboard(null);
          setRefreshDashboards(!refreshDashboards);
        }
      );
    }
  };
  const graphWidth = 1200;
  const graphHeight = 400;

  return (
    <Container sx={{ my: "auto" }}>
      <Stack
        direction="row"
        spacing={2}
        sx={{
          display: "flex",
          justifyContent: "center",
          alignContent: "center",
        }}
      >
        <Typography variant="h5">Dashboards</Typography>
        <IconButton onClick={() => setOpenCreateDashboardDialog(true)}>
          <AddIcon />
        </IconButton>
        {selectedDashboard && (
          <IconButton onClick={() => setOpenUpdateDashboardDialog(true)}>
            <EditIcon />
          </IconButton>
        )}
        {selectedDashboard && (
          <IconButton onClick={() => setDeleteConfirmation(true)}>
            <DeleteIcon />
          </IconButton>
        )}
        {selectedDashboard && (
          <FormControlLabel
            control={
              <Switch
                onChange={(_, checked) => {
                  setLiveModeMetrics([]);
                  setGraphData({});
                  setLiveMode(checked);
                }}
              />
            }
            label="LiveMode"
          />
        )}
      </Stack>

      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignContent: "center",
          mt: 2,
        }}
      >
        <FormControl sx={{ width: 150, m: 2 }}>
          <InputLabel id="dashboard-select-label">Dashboard</InputLabel>
          <Select
            labelId="dashboard-select-label"
            id="dashboard-select"
            value={selectedDashboard?.name || ""}
            label="Dashboard"
            onChange={(_, child) =>
              handleSelectDashboardChange((child as any).props.itemID)
            }
          >
            {dashboards.map((dashboard) => (
              <MenuItem itemID={dashboard.id} value={dashboard.name}>
                {dashboard.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        {!liveMode && (
          <>
            <FormControl sx={{ width: 100, m: 2 }}>
              <InputLabel id="aggregation-select-label">Aggregation</InputLabel>
              <Select
                labelId="aggregation-select-label"
                id="aggregation-select"
                value={aggretationType}
                label="Aggregation"
                onChange={(event) => setAggregationType(event.target.value)}
              >
                <MenuItem value="AVG">AVG</MenuItem>
                <MenuItem value="LTTB">LTTB</MenuItem>
              </Select>
            </FormControl>
            <DateTimePicker
              label="Data from"
              format="DD-MM-YYYY HH:mm"
              defaultValue={dataFrom}
              viewRenderers={{
                hours: renderTimeViewClock,
                minutes: renderTimeViewClock,
              }}
              onChange={(date) => {
                if (date) {
                  setDataFrom(date);
                }
              }}
              ampm={false}
              sx={{ m: 2 }}
            />
            <DateTimePicker
              label="Data to"
              format="DD-MM-YYYY HH:mm"
              defaultValue={dataTo}
              viewRenderers={{
                hours: renderTimeViewClock,
                minutes: renderTimeViewClock,
              }}
              onChange={(date) => {
                if (date) {
                  setDataTo(date);
                }
              }}
              ampm={false}
              sx={{ m: 2 }}
            />
            <Button
              variant="contained"
              sx={{ m: 2 }}
              onClick={handleLoadButtonClick}
            >
              Load
            </Button>
          </>
        )}
      </Box>
      {!loading && (
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignContent: "center",
            mt: 2,
          }}
        >
          <Box>
            {selectedDashboard?.graphs.map((graph) => (
              <>
                {graphDataLoading[graph.index] && (
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "center",
                      alignContent: "center",
                      mt: 2,
                      width: graphWidth,
                      height: graphHeight,
                    }}
                  >
                    <CircularProgress />
                  </Box>
                )}
                <Box>
                  {graphDataLoading[graph.index] != true && (
                    <>
                      <ResponsiveContainer
                        width={graphWidth}
                        height={graphHeight}
                      >
                        <LineChart
                          data={
                            graphData[graph.index]?.map((point) => ({
                              time: dayjs(point.time).unix(),
                              value: Number(point.value.toFixed(2)),
                            })) || []
                          }
                          margin={{ top: 10, right: 50, bottom: 0, left: 50 }}
                        >
                          <CartesianGrid strokeDasharray="3 3" />
                          <XAxis
                            type="number"
                            dataKey="time"
                            allowDataOverflow
                            allowDuplicatedCategory={false}
                            tickCount={15}
                            domain={["dataMin", "dataMax"]}
                            tickFormatter={(unixTime) =>
                              dayjs
                                .unix(unixTime)
                                .local()
                                .format("DD-MM-YY HH:mm:ss")
                            }
                          />
                          <YAxis
                            type="number"
                            domain={["auto", "auto"]}
                            tickCount={10}
                            unit={graph.unit}
                          />
                          <Tooltip
                            formatter={(value, _name, _props) => [
                              value,
                              graph.metricType + ` [${graph.unit}]`,
                            ]}
                            labelFormatter={(label, _) =>
                              "Time: " + dayjs.unix(label).format("HH:mm:ss")
                            }
                          />
                          <Legend verticalAlign="top" />
                          <Line
                            name={
                              "Sensor: " +
                              graph.sensorName +
                              " Metric: " +
                              graph.metricName +
                              " Type: " +
                              graph.metricType +
                              ` [${graph.unit}]`
                            }
                            type="monotone"
                            dataKey="value"
                            stroke={graph.lineColor}
                            strokeWidth={2}
                            dot={false}
                          />
                        </LineChart>
                      </ResponsiveContainer>
                    </>
                  )}
                </Box>
              </>
            ))}
          </Box>
        </Box>
      )}
      {loading && (
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            alignContent: "center",
            height: "100vh", // Ensure the parent container takes the full viewport height
          }}
        >
          <CircularProgress />
        </Box>
      )}
      <CreateDashboardDialog
        agentId={agentId as string}
        resourceGroupId={resourceGroupId as string}
        open={openCreateDashboardDialog}
        setOpen={setOpenCreateDashboardDialog}
        onCreate={() => {
          setRefreshDashboards(!refreshDashboards);
        }}
      ></CreateDashboardDialog>
      {selectedDashboard && (
        <UpdateDashboardDialog
          agentId={agentId as string}
          resourceGroupId={resourceGroupId as string}
          open={openUpdateDashboardDialog}
          setOpen={setOpenUpdateDashboardDialog}
          onUpdate={() => {
            setRefreshDashboards(!refreshDashboards);
          }}
          dashboard={selectedDashboard!}
          dashboardId={selectedDashboardId!}
        ></UpdateDashboardDialog>
      )}
      {deleteConfirmationDialog(
        "dashboard",
        deleteConfirmation,
        () => {
          setDeleteConfirmation(false);
        },
        handleDeleteDashboard
      )}
    </Container>
  );
};

export default DashboardsPage;

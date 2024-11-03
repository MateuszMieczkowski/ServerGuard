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
  Divider,
  Alert,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
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
  getDashboard,
  getDashboards,
  getGraphData,
} from "../api/dashboards-service";
import { DateTimePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import { renderTimeViewClock } from "@mui/x-date-pickers/timeViewRenderers";
import { CreateDashboardDialog } from "../components/create-dashboard-dialog";

const DashboardsPage = () => {
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

  const [refreshDashboards, setRefreshDashboards] = useState(false);

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
      100,
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
      selectedDashboard.graphs.forEach((graph, index) => {
        loadGraphData(index);
      });
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
        <Typography variant="h6">Dashboards</Typography>
        <Button
          startIcon={<AddIcon />}
          onClick={() => setOpenCreateDashboardDialog(true)}
        ></Button>
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
          format="YYYY-MM-DD HH:mm"
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
          timezone="UTC"
          ampm={false}
          sx={{ m: 2 }}
        />
        <DateTimePicker
          label="Data to"
          format="YYYY-MM-DD HH:mm"
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
          timezone="UTC"
          sx={{ m: 2 }}
        />
        <Button
          variant="contained"
          sx={{ m: 2 }}
          onClick={handleLoadButtonClick}
        >
          Load
        </Button>
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
                              dayjs.unix(unixTime).format("HH:mm:ss")
                            }
                          />
                          <YAxis
                            type="number"
                            domain={["auto", "auto"]}
                            tickCount={10}
                            unit={graph.unit}
                          />
                          <Tooltip
                            formatter={(value, name, props) => [
                              value,
                              graph.metricType + ` [${graph.unit}]`,
                            ]}
                            labelFormatter={(label, _) =>
                              "Time UTC: " +
                              dayjs.unix(label).format("HH:mm:ss")
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
    </Container>
  );
};

export default DashboardsPage;

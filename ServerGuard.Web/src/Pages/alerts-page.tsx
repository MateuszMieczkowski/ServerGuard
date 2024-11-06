import * as React from "react";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import { useEffect, useState } from "react";
import {
  Alert,
  AlertLog,
  deleteAlert,
  getAlertLogsPage,
  getAlertsPage,
} from "../api/alerts-service";
import { Page } from "../api/Page";
import { useParams } from "react-router-dom";
import {
  IconButton,
  Paper,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Button,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import dayjs from "dayjs";
import duration from "dayjs/plugin/duration";
import {
  DataGrid,
  GridToolbarContainer,
  GridSlots,
  GridToolbarProps,
} from "@mui/x-data-grid";
import AddIcon from "@mui/icons-material/Add";
import { CreateAlertDialog } from "../components/create-alert-dialog";
import { deleteConfirmationDialog } from "../components/delete-confirmation-dialog";

dayjs.extend(duration);

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function CustomTabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

function a11yProps(index: number) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

interface ToolbarProps {
  onClick: () => void;
}

function Toolbar(props: ToolbarProps) {
  return (
    <GridToolbarContainer>
      <IconButton onClick={props.onClick}>
        <AddIcon />
      </IconButton>
    </GridToolbarContainer>
  );
}

function createAlertDialog(
  resourceGroupId: string,
  agentId: string,
  open: boolean,
  setOpen: (open: boolean) => void,
  onCreate: () => void
) {
  return (
    <CreateAlertDialog
      resourceGroupId={resourceGroupId}
      agentId={agentId}
      open={open}
      setOpen={setOpen}
      onCreate={onCreate}
    ></CreateAlertDialog>
  );
}

export function AlertsPage() {
  const { resourceGroupId, agentId } = useParams();
  const [tabValue, setTabValue] = React.useState(0);

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const [alerts, setAlerts] = React.useState<Page<Alert> | null>();
  const [alertsPagination, setAlertsPagination] = React.useState({
    page: 0,
    pageSize: 10,
  });

  const [alertLogs, setAlertLogs] = React.useState<Page<AlertLog> | null>();
  const [alertLogsPagination, setAlertLogsPagination] = React.useState({
    page: 0,
    pageSize: 10,
  });

  const [refreshAlerts, setRefreshAlerts] = React.useState(false);

  const [open, setOpen] = useState(false);
  const [selectedAlertId, setSelectedAlertId] = useState<string | null>(null);
  const [createOpenDialogOpen, setCreateOpenDialogOpen] = useState(false);

  const handleClickOpen = (id: string) => {
    setSelectedAlertId(id);
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedAlertId(null);
  };

  const handleConfirmDelete = () => {
    if (selectedAlertId) {
      handleDeleteAlert(selectedAlertId);
    }
    handleClose();
  };

  useEffect(() => {
    if (!resourceGroupId || !agentId) {
      return;
    }
    if (tabValue === 0) {
      getAlertsPage(
        resourceGroupId,
        agentId,
        alertsPagination.page,
        alertsPagination.pageSize
      ).then((response) => {
        setAlerts(response);
      });
    } else {
      getAlertLogsPage(
        resourceGroupId,
        agentId,
        alertLogsPagination.page,
        alertLogsPagination.pageSize
      ).then((response) => {
        setAlertLogs(response);
      });
    }
  }, [
    resourceGroupId,
    agentId,
    tabValue,
    alertsPagination,
    alertLogsPagination,
    refreshAlerts,
  ]);

  async function handleDeleteAlert(alertId: string) {
    if (!resourceGroupId || !agentId) {
      return;
    }
    await deleteAlert(resourceGroupId, agentId, alertId);
    setRefreshAlerts(!refreshAlerts);
  }

  return (
    <Box
      sx={{ display: "flex", justifyContent: "center", alignContent: "center" }}
    >
      <Box>
        <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
          <Tabs
            value={tabValue}
            onChange={handleChange}
            aria-label="Alerts and Alert Log"
          >
            <Tab label="Alerts" {...a11yProps(0)} />
            <Tab label="Alert Log" {...a11yProps(1)} />
          </Tabs>
        </Box>
        <CustomTabPanel value={tabValue} index={0}>
          <Paper sx={{ width: "100%" }}>
            <DataGrid
              sx={{ width: 1000 }}
              rows={alerts?.content || []}
              disableColumnSorting
              disableColumnFilter
              disableRowSelectionOnClick
              disableColumnMenu
              paginationModel={alertsPagination}
              autoPageSize
              paginationMode="server"
              rowCount={alerts?.totalElements || 0}
              slots={{
                toolbar: Toolbar as GridSlots["toolbar"],
              }}
              slotProps={{
                toolbar: {
                  onClick: () => {
                    setCreateOpenDialogOpen(true);
                  },
                },
              }}
              onPaginationModelChange={(model) => {
                setAlertsPagination({
                  ...alertsPagination,
                  page: model.page,
                });
              }}
              columns={[
                { field: "name", headerName: "Name", flex: 2 },
                { field: "sensorName", headerName: "Sensor name", flex: 1 },
                { field: "metricName", headerName: "Metric name", flex: 1 },
                { field: "metricType", headerName: "Metric type", flex: 1 },
                { field: "groupBy", headerName: "Group by", flex: 1 },
                { field: "operator", headerName: "Operator", flex: 1 },
                { field: "threshold", headerName: "Threshold", flex: 1 },
                {
                  field: "duration",
                  headerName: "Duration",
                  flex: 1,
                  valueGetter: (value, row, column) => {
                    return dayjs.duration(value).locale("en").humanize();
                  },
                },
                {
                  field: "actions",
                  headerName: "",
                  flex: 1,
                  renderCell: (value) => (
                    <IconButton
                      aria-label="delete"
                      sx={{ m: 0, p: 0 }}
                      onClick={() => handleClickOpen(value.row.id)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  ),
                },
              ]}
            />
          </Paper>
        </CustomTabPanel>
        <CustomTabPanel value={tabValue} index={1}>
          <Paper sx={{ width: "100%" }}>
            <DataGrid
              sx={{ width: 1000 }}
              rows={alertLogs?.content || []}
              disableColumnSorting
              disableColumnFilter
              disableRowSelectionOnClick
              disableColumnMenu
              paginationModel={alertLogsPagination}
              autoPageSize
              paginationMode="server"
              rowCount={alertLogs?.totalElements || 0}
              onPaginationModelChange={(model) => {
                setAlertLogsPagination({
                  ...alertLogsPagination,
                  page: model.page,
                });
              }}
              columns={[
                { field: "name", headerName: "Name", flex: 2 },
                {
                  field: "triggeredAt",
                  headerName: "Triggered at",
                  flex: 1,
                  valueGetter: (value, row, column) => {
                    return dayjs(value).format("YYYY-MM-DD HH:mm:ss");
                  },
                },
                {
                  field: "triggeredByValue",
                  headerName: "Triggered by",
                  flex: 1,
                },
                { field: "sensorName", headerName: "Sensor name", flex: 1 },
                { field: "metricName", headerName: "Metric name", flex: 1 },
                { field: "metricType", headerName: "Metric type", flex: 1 },
                { field: "groupBy", headerName: "Group by", flex: 1 },
                { field: "operator", headerName: "Operator", flex: 1 },
                { field: "threshold", headerName: "Threshold", flex: 1 },
                {
                  field: "duration",
                  headerName: "Duration",
                  flex: 1,
                  valueGetter: (value, row, column) => {
                    return dayjs.duration(value).locale("en").humanize();
                  },
                },
              ]}
            />
          </Paper>
        </CustomTabPanel>
        {deleteConfirmationDialog(
          "Alert",
          open,
          handleClose,
          handleConfirmDelete
        )}
        {createAlertDialog(
          resourceGroupId ?? "",
          agentId ?? "",
          createOpenDialogOpen,
          setCreateOpenDialogOpen,
          () => {
            setRefreshAlerts(!refreshAlerts);
          }
        )}
      </Box>
    </Box>
  );
}

export default AlertsPage;

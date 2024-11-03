import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getResourceGroup, ResourceGroup } from "../api/resource-group-service";
import { getAgents, GetAgentsResponse } from "../api/agents-service";
import {
  Container,
  Typography,
  Card,
  CardContent,
  Stack,
  Pagination,
  Button,
  CardActions,
} from "@mui/material";
import Grid from "@mui/material/Grid2";
import AddIcon from "@mui/icons-material/Add";
import CreateAgentDialog from "../components/create-agent-dialog";
import TimelineOutlinedIcon from "@mui/icons-material/TimelineOutlined";
import AnnouncementOutlinedIcon from "@mui/icons-material/AnnouncementOutlined";
import MoreVertOutlinedIcon from "@mui/icons-material/MoreVertOutlined";
import dayjs from "dayjs";

const AgentsPage = () => {
  const { resourceGroupId } = useParams();
  const [resourceGroup, setResourceGroup] = useState<ResourceGroup | null>(
    null
  );
  const [refreshAgents, setRefreshAgents] = useState(false);
  const handleCreateAgent = () => {
    setRefreshAgents(!refreshAgents);
  };
  const [openCreateAgentDialog, setOpenCreateAgentDialog] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [getAgentsResponse, setGetAgentsResponse] =
    useState<GetAgentsResponse | null>(null);
  useEffect(() => {
    getResourceGroup(resourceGroupId as string).then((response) => {
      setResourceGroup(response);
    });
  }, [resourceGroupId]);

  useEffect(() => {
    if (resourceGroup) {
      getAgents(resourceGroup.id, currentPage - 1, 9).then((response) => {
        setGetAgentsResponse(response);
        setCurrentPage(response.pageNumber + 1);
      });
    }
  }, [resourceGroup, currentPage, refreshAgents]);

  const navigate = useNavigate();

  return (
    <Container>
      <Stack
        direction="row"
        spacing={2}
        sx={{
          display: "flex",
          justifyContent: "center",
          alignContent: "center",
        }}
      >
        <Typography variant="h4">Agents: {resourceGroup?.name}</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setOpenCreateAgentDialog(true)}
        >
          Create
        </Button>
      </Stack>
      <Grid
        container
        spacing={3}
        mt={3}
        sx={{
          display: "flex",
          justifyContent: "center",
          alignContent: "center",
        }}
      >
        {getAgentsResponse?.agents.map((agent) => (
          <Grid key={agent.id} size={4}>
            <Card
              sx={{
                border: 1,
                borderColor:
                  agent.lastContactAt === null ||
                  dayjs(agent.lastContactAt).isBefore(
                    dayjs().utc().subtract(5, "minutes")
                  )
                    ? "red"
                    : "primary.main",
              }}
            >
              <CardContent>
                <Typography variant="h6">{agent.name}</Typography>
                <Typography variant="body2">
                  Last contact at:{" "}
                  {dayjs(agent.lastContactAt)
                    .utc()
                    .format("DD:MM:YYYY/HH:mm:ss") ?? "-"}
                </Typography>
              </CardContent>
              <CardActions sx={{ my: "auto" }}>
                <Button
                  startIcon={<TimelineOutlinedIcon />}
                  onClick={() => navigate(`${agent.id}/dashboards`)}
                >
                  Dashboards
                </Button>
                <Button startIcon={<AnnouncementOutlinedIcon />}>Alerts</Button>
                <Button startIcon={<MoreVertOutlinedIcon />}>More</Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
      {getAgentsResponse?.totalPages != 0 && (
        <Pagination
          sx={{ display: "flex", justifyContent: "center", mt: 2 }}
          count={getAgentsResponse?.totalPages}
          page={currentPage}
          onChange={(_, page) => setCurrentPage(page)}
        />
      )}
      <CreateAgentDialog
        resourceGroupId={resourceGroupId as string}
        open={openCreateAgentDialog}
        setOpen={setOpenCreateAgentDialog}
        onCreate={handleCreateAgent}
      />
    </Container>
  );
};

export default AgentsPage;

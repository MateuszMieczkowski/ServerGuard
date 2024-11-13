import { useEffect, useState } from "react";
import {
  Typography,
  Drawer,
  List,
  ListItemText,
  AppBar,
  Toolbar,
  CssBaseline,
  Box,
  ListItemButton,
  Collapse,
  ListItemIcon,
  Divider,
  Button,
  Pagination,
} from "@mui/material";
import { Outlet, useNavigate } from "react-router-dom";
import { ExpandLess, ExpandMore } from "@mui/icons-material";
import AdminPanelSettingsOutlinedIcon from "@mui/icons-material/AdminPanelSettingsOutlined";
import AddIcon from "@mui/icons-material/Add";
import CreateResourceGroupDialog from "../components/create-resource-group-dialog";
import AppsIcon from "@mui/icons-material/Apps";
import StorageIcon from "@mui/icons-material/Storage";
import {
  getResourceGroups,
  GetResourceGroupsResponse,
} from "../api/resource-group-service";

const drawerWidth = 240;

const RootPage = () => {
  const isAuthenticated = localStorage.getItem("authToken") !== null;
  const [openCreateResourceGroupDialog, setOpenCreateResourceGroupDialog] =
    useState(false);
  const [refreshResourceGroups, setrefreshResourceGroups] = useState(false);
  const [openStates, setOpenStates] = useState<{ [key: string]: boolean }>({});
  const handleCreateResourceGroup = () => {
    setrefreshResourceGroups(!refreshResourceGroups);
  };
  const [currentPage, setCurrentPage] = useState(1);

  const handleClick = (id: string) => {
    setOpenStates((prevState) => ({
      ...prevState,
      [id]: !prevState[id],
    }));
  };
  const [getResourceGroupsResponse, setGetResourceGroupsResponse] =
    useState<GetResourceGroupsResponse | null>(null);

  useEffect(() => {
    getResourceGroups(currentPage - 1, 10).then((response) => {
      setGetResourceGroupsResponse(response);
      setCurrentPage(response.pageNumber + 1);
      const initialOpenStates = response.resourceGroups.reduce((acc, group) => {
        acc[group.id] = false;
        return acc;
      }, {} as { [key: string]: boolean });
      setOpenStates(initialOpenStates);
    });
  }, [refreshResourceGroups, currentPage]);

  const navigate = useNavigate();

  const handleAgentsClick = (resourceGroupId: string) => {
    navigate(`/resourceGroups/${resourceGroupId}/agents`);
  };

  if (!isAuthenticated) {
    navigate("/sign-in");
  }

  return (
    <>
      <Box>
        <CssBaseline />
        <AppBar
          position="fixed"
          sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}
        >
          <Toolbar>
            <Typography variant="h6" noWrap>
              ServerGuard
            </Typography>
            <Box sx={{ flexGrow: 1 }} />
            <Button
              variant="outlined"
              color="inherit"
              onClick={() => {
                localStorage.removeItem("authToken");
                navigate("/sign-in");
              }}
              sx={{ marginLeft: "auto" }}
            >
              Logout
            </Button>
          </Toolbar>
        </AppBar>
        <Drawer
          variant="permanent"
          sx={{
            width: { drawerWidth },
            flexShrink: 0,
            [`& .MuiDrawer-paper`]: {
              width: drawerWidth,
              boxSizing: "border-box",
            },
          }}
        >
          <Toolbar />
          <Box sx={{ overflow: "auto" }} role="presentation">
            <List>
              {getResourceGroupsResponse &&
                getResourceGroupsResponse.resourceGroups.map((group) => {
                  return (
                    <>
                      <ListItemButton
                        onClick={() => handleClick(group.id)}
                        id={group.id}
                      >
                        <ListItemIcon>
                          <AppsIcon />
                        </ListItemIcon>
                        <ListItemText primary={group.name} />
                        {openStates[group.id] ? <ExpandLess /> : <ExpandMore />}
                      </ListItemButton>
                      <Collapse
                        in={openStates[group.id]}
                        timeout="auto"
                        unmountOnExit
                      >
                        <List component="div" disablePadding>
                          <ListItemButton
                            sx={{ pl: 4 }}
                            onClick={() => handleAgentsClick(group.id)}
                          >
                            <ListItemIcon>
                              <StorageIcon />
                            </ListItemIcon>
                            <ListItemText primary="Agents" />
                          </ListItemButton>
                          <ListItemButton
                            sx={{ pl: 4 }}
                            onClick={() =>
                              navigate(`/resourceGroups/${group.id}/settings`)
                            }
                          >
                            <ListItemIcon>
                              <AdminPanelSettingsOutlinedIcon />
                            </ListItemIcon>
                            <ListItemText primary="Settings" />
                          </ListItemButton>
                        </List>
                      </Collapse>
                      <Divider />
                    </>
                  );
                })}
            </List>
            {getResourceGroupsResponse && (
              <Box sx={{ display: "flex", justifyContent: "center" }}>
                <Pagination
                  page={currentPage}
                  count={getResourceGroupsResponse.totalPages}
                  siblingCount={0}
                  boundaryCount={0}
                  onChange={(_, page) => {
                    setCurrentPage(page);
                  }}
                />
              </Box>
            )}

            <Box sx={{ position: "static", bottom: 0, width: "100%", p: 1 }}>
              <Button
                variant="contained"
                fullWidth
                startIcon={<AddIcon />}
                onClick={() => setOpenCreateResourceGroupDialog(true)}
              >
                Create new
              </Button>
            </Box>
          </Box>
        </Drawer>
        <Box sx={{ mt: 9, ml: 29 }}>
          <Outlet />
        </Box>
      </Box>
      <CreateResourceGroupDialog
        open={openCreateResourceGroupDialog}
        setOpen={setOpenCreateResourceGroupDialog}
        onCreate={handleCreateResourceGroup}
      />
    </>
  );
};

export default RootPage;

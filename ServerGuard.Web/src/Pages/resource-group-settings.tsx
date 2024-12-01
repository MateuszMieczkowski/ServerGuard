import {
  TextField,
  Container,
  Typography,
  Box,
  Paper,
  IconButton,
} from "@mui/material";
import { useFormik } from "formik";
import { useEffect, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import * as Yup from "yup";
import {
  deleteUser,
  getResourceGroup,
  getUsers,
  ResourceGroup,
  updateResourceGroup,
  User,
} from "../api/resource-group-service";
import { DataGrid, GridSlots, GridToolbarContainer } from "@mui/x-data-grid";
import { Page } from "../api/Page";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import { debounce } from "lodash";
import InviteUserDialog from "../components/invite-user-dialog";
import { getCurrentUserEmail } from "../util";
import { deleteConfirmationDialog } from "../components/delete-confirmation-dialog";

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

export const ResourceGroupSettingsPage = () => {
  const { resourceGroupId } = useParams();
  const [resourceGroup, setResourceGroup] = useState<ResourceGroup | null>(
    null
  );
  const [usersPage, setUsersPage] = useState<Page<User> | null>(null);

  const [usersPagination, setUsersPagination] = useState({
    page: 0,
    pageSize: 10,
  });
  const [_, setResourceGroupName] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [refreshUsers, setRefreshUsers] = useState(false);
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [userToDeleteId, setUserToDeleteId] = useState<string | null>(null);

  useEffect(() => {
    if (!resourceGroupId) {
      return;
    }
    getResourceGroup(resourceGroupId).then((response) => {
      setResourceGroup(response);
      setResourceGroupName(response.name);
    });
  }, [resourceGroupId]);

  useEffect(() => {
    if (!resourceGroupId) {
      return;
    }
    getUsers(
      resourceGroupId,
      usersPagination.page,
      usersPagination.pageSize
    ).then((response) => {
      setUsersPage(response);
    });
  }, [refreshUsers, usersPagination]);

  const removeUser = async (userId: string) => {
    if (!resourceGroupId) {
      return;
    }
    await deleteUser(resourceGroupId, userId);
  };

  const handleSubmit = useCallback(
    async (name: string) => {
      if (!resourceGroupId || name.length < 3 || isSubmitting) {
        return;
      }

      setIsSubmitting(true);
      try {
        await updateResourceGroup(resourceGroupId, name);
        setResourceGroupName(name);
      } catch (error) {
        // Handle error
      } finally {
        setIsSubmitting(false);
      }
    },
    [resourceGroupId, isSubmitting]
  );

  const debouncedSubmit = useCallback(
    debounce((name: string) => {
      if (name.length >= 3 && name !== resourceGroup?.name) {
        handleSubmit(name);
      }
    }, 1000),
    [handleSubmit, resourceGroup]
  );

  useEffect(() => {
    return () => {
      debouncedSubmit.cancel();
    };
  }, [debouncedSubmit]);

  const formik = useFormik({
    initialValues: {
      name: resourceGroup?.name ?? "",
    },
    enableReinitialize: true,
    validationSchema: Yup.object({
      name: Yup.string()
        .required("Resource Group Name is required")
        .min(3, "Resource Group Name must be at least 3 characters"),
    }),
    onSubmit: (values) => {
      handleSubmit(values.name);
    },
  });

  useEffect(() => {
    if (formik.values.name !== resourceGroup?.name) {
      debouncedSubmit(formik.values.name);
    }
  }, [formik.values.name]);

  return (
    <Container>
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignContent: "center",
        }}
      >
        <Typography variant="h5">Resource Group Settings</Typography>
      </Box>
      <Box
        component="form"
        onSubmit={formik.handleSubmit}
        sx={{ width: "20%" }}
      >
        <TextField
          label="Resource Group Name"
          name="name"
          value={formik.values.name}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.name && Boolean(formik.errors.name)}
          helperText={formik.touched.name && formik.errors.name}
          fullWidth
          margin="normal"
        />
      </Box>
      <Paper sx={{ width: "100%" }}>
        <DataGrid
          rows={usersPage?.content || []}
          disableColumnSorting
          disableColumnFilter
          disableRowSelectionOnClick
          disableColumnMenu
          paginationModel={usersPagination}
          autoPageSize
          paginationMode="server"
          rowCount={usersPage?.totalElements || 0}
          slots={{
            toolbar: Toolbar as GridSlots["toolbar"],
          }}
          slotProps={{
            toolbar: {
              onClick: () => {
                setInviteDialogOpen(true);
              },
            },
          }}
          onPaginationModelChange={(model) => {
            setUsersPagination({
              ...usersPagination,
              page: model.page,
            });
          }}
          columns={[
            {
              field: "email",
              headerName: "Email",
              flex: 1,
              renderCell: (value) => (
                <Typography variant="body1">
                  {getCurrentUserEmail() === value.row.email && "(You) "}
                  {value.row.email}
                </Typography>
              ),
            },
            {
              field: "role",
              headerName: "Role",
              flex: 1,
            },
            {
              field: "actions",
              headerName: "",
              flex: 1,
              renderCell: (value) => (
                <>
                  {getCurrentUserEmail() !== value.row.email && (
                    <IconButton
                      aria-label="delete"
                      sx={{ m: 0, p: 0 }}
                      onClick={() => {
                        setUserToDeleteId(value.row.id);
                        setDeleteDialogOpen(true);
                      }}
                    >
                      <DeleteIcon />
                    </IconButton>
                  )}
                </>
              ),
            },
          ]}
        />
      </Paper>
      <InviteUserDialog
        resourceGroupId={resourceGroupId ?? ""}
        isOpen={inviteDialogOpen}
        setOpen={setInviteDialogOpen}
      />
      {deleteConfirmationDialog(
        "Resource Group User",
        deleteDialogOpen,
        () => {
          setUserToDeleteId(null);
          setDeleteDialogOpen(false);
        },
        () => {
          removeUser(userToDeleteId ?? "").then(() => {
            setRefreshUsers(!refreshUsers);
          });
        }
      )}
    </Container>
  );
};

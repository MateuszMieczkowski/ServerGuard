import React, { useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  Button,
  DialogActions,
  Divider,
  Typography,
  Switch,
  FormGroup,
  FormControlLabel,
  CircularProgress,
  Box,
} from "@mui/material";
import { useFormik } from "formik";
import * as Yup from "yup";
import { createResourceGroup } from "../api/resource-group-service";
import { Label } from "@mui/icons-material";
import {
  Agent,
  AgentDetails,
  createAgent,
  CreateAgentRequest,
  getAgent,
  updateAgent,
} from "../api/agents-service";

interface UpdateAgentDialogProps {
  resourceGroupId: string;
  agentId: string;
  open: boolean;
  setOpen: (open: boolean) => void;
  onUpdate: () => void;
}

export const UpdateAgentDialog: React.FC<UpdateAgentDialogProps> = ({
  resourceGroupId,
  open,
  setOpen,
  onUpdate,
  agentId,
}) => {
  const handleClose = async () => {
    formik.resetForm();
    setOpen(false);
  };
  const [agent, setAgent] = React.useState<AgentDetails>({
    id: "",
    name: "",
    lastContactAt: "",
    config: {
      collectEverySeconds: 0,
      apiKey: "",
      cpuEnabled: false,
      gpuEnabled: false,
      memoryEnabled: false,
      networkEnabled: false,
      controllerEnabled: false,
      motherboardEnabled: false,
      storageEnabled: false,
    },
  });
  const [loading, setLoading] = React.useState(false);

  const [apiKeyInputType, setApiKeyInputType] = React.useState("password");

  useEffect(() => {
    if (open) {
      setLoading(true);
      getAgent(resourceGroupId, agentId).then(async (response) => {
        setAgent(response);
        await formik
          .setValues({
            name: response.name,
            collectEverySeconds: response.config.collectEverySeconds,
            isCpuEnabled: response.config.cpuEnabled,
            isGpuEnabled: response.config.gpuEnabled,
            isMemoryEnabled: response.config.memoryEnabled,
            isNetworkEnabled: response.config.networkEnabled,
            isControllerEnabled: response.config.controllerEnabled,
            isMotherboardEnabled: response.config.motherboardEnabled,
            isStorageEnabled: response.config.storageEnabled,
          })
          .catch((error) => {
            setLoading(false);
          });
        setLoading(false);
      });
    }
  }, [open]);

  const formik = useFormik({
    initialValues: {
      name: "                  ",
      collectEverySeconds: 15,
      isCpuEnabled: false,
      isGpuEnabled: false,
      isMemoryEnabled: false,
      isNetworkEnabled: false,
      isControllerEnabled: false,
      isMotherboardEnabled: false,
      isStorageEnabled: false,
    },
    validationSchema: Yup.object({
      name: Yup.string().min(5).max(50).required(),
      collectEverySeconds: Yup.number().min(1).required(),
    }),
    onSubmit: async (values) => {
      try {
        const request: CreateAgentRequest = {
          name: values.name,
          agentConfig: {
            collectEverySeconds: values.collectEverySeconds,
            isCpuEnabled: values.isCpuEnabled,
            isGpuEnabled: values.isGpuEnabled,
            isMemoryEnabled: values.isMemoryEnabled,
            isNetworkEnabled: values.isNetworkEnabled,
            isControllerEnabled: values.isControllerEnabled,
            isMotherboardEnabled: values.isMotherboardEnabled,
            isStorageEnabled: values.isStorageEnabled,
          },
        };
        await updateAgent(resourceGroupId, agent.id, request);
        onUpdate();
      } catch (error) {}
      handleClose();
    },
    validateOnChange: false,
  });

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      PaperProps={{
        component: "form",
        onSubmit: formik.handleSubmit,
      }}
    >
      <DialogTitle>Agent details</DialogTitle>
      {loading && (
        <DialogContent>
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <CircularProgress />
          </Box>
        </DialogContent>
      )}
      {!loading && (
        <DialogContent>
          <TextField
            autoFocus
            fullWidth
            margin="dense"
            variant="outlined"
            id="agentName"
            name="name"
            label="Agent name"
            type="text"
            value={formik.values.name}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.name && Boolean(formik.errors.name)}
            helperText={formik.touched.name && formik.errors.name}
          />
          <TextField
            fullWidth
            margin="dense"
            variant="outlined"
            id="collectEverySeconds"
            name="collectEverySeconds"
            label="Collect every (seconds)"
            type="number"
            value={formik.values.collectEverySeconds}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={
              formik.touched.collectEverySeconds &&
              Boolean(formik.errors.collectEverySeconds)
            }
            helperText={
              formik.touched.collectEverySeconds &&
              formik.errors.collectEverySeconds
            }
          />
          <Typography
            variant="body1"
            id="apiKey"
            onClick={() => {
              if (apiKeyInputType === "password") {
                setApiKeyInputType("text");
              } else {
                setApiKeyInputType("password");
              }
            }}
          >
            Api key (Click): {apiKeyInputType === "text" && agent.config.apiKey}
            {apiKeyInputType === "password" &&
              agent.config.apiKey.replace(/./g, "X")}
          </Typography>
          <Divider sx={{ my: 2 }} />
          <Typography variant="h6">Enabled metrics</Typography>
          <FormGroup>
            <FormControlLabel
              control={
                <Switch
                  checked={formik.values.isCpuEnabled}
                  onChange={(_, checked) =>
                    formik.setFieldValue("isCpuEnabled", checked)
                  }
                />
              }
              label="Cpu"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formik.values.isGpuEnabled}
                  onChange={(_, checked) =>
                    formik.setFieldValue("isGpuEnabled", checked)
                  }
                />
              }
              label="Gpu"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formik.values.isMemoryEnabled}
                  onChange={(_, checked) =>
                    formik.setFieldValue("isMemoryEnabled", checked)
                  }
                />
              }
              label="Memory"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formik.values.isMotherboardEnabled}
                  onChange={(_, checked) =>
                    formik.setFieldValue("isMotherboardEnabled", checked)
                  }
                />
              }
              label="Motherboard"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formik.values.isControllerEnabled}
                  onChange={(_, checked) =>
                    formik.setFieldValue("isControllerEnabled", checked)
                  }
                />
              }
              label="Controller"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formik.values.isNetworkEnabled}
                  onChange={(_, checked) =>
                    formik.setFieldValue("isNetworkEnabled", checked)
                  }
                />
              }
              label="Network"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={formik.values.isStorageEnabled}
                  onChange={(_, checked) =>
                    formik.setFieldValue("isStorageEnabled", checked)
                  }
                />
              }
              label="Storage"
            />
          </FormGroup>
          <DialogActions>
            <Button
              color="primary"
              disabled={formik.isSubmitting}
              onClick={handleClose}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              color="primary"
              disabled={formik.isSubmitting}
            >
              Update
            </Button>
          </DialogActions>
        </DialogContent>
      )}
    </Dialog>
  );
};

import React from "react";
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
} from "@mui/material";
import { useFormik } from "formik";
import * as Yup from "yup";
import { createResourceGroup } from "../api/resource-group-service";
import { Label } from "@mui/icons-material";
import { createAgent, CreateAgentRequest } from "../api/agents-service";

interface CreateAgentDialogProps {
  resourceGroupId: string;
  open: boolean;
  setOpen: (open: boolean) => void;
  onCreate: () => void;
}

const CreateAgentDialog: React.FC<CreateAgentDialogProps> = ({
  resourceGroupId,
  open,
  setOpen,
  onCreate,
}) => {
  const handleClose = async () => {
    formik.resetForm();
    setOpen(false);
  };

  const formik = useFormik({
    initialValues: {
      name: "",
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
        await createAgent(resourceGroupId, request);
        onCreate();
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
      <DialogTitle>New agent</DialogTitle>
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
          autoFocus
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
        <Divider sx={{ my: 2 }} />
        <Typography variant="h6">Enabled metrics</Typography>
        <FormGroup>
          <FormControlLabel
            control={
              <Switch
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
          <Button type="submit" color="primary" disabled={formik.isSubmitting}>
            Create
          </Button>
        </DialogActions>
      </DialogContent>
    </Dialog>
  );
};

export default CreateAgentDialog;

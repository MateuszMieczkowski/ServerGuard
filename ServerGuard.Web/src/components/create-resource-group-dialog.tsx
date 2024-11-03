import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  TextField,
  Button,
  DialogActions,
} from "@mui/material";
import { useFormik } from "formik";
import * as Yup from "yup";
import { createResourceGroup } from "../api/resource-group-service";

interface CreateResourceGroupDialogProps {
  open: boolean;
  setOpen: (open: boolean) => void;
  onCreate: () => void;
}

const CreateResourceGroupDialog: React.FC<CreateResourceGroupDialogProps> = ({
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
    },
    validationSchema: Yup.object({
      name: Yup.string().min(5).max(50).required(),
    }),
    onSubmit: async (values) => {
      try {
        await createResourceGroup(values.name);
        onCreate();
      } catch (error) {}
      handleClose();
    },
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
      <DialogTitle>New resource group</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          fullWidth
          margin="dense"
          variant="outlined"
          id="resourceGroupName"
          name="name"
          label="Resource group name"
          type="text"
          value={formik.values.name}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.name && Boolean(formik.errors.name)}
          helperText={formik.touched.name && formik.errors.name}
        />
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

export default CreateResourceGroupDialog;

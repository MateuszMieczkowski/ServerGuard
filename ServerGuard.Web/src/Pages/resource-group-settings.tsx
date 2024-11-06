import { TextField, Button, Container, Typography, Box } from "@mui/material";
import { useFormik } from "formik";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import * as Yup from "yup";
import {
  getResourceGroup,
  ResourceGroup,
  updateResourceGroup,
} from "../api/resource-group-service";

export const ResourceGroupSettingsPage = () => {
  const { resourceGroupId } = useParams();
  const [resourceGroup, setResourceGroup] = useState<ResourceGroup | null>(
    null
  );

  useEffect(() => {
    if (!resourceGroupId) {
      return;
    }
    getResourceGroup(resourceGroupId).then((response) => {
      setResourceGroup(response);
    });
  }, [resourceGroupId]);

  const handleSubmit = (name: string) => {
    if (!resourceGroupId) {
      return;
    }
    updateResourceGroup(resourceGroupId, name)
      .then(() => {})
      .catch(() => {});
  };

  const formik = useFormik({
    initialValues: {
      name: resourceGroup?.name || "",
    },
    validationSchema: Yup.object({
      name: Yup.string()
        .required("Resource Group Name is required")
        .min(3, "Resource Group Name must be at least 3 characters"),
    }),
    onSubmit: (values) => {
      handleSubmit(values.name);
    },
  });

  return (
    <Container>
      <Typography variant="h4" gutterBottom>
        Update Resource Group Name
      </Typography>
      <Box component="form" onSubmit={formik.handleSubmit}>
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
        <Button type="submit" variant="contained" color="primary">
          Save
        </Button>
      </Box>
    </Container>
  );
};

import {
  Dialog,
  DialogContent,
  DialogTitle,
  TextField,
  Select,
  MenuItem,
  Button,
  FormControl,
  InputLabel,
  FormHelperText,
  Box,
  Typography,
} from "@mui/material";
import { useFormik } from "formik";
import * as Yup from "yup";
import { inviteUser } from "../api/resource-group-service";

interface InviteUserDialogProps {
  resourceGroupId: string;
  isOpen: boolean;
  setOpen: (open: boolean) => void;
}

const validationSchema = Yup.object({
  email: Yup.string()
    .email("Invalid email format")
    .required("Email is required"),
  role: Yup.string()
    .oneOf(["admin", "user"], "Invalid role")
    .required("Role is required"),
});

const InviteUserDialog = ({
  resourceGroupId,
  isOpen,
  setOpen,
}: InviteUserDialogProps) => {
  const formik = useFormik({
    initialValues: {
      email: "",
      role: "user",
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        await inviteUser(resourceGroupId, values.email, values.role);
        setOpen(false);
      } catch (error) {
        console.error("Error inviting user:", error);
      }
    },
  });

  return (
    <Dialog open={isOpen} onClose={() => setOpen(false)}>
      <DialogTitle>Invite User</DialogTitle>
      <DialogContent>
        <Box
          component="form"
          onSubmit={formik.handleSubmit}
          style={{
            display: "flex",
            flexDirection: "column",
            gap: "20px",
            minWidth: "300px",
          }}
        >
          <Typography variant="body1" color="textSecondary">
            After inviting a user, Email will be sent to the user with the
            invitation link.
          </Typography>
          <TextField
            fullWidth
            id="email"
            name="email"
            label="Email"
            value={formik.values.email}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.email && Boolean(formik.errors.email)}
            helperText={formik.touched.email && formik.errors.email}
          />

          <FormControl
            fullWidth
            error={formik.touched.role && Boolean(formik.errors.role)}
          >
            <InputLabel id="role-label">Role</InputLabel>
            <Select
              labelId="role-label"
              id="role"
              name="role"
              value={formik.values.role}
              label="Role"
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            >
              <MenuItem value="user">User</MenuItem>
              <MenuItem value="admin">Admin</MenuItem>
            </Select>
            {formik.touched.role && formik.errors.role && (
              <FormHelperText>{formik.errors.role}</FormHelperText>
            )}
          </FormControl>

          <Button type="submit" variant="contained" color="primary" fullWidth>
            Invite
          </Button>
        </Box>
      </DialogContent>
    </Dialog>
  );
};

export default InviteUserDialog;

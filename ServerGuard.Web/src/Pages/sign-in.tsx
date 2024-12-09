import { useState } from "react";
import { useFormik } from "formik";
import * as Yup from "yup";
import {
  Typography,
  Box,
  TextField,
  Button,
  Snackbar,
  Alert,
  Link,
  Container,
} from "@mui/material";
import { Navigate, Link as RouterLink } from "react-router-dom";
import { loginUser } from "../api/users-service";

const validationSchema = Yup.object({
  email: Yup.string().email("Invalid email address").required("Required"),
  password: Yup.string().required("Required"),
});

const SignIn = () => {
  const [loading, setLoading] = useState(false);
  const [loginSuccess, setLoginSuccess] = useState<boolean | null>(null);
  const [snackbar, setSnackbar] = useState<{
    open: boolean;
    severity: "success" | "error" | "warning" | "info";
    message: string;
  }>({
    open: false,
    severity: "success",
    message: "",
  });

  const formik = useFormik({
    initialValues: {
      email: "",
      password: "",
    },
    validationSchema: validationSchema,
    onSubmit: async (values) => {
      setLoading(true);
      const { email, password } = values;
      try {
        setLoginSuccess(await loginUser(email, password));
        if (loginSuccess) {
          setSnackbar({
            open: true,
            severity: "success",
            message: "Signed in successfully!",
          });
        } else {
          setSnackbar({
            open: true,
            severity: "error",
            message: "Sign in failed. Please check your credentials.",
          });
        }
      } catch (error) {
        setSnackbar({
          open: true,
          severity: "error",
          message: "An error occurred. Please try again.",
        });
      } finally {
        setLoading(false);
      }
    },
  });

  if (loginSuccess) {
    return <Navigate to="/" />;
  }

  return (
    <>
      <Container component="main" maxWidth="xs">
        <Box
          sx={{
            marginTop: 8,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Typography component="h1" variant="h5">
            Sign in
          </Typography>
          <Box
            component="form"
            onSubmit={formik.handleSubmit}
            noValidate
            sx={{ mt: 1 }}
          >
            <Box sx={{ mt: 1 }}>
              <TextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                id="email"
                label="Email Address"
                name="email"
                autoComplete="email"
                autoFocus
                value={formik.values.email}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                error={formik.touched.email && Boolean(formik.errors.email)}
                helperText={formik.touched.email && formik.errors.email}
              />
              <TextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="current-password"
                value={formik.values.password}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                error={
                  formik.touched.password && Boolean(formik.errors.password)
                }
                helperText={formik.touched.password && formik.errors.password}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                color="primary"
                disabled={loading}
              >
                Sign In
              </Button>
              <Box display="flex" justifyContent="space-between">
                <Link
                  component={RouterLink}
                  to="/forgot-password"
                  variant="body2"
                >
                  Forgot password?
                </Link>
                <Link component={RouterLink} to="/sign-up" variant="body2">
                  {"Don't have an account? Sign Up"}
                </Link>
              </Box>
            </Box>
            <Snackbar
              open={snackbar.open}
              anchorOrigin={{ vertical: "top", horizontal: "center" }}
              autoHideDuration={6000}
              onClose={() => setSnackbar({ ...snackbar, open: false })}
            >
              <Alert
                onClose={() => setSnackbar({ ...snackbar, open: false })}
                severity={snackbar.severity}
                sx={{ width: "100%" }}
              >
                {snackbar.message}
              </Alert>
            </Snackbar>
          </Box>
        </Box>
      </Container>
      <footer
        style={{
          height: "50px",
          background: "#f1f1f1",
          textAlign: "center",
          position: "fixed",
          bottom: 0,
          left: 0,
          right: 0,
          zIndex: 2,
          borderTop: "1px solid rgba(0, 0, 0, 0.12)",
        }}
      >
        <p style={{ margin: "15px 0" }}>
          System powstał na Wydziale Informatyki Politechniki Białostockiej
        </p>
      </footer>
    </>
  );
};

export default SignIn;

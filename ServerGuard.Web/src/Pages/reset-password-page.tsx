import React, { useState, useEffect } from "react";
import {
  TextField,
  Button,
  Container,
  Typography,
  Box,
  Alert,
} from "@mui/material";
import { useFormik } from "formik";
import * as Yup from "yup";
import { useNavigate, useSearchParams } from "react-router-dom";
import { resetPassword } from "../api/users-service";

const ResetPasswordPage: React.FC = () => {
  const [passwordReset, setPasswordReset] = useState(false);
  const [tokenValid, setTokenValid] = useState(true);
  const navigate = useNavigate();
  const [token, setToken] = useSearchParams();

  useEffect(() => {
    // Simulate token validation
    if (!token) {
      setTokenValid(false);
    }
  }, [token]);

  const formik = useFormik({
    initialValues: {
      password: "",
      confirmPassword: "",
    },
    validationSchema: Yup.object({
      password: Yup.string()
        .min(8, "Password must be at least 8 characters")
        .required("Required"),
      confirmPassword: Yup.string()
        .oneOf([Yup.ref("password"), undefined], "Passwords must match")
        .required("Required"),
    }),
    onSubmit: async (values) => {
      try {
        const result = await resetPassword(
          token.get("token")!,
          values.password
        );
        if (!result) {
          setTokenValid(false);
          return;
        }
        setPasswordReset(true);
      } catch (error) {
        setTokenValid(false);
      }
    },
  });

  if (!tokenValid) {
    return (
      <Container maxWidth="sm">
        <Box mt={5}>
          <Alert severity="error">Invalid or expired token.</Alert>
          <Button
            color="primary"
            variant="contained"
            fullWidth
            onClick={() => navigate("/forgot-password")}
            style={{ marginTop: "16px" }}
          >
            Go to Forgot Password
          </Button>
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="sm">
      <Box mt={5}>
        <Typography variant="h4" gutterBottom>
          Reset Password
        </Typography>
        {passwordReset ? (
          <>
            <Alert severity="success">
              Your password has been reset successfully.
            </Alert>
            <Button
              color="primary"
              variant="contained"
              fullWidth
              onClick={() => navigate("/sign-in")}
              style={{ marginTop: "16px" }}
            >
              Go to Sign In
            </Button>
          </>
        ) : (
          <form onSubmit={formik.handleSubmit}>
            <TextField
              fullWidth
              id="password"
              name="password"
              label="New Password"
              type="password"
              value={formik.values.password}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.password && Boolean(formik.errors.password)}
              helperText={formik.touched.password && formik.errors.password}
              margin="normal"
            />
            <TextField
              fullWidth
              id="confirmPassword"
              name="confirmPassword"
              label="Confirm New Password"
              type="password"
              value={formik.values.confirmPassword}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={
                formik.touched.confirmPassword &&
                Boolean(formik.errors.confirmPassword)
              }
              helperText={
                formik.touched.confirmPassword && formik.errors.confirmPassword
              }
              margin="normal"
            />
            <Button color="primary" variant="contained" fullWidth type="submit">
              Reset Password
            </Button>
          </form>
        )}
      </Box>
    </Container>
  );
};

export default ResetPasswordPage;

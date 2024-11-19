import React, { useState } from "react";
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
import { useNavigate } from "react-router-dom";
import { forgotPassword } from "../api/users-service";

const ForgotPasswordPage: React.FC = () => {
  const [emailSent, setEmailSent] = useState(false);
  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: {
      email: "",
    },
    validationSchema: Yup.object({
      email: Yup.string().email("Invalid email address").required("Required"),
    }),
    onSubmit: async (values) => {
      await forgotPassword(values.email);
      setTimeout(() => {
        setEmailSent(true);
      }, 1000);
    },
  });

  return (
    <Container maxWidth="sm">
      <Box mt={5}>
        <Typography variant="h4" gutterBottom>
          Forgot Password
        </Typography>
        {emailSent ? (
          <>
            <Alert severity="success">
              An email has been sent to reset your password.
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
              id="email"
              name="email"
              label="Email"
              value={formik.values.email}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.email && Boolean(formik.errors.email)}
              helperText={formik.touched.email && formik.errors.email}
              margin="normal"
            />
            <Button color="primary" variant="contained" fullWidth type="submit">
              Send Reset Link
            </Button>
          </form>
        )}
      </Box>
    </Container>
  );
};

export default ForgotPasswordPage;

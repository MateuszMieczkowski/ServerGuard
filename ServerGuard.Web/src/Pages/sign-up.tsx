import { useState } from "react";
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  Snackbar,
  Alert,
} from "@mui/material";
import { register } from "../api/auth-service";
import { Navigate } from "react-router-dom";

const SignUpPage = () => {
  const [loading, setLoading] = useState(false);
  const [signUpSuccess, setSignUpSuccess] = useState<boolean | null>(null);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "success" as "success" | "error",
  });

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    const data = new FormData(event.currentTarget);
    const email = data.get("email") as string;
    const password = data.get("password") as string;
    const confirmPassword = data.get("confirmPassword") as string;
    if (password !== confirmPassword) {
      setSignUpSuccess(null);
      setSnackbar({
        open: true,
        message: "Passwords do not match",
        severity: "error",
      });
      setLoading(false);
      return;
    }

    try {
      const registerSuccess = await register({ email, password });
      setSignUpSuccess(registerSuccess);
      if (!registerSuccess) {
        setSnackbar({
          open: true,
          message: "Sign-up failed. Please try again.",
          severity: "error",
        });
      } else {
        setSnackbar({
          open: true,
          message: "Sign-up successful!",
          severity: "success",
        });
      }
    } catch (error) {
      setSignUpSuccess(false);
      setSnackbar({
        open: true,
        message: "Sign-up failed. Please try again.",
        severity: "error",
      });
    } finally {
      setLoading(false);
    }
  };

  if (signUpSuccess === true) {
    return <Navigate to="/sign-in" />;
  }

  return (
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
          Sign Up
        </Typography>
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
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
          />
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            name="confirmPassword"
            label="Confirm Password"
            type="password"
            id="confirmPassword"
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            disabled={loading}
          >
            Sign Up
          </Button>
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
    </Container>
  );
};

export default SignUpPage;

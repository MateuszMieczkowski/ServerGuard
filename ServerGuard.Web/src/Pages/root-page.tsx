import {
  createTheme,
  CssBaseline,
  ThemeProvider,
  Typography,
} from "@mui/material";
import { Navigate } from "react-router-dom";

const RootPage = () => {
  const isAuthenticated = false;
  if (!isAuthenticated) {
    return <Navigate to="/sign-in" />;
  }
  return <Typography variant="h1">Root page</Typography>;
};

export default RootPage;

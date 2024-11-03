import { StrictMode, useEffect } from "react";
import { createRoot } from "react-dom/client";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import RootPage from "./Pages/root-page.tsx";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import ErrorPage from "./Pages/error-page.tsx";
import SignInPage from "./Pages/sign-in.tsx";
import { ThemeProvider } from "@emotion/react";
import { createTheme, CssBaseline } from "@mui/material";
import SignUpPage from "./Pages/sign-up.tsx";
import AgentsPage from "./Pages/agents-page.tsx";
import DashboardsPage from "./Pages/dashboards-page.tsx";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import utc from "dayjs/plugin/utc";
import dayjs from "dayjs";

const router = createBrowserRouter([
  {
    path: "/",
    element: <RootPage />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: "/resourceGroups/:resourceGroupId/agents",
        element: <AgentsPage />,
      },
      {
        path: "/resourceGroups/:resourceGroupId/agents/:agentId/dashboards",
        element: <DashboardsPage />,
      },
    ],
  },
  {
    path: "/sign-in",
    element: <SignInPage />,
  },
  {
    path: "/sign-up",
    element: <SignUpPage />,
  },
]);

const theme = createTheme({
  palette: {
    mode: "light",
  },
});

dayjs.extend(utc);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <RouterProvider router={router} />
      </ThemeProvider>
    </LocalizationProvider>
  </StrictMode>
);

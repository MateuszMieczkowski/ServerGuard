import { Container, Box, Typography } from "@mui/material";
import { useRouteError } from "react-router-dom";

const ErrorPage = () => {
  const error: any = useRouteError();
  console.error(error);
  return (
    <Container>
      <Box
        display="flex"
        flexDirection="column"
        justifyContent="center"
        alignItems="center"
        height="100vh"
      >
        <Typography variant="h1">404</Typography>
        <Typography variant="h6">Page not found</Typography>
        <Typography variant="body1">
          {error.statusText || error.message}
        </Typography>
      </Box>
    </Container>
  );
};

export default ErrorPage;

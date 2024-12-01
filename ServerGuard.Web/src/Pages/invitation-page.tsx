import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import {
  acceptInvitation,
  getInvitation,
  Invitation,
} from "../api/resource-group-service";
import { useEffect, useState } from "react";
import { Box, Button, Card, Container, Typography } from "@mui/material";

export const InvitationPage = () => {
  const { resourceGroupId } = useParams();
  const [searchParams] = useSearchParams();
  const [invitation, setInvitation] = useState<Invitation | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (!resourceGroupId) {
      return;
    }
    if (!searchParams.get("token")) {
      navigate("/");
    }
    getInvitation(resourceGroupId, searchParams.get("token")!).then(
      (response) => {
        setInvitation(response);
      }
    );
  }, [resourceGroupId, searchParams]);

  const accept = async () => {
    if (!resourceGroupId) {
      return;
    }
    if (!searchParams.get("token")) {
      navigate("/");
    }
    try {
      await acceptInvitation(resourceGroupId, searchParams.get("token")!);
      navigate(`/`);
      window.location.reload();
    } catch (error) {
      console.error("Error accepting invitation:", error);
    }
  };

  const decline = () => {
    navigate("/");
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ mt: 8, mb: 4 }}>
        <Card sx={{ p: 4 }}>
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              gap: 3,
            }}
          >
            <Typography variant="h5" component="h1" gutterBottom>
              Resource Group Invitation
            </Typography>

            <Box sx={{ my: 2, textAlign: "center" }}>
              {invitation ? (
                <Typography variant="body1">
                  You have been invited to join the resource group{" "}
                  <Typography component="span" fontWeight="bold">
                    {invitation.resourceGroupName}
                  </Typography>{" "}
                  as{" "}
                  <Typography component="span" fontWeight="bold">
                    {invitation.role}
                  </Typography>
                  .
                </Typography>
              ) : (
                <Typography variant="body1" color="text.secondary">
                  Loading invitation details...
                </Typography>
              )}
            </Box>

            <Box sx={{ display: "flex", gap: 2, mt: 2 }}>
              <Button
                variant="contained"
                color="primary"
                size="large"
                sx={{ minWidth: 120 }}
                onClick={accept}
              >
                Accept
              </Button>
              <Button
                variant="outlined"
                color="error"
                size="large"
                sx={{ minWidth: 120 }}
                onClick={decline}
              >
                Decline
              </Button>
            </Box>
          </Box>
        </Card>
      </Box>
    </Container>
  );
};

import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Link,
} from "@mui/material";

interface DownloadAgentDialogProps {
  open: boolean;
  setOpen: (open: boolean) => void;
}

const DownloadAgentDialog: React.FC<DownloadAgentDialogProps> = ({
  open,
  setOpen,
}) => {
  const handleClose = async () => {
    setOpen(false);
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>Download Agent</DialogTitle>
      <DialogContent>
        <DialogActions
          sx={{ display: "flex", justifyContent: "space-between" }}
        >
          <Link
            href="https://github.com/MateuszMieczkowski/ServerGuard/releases/download/0.1.0/agent_win_0.1.0.zip"
            color="primary"
            onClick={handleClose}
          >
            Windows
          </Link>
          <Link
            href="https://github.com/MateuszMieczkowski/ServerGuard/releases/download/0.1.0/agent_linux_0.1.0.zip"
            color="primary"
            onClick={handleClose}
          >
            Linux
          </Link>
        </DialogActions>
      </DialogContent>
    </Dialog>
  );
};

export default DownloadAgentDialog;

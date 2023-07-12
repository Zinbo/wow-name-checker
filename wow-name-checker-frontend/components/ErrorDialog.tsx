import {Dialog, DialogContent, DialogContentText, DialogTitle, IconButton, Typography} from "@mui/material";
import {CloseIcon} from "next/dist/client/components/react-dev-overlay/internal/icons/CloseIcon";
import React from "react";

interface Props {
    hasError: boolean
    setHasError ?: (hasError: boolean) => void
    errorTitle: string
}
export default ({hasError, setHasError, errorTitle}: Props) => {
    return (<Dialog
        open={hasError}
        onClose={setHasError ? () => setHasError(false) : () => {}}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
        PaperProps={{
            style: {
                backgroundColor: 'white',
                borderRadius: 0,
                border: '2px solid black',
            },
        }}
    >
        <DialogTitle id="alert-dialog-title" color='black' sx={{borderBottom: '1px solid black'}}>
            <Typography sx={{mr: 5, fontWeight: 'bold'}}>
                {errorTitle}
            </Typography>
            {setHasError && <IconButton
                aria-label="close"
                onClick={() => setHasError(false)}
                sx={{
                    position: 'absolute',
                    right: 8,
                    top: 4,
                    color: (theme) => theme.palette.grey[500],
                }}
            >
                <CloseIcon/>
            </IconButton>}
        </DialogTitle>
        <DialogContent>
            <DialogContentText id="alert-dialog-description" color='black' sx={{mt: 2}}>
                Please try again later.
            </DialogContentText>
        </DialogContent>
    </Dialog>)
}
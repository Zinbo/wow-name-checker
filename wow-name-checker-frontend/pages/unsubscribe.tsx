import {useRouter} from "next/router";
import React from "react";
import {
    Backdrop,
    Button, CircularProgress,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle, IconButton,
    TextField,
    Typography
} from "@mui/material";
import Box from "@mui/material/Box";
import WoWBox from "@/components/WoWBox";
import {CloseIcon} from "next/dist/client/components/react-dev-overlay/internal/icons/CloseIcon";
import * as yup from "yup";
import {useFormik} from "formik";

export default function Results() {
    const [hasUnsubscribed, setHasUnsubscribed] = React.useState(false);
    const [hasError, setHasError] = React.useState(false);
    const [isLoading, setIsLoading] = React.useState(false);

    const unsubscribe = async (email: string) => {
        setIsLoading(true);
        const res = await fetch(`/alert/unsubscribe`, {
            method: 'POST',
            body: JSON.stringify({email})
        });
        if (res.ok) {
            // Change email box to unsubscribed text
            setHasUnsubscribed(true);
            setIsLoading(false);
        } else {
            setHasError(true);
            setIsLoading(false);
        }
    };

    const validationSchema = yup.object({
        email: yup.string()
            .email('Enter a valid email')
            .required('Email is required')
    })

    const formik = useFormik({
            initialValues: {
                email: ''
            },
            validationSchema: validationSchema,
            onSubmit: async (values) => {
                await unsubscribe(values.email);
            },
        });

    const LoadingScreen = () => (
        <Backdrop
            sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
            open={isLoading}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    );

    const ErrorDialog = () => {
        return (<Dialog
            open={hasError}
            onClose={() => setHasError(false)}
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
                    Could not unsubscribe email
                </Typography>
                <IconButton
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
                </IconButton>
            </DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description" color='black' sx={{mt: 2}}>
                    Please try again later.
                </DialogContentText>
            </DialogContent>
        </Dialog>)
    }

    return (
        <WoWBox>
            <LoadingScreen/>
            <Typography variant="h1" gutterBottom color='text.secondary'
                        sx={{fontWeight: 'bold', fontSize: '40px', alignSelf: 'center', textAlign: 'center'}}>
                Do you want to unsubscribe?
            </Typography>
            <Typography variant="h2" gutterBottom color='text.secondary' sx={{fontSize: '36px', alignSelf: 'center'}}>
                Enter your email address to unsubscribe
            </Typography>
            <Box sx={{flex: 1, display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 2}}>
                {hasUnsubscribed && <Typography variant="h2" gutterBottom sx={{fontSize: '36px', alignSelf: 'center'}}>Unsubscribed
                    successfully!</Typography>}
                {!hasUnsubscribed && (
                    <form onSubmit={formik.handleSubmit} style={{flex: 1, display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                        <TextField id="email"
                                   name="email"
                                   label="Email"
                                   variant="outlined"
                                   sx={{mr: 2}}
                                   color='secondary'
                                   value={formik.values.email}
                                   type={'email'}
                                   onChange={formik.handleChange}
                                   onBlur={formik.handleBlur}
                                   error={formik.touched.email && Boolean(formik.errors.email)}
                                   helperText={formik.touched.email && formik.errors.email}
                                   disabled={isLoading}
                        />
{/*                        <TextField
                            fullWidth
                            id="email"
                            name="email"
                            label="Email"
                            value={formik.values.email}
                            onChange={formik.handleChange}
                            onBlur={formik.handleBlur}
                            error={formik.touched.email && Boolean(formik.errors.email)}
                            helperText={formik.touched.email && formik.errors.email}
                        />*/}
                        <Button color='secondary' variant="contained" type="submit" disabled={isLoading}>Unsubscribe</Button>
                    </form>)}
                {hasError && <ErrorDialog/>}
            </Box>
        </WoWBox>
    );
}
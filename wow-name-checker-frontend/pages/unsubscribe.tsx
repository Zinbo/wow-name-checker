import React from "react";
import {Backdrop, Button, CircularProgress, TextField, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import WoWBox from "@/components/BoxContainer";
import * as yup from "yup";
import {useFormik} from "formik";
import ErrorDialog from "@/components/ErrorDialog";
import SubscriptionContainer from "@/components/SubscriptionContainer";

export default function Results() {
    const [hasUnsubscribed, setHasUnsubscribed] = React.useState(false);
    const [hasError, setHasError] = React.useState(false);
    const [isLoading, setIsLoading] = React.useState(false);

    const unsubscribe = async (email: string) => {
        setIsLoading(true);
        const res = await fetch(`/alert/unsubscribe`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
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

    return (
        <SubscriptionContainer title="Do you want to unsubscribe?"
                               subTitle="Enter your email address to unsubscribe" actionName="Unsubscribe"
                               url="/alert/unsubscribe"/>
    );
}
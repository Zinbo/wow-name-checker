import Box from "@mui/material/Box";
import React from "react";
import {Button, TextField, Typography} from "@mui/material";
import BoxContainer from "@/components/BoxContainer";
import * as yup from "yup";
import {useFormik} from "formik";
import ErrorDialog from "@/components/ErrorDialog";
import LoadingScreen from "@/components/LoadingScreen";

interface Props {
    title: string
    subTitle: string
    actionName: string
    dto?: object
    url: string
}

export default ({title, subTitle, actionName, dto, url}: Props) => {
    const [actionCompleted, setActionCompleted] = React.useState(false);
    const [hasError, setHasError] = React.useState(false);
    const [isLoading, setIsLoading] = React.useState(false);

    const action = async (email: string) => {
        setIsLoading(true);
        const res = await fetch(url, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({email, ...(dto || {})})
        });
        if (res.ok) {
            setActionCompleted(true);
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
            await action(values.email);
        },
    });

    return (
        <BoxContainer>
            <LoadingScreen isLoading={isLoading}/>
            <Typography variant="h1" gutterBottom color='text.secondary'
                        sx={{fontWeight: 'bold', fontSize: '40px', alignSelf: 'center', textAlign: 'center'}}>
                {title}
            </Typography>
            <Typography variant="h2" gutterBottom color='text.secondary' sx={{fontSize: '36px', alignSelf: 'center'}}>
                {subTitle}
            </Typography>


            <Box sx={{flex: 1, display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 2}}>
                {actionCompleted && <Typography variant="h2" gutterBottom sx={{
                    fontSize: '36px',
                    alignSelf: 'center'
                }}>{actionName}d successfully!</Typography>}
                {!actionCompleted && (
                    <form onSubmit={formik.handleSubmit}
                          style={{flex: 1, display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
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
                        <Button color='secondary' variant="contained" type="submit"
                                disabled={isLoading}>{actionName}</Button>
                    </form>)}
                {hasError && <ErrorDialog hasError={hasError} setHasError={setHasError} errorTitle={`Could not ${actionName.toLowerCase()} email`}/>}
            </Box>
        </BoxContainer>);
}
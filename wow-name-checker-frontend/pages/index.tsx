import React from "react";
import getServers, {Server} from "@/lib/realms";
import {useRouter} from "next/router";
import BoxContainer from "@/components/BoxContainer";
import {Autocomplete, Button, TextField, Typography} from "@mui/material";
import lifecraftFont from "@/utils/lifecraftFont";
import Box from "@mui/material/Box";
import * as yup from "yup";
import {useFormik} from "formik";
import {styled} from "@mui/system";

export async function getStaticProps() {
    const servers = getServers();
    return {
        props: {
            servers
        },
    };
}

interface Props {
    servers: Server[]
}

interface FormValues {
    name: string
    server: Server | null
}

const Form = styled('form')({
    flex: 1, display: 'flex', flexDirection: 'column'
})

export default ({servers}: Props) => {
    const router = useRouter();


    const goToNamePage = (name: string, server: Server) => {
        router.push(`/check-name?name=${name}&region=${server?.region}&realm=${server?.realm}`);
    };

    const validationSchema = yup.object({
        name: yup.string()
            .required('Character name is required')
            .min(2),
        server: yup.object({
            realm: yup.string().required(),
            region: yup.string().required()
        }).required('Server is required')
    })

    const initialValues: FormValues = {
        name: '',
        server: null
    }

    const formik = useFormik({
        initialValues,
        enableReinitialize: true,
        validationSchema: validationSchema,
        onSubmit: async (values) => {
            console.log("Submitting!")
            await goToNamePage(values.name, values.server as Server);
        },
    });

    return (
        <BoxContainer>
            <Typography variant="h1" sx={{alignSelf: 'center', textAlign: 'center'}}
                        className={lifecraftFont.className} gutterBottom>
                WoW Name Checker
            </Typography>

            <Form onSubmit={formik.handleSubmit}>
                <Box sx={{
                    flex: 1,
                    display: 'flex',
                    justifyContent: 'center',
                    mb: 3,
                    mx: 2
                }}>
                    <TextField id="name"
                               name="name"
                               label="Character Name"
                               variant="outlined"
                               sx={{mr: 2, flex: 1}}
                               color='secondary'
                               value={formik.values.name}
                               onChange={formik.handleChange}
                               onBlur={formik.handleBlur}
                               error={formik.touched.name && Boolean(formik.errors.name)}
                               helperText={formik.touched.name && formik.errors.name}
                    />


                    <Autocomplete
                        id='server'
                        color='secondary'
                        options={servers}
                        groupBy={(option) => option.region}
                        getOptionLabel={(option) => `${option.realm} (${option.region})`}
                        value={formik.values.server}
                        onChange={(event: any, newValue: Server | null) => formik.setFieldValue('server', newValue)}
                        sx={{width: '50%', flex: 1}}
                        onBlur={formik.handleBlur}
                        renderInput={(params) => <TextField name="server" {...params} label="Realm"
                                                            style={{width: "100%"}}
                                                            error={formik.touched.server && Boolean(formik.errors.server)}
                                                            helperText={formik.touched.server && formik.errors.server}/>}
                    />
                </Box>
                <Button id="submit" color='secondary' variant="contained" type="submit" sx={{alignSelf: 'center'}}>Search</Button>
            </Form>

        </BoxContainer>
    )
}
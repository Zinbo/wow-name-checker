// pages/index.tsx
import React from "react";
import {Autocomplete, Button, TextField, Typography} from "@mui/material";
import {styled} from "@mui/system";
import Head from "next/head";
import getServers, {Server} from "../lib/realms";
import {useRouter} from "next/router";

// Define some regions for the autocomplete dropdown

// Style the page container to look like webuyanycar.com
const PageContainer = styled("div")({
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    height: "100vh",
    mt: 1,
    mb: 1
});


// Style the form container to look like webuyanycar.com
const FormContainer = styled("div")({
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "space-between",
    width: "600px",
    height: "400px",
    padding: "32px",
    borderRadius: "8px",
});

// Style the input fields to look like webuyanycar.com
const InputField = styled(TextField)({
    width: "80%",
});

// Style the submit button to look like webuyanycar.com
const SubmitButton = styled(Button)({
    width: "80%",
    height: "56px",
});

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

export default function Home({servers}: Props) {

    // Use React hooks to manage state for name, realm and region inputs
    const [name, setName] = React.useState("");
    const [server, setServer] = React.useState<Server|null>(null);
    const router = useRouter();

    // Handle click event for submit button
    const handleSubmitClick = () => {
        // @ts-ignore
        router.push(`/check-name?name=${name}&region=${server?.region}&realm=${server?.realm}`);
    };

    return (
        <PageContainer>
            <Head>
                <title>Create Next App</title>
                <meta name="description" content="Check if a name is available in World of Warcraft"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <link rel="icon" href="/favicon.ico"/>
            </Head>
            <Typography variant="h1">WoW Name Checker</Typography>
            <FormContainer>
                <InputField label="Character Name" value={name} onChange={e => setName(e.target.value)}/>

                <Autocomplete
                    options={servers}
                    groupBy={(option) => option.region}
                    getOptionLabel={(option) => `${option.realm} (${option.region})`}
                    value={server}
                    onChange={(event: any, newValue: Server|null) => setServer(newValue)}
                    style={{width: "80%"}}
                    renderInput={(params) => <TextField {...params} label="Realm" style={{width: "100%"}}/>}
                />

                <SubmitButton variant="contained" onClick={handleSubmitClick}>
                    Check Name
                </SubmitButton>
            </FormContainer>
        </PageContainer>
    );
}

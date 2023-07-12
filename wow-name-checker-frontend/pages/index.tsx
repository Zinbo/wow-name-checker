import React from "react";
import getServers, {Server} from "@/lib/realms";
import {useRouter} from "next/router";
import BoxContainer from "@/components/BoxContainer";
import {Autocomplete, Button, TextField, Typography} from "@mui/material";
import lifecraftFont from "@/utils/lifecraftFont";
import Box from "@mui/material/Box";

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

export default ({servers}: Props) => {

    const [name, setName] = React.useState("");
    const [server, setServer] = React.useState<Server | null>(null);
    const router = useRouter();

    const handleSubmitClick = () => {
        // @ts-ignore
        router.push(`/check-name?name=${name}&region=${server?.region}&realm=${server?.realm}`);
    };

    return (
        <BoxContainer>
            <Typography variant="h1" sx={{alignSelf: 'center', textAlign: 'center'}}
                        className={lifecraftFont.className} gutterBottom>
                WoW Name Checker
            </Typography>
            <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', mb: 2, ml: 2, mr: 2}}>
                <TextField id="outlined-basic" label="Character Name" variant="outlined" sx={{mr: 2, flex: 1}}
                           color='secondary' value={name} onChange={e => setName(e.target.value)}/>
                <Autocomplete
                    color='secondary'
                    options={servers}
                    groupBy={(option) => option.region}
                    getOptionLabel={(option) => `${option.realm} (${option.region})`}
                    value={server}
                    onChange={(event: any, newValue: Server | null) => setServer(newValue)}
                    style={{width: "50%", flex: 1}}
                    renderInput={(params) => <TextField {...params} label="Realm" style={{width: "100%"}}/>}
                />
            </Box>
            <Button color='secondary' variant="contained" sx={{alignSelf: 'center'}}
                    onClick={handleSubmitClick}>Search</Button>
        </BoxContainer>
    )
}
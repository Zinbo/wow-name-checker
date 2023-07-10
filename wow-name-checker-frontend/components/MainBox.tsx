import Box from "@mui/material/Box";
import React from "react";
import {Button, TextField, Typography} from "@mui/material";
import WoWBox from "@/components/WoWBox";

export default () => {

    return (<WoWBox>
        <Typography variant="h1" gutterBottom color='text.secondary' sx={{ fontWeight: 'bold', fontSize: '40px', alignSelf: 'center', textAlign: 'center'}} >
            That name is taken!
        </Typography>
        <Typography variant="h2" gutterBottom color='text.secondary' sx={{ fontSize: '36px', alignSelf: 'center'}}>
            Do you want to get an email when it becomes available?
        </Typography>
        <Box sx={{flex: 1, display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 2}}>
            <TextField id="outlined-basic" label="Email" variant="outlined" sx={{mr: 2}}
                       color='secondary' />
            <Button color='secondary' variant="contained">Subscribe</Button>
        </Box>
    </WoWBox>);
}
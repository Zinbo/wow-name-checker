import Box from "@mui/material/Box";
import React from "react";
import {Button, TextField, Typography} from "@mui/material";

export default () => {

    return (<Box
        sx={{
            display: 'flex',
            flex: 1,
        }}
    >
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                flex: 1,
                alignSelf: 'center',
                position: 'relative',
                border: '2px solid #F8B700',
                p: 2
            }}
        >
            <Typography variant="h1" gutterBottom>
                That name has been taken!
            </Typography>
            <Typography variant="h2" gutterBottom>
                Do you want to get an email when it becomes available?
            </Typography>
            <Box sx={{ flex: 1,display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                <TextField id="outlined-basic" label="Email" variant="outlined" sx={{mr: 2}} />
                <Button variant="outlined">Subscribe</Button>
            </Box>

            <div style={{
                zIndex: '-1',
                backgroundColor: '#183685',
                border: '2px solid #F8B700',
                position: 'absolute',
                top: '5px',
                bottom: '-9px',
                left: '5px',
                right: '-9px'
            }}
            />
            <div style={{
                zIndex: '-2',
                backgroundColor: '#74000D',
                border: '2px solid #F8B700',
                position: 'absolute',
                top: '10px',
                bottom: '-16px',
                left: '10px',
                right: '-16px'
            }}
            />
        </Box>

    </Box>);
}
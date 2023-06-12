import Box from "@mui/material/Box";
import React from "react";
import {Button, FormControl, InputLabel, MenuItem, Paper, Select, TextField, Typography} from "@mui/material";

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
            bgcolor='background.paper'
        >
            <Typography variant="h1" gutterBottom sx={{alignSelf: 'center', textAlign: 'center'}}>
                WoW Name Checker
            </Typography>
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                <TextField id="outlined-basic" label="Character Name" variant="outlined" sx={{mr: 2}}  color='secondary' />
                <FormControl  sx={{m: 1, minWidth: 209}}>
                    <InputLabel id="demo-simple-select-label" color='secondary' >Realm</InputLabel>
                    <Select
                        labelId="demo-simple-select-label"
                        id="demo-simple-select"
                        label="Age"
                    >
                        <MenuItem value={10}>Ten</MenuItem>
                        <MenuItem value={20}>Twenty</MenuItem>
                        <MenuItem value={30}>Thirty</MenuItem>
                    </Select>
                </FormControl>
            </Box>
            <Button color='secondary' variant="contained" sx={{alignSelf: 'center'}}>Search</Button>

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
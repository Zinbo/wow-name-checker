import {AppBar, Button, Toolbar, Typography} from "@mui/material";
import React from "react";

export default () => (<AppBar position="static" sx={{borderBottom: '2px solid #F8B700'}}>
    <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            WoW Name Checker
        </Typography>
        <Button color="inherit">Blog</Button>
        <Button color="inherit">About</Button>
    </Toolbar>
</AppBar>)
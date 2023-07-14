import {AppBar, Toolbar, Typography} from "@mui/material";
import React from "react";
import lifecraftFont from "@/utils/lifecraftFont";
import Link from "next/link";

export default () => (<AppBar position="static" sx={{borderBottom: '2px solid #F8B700'}}>
    <Toolbar>
        <Typography variant="h6" component="div"
                    sx={{
                        flexGrow: 1,
                        fontSize: '40px'
                    }}
                    className={lifecraftFont.className} color={"text.primary"}>
            <Link href={"/"} style={{ textDecoration: 'none', color: 'inherit' }}>WoW Name Checker</Link>
        </Typography>
    </Toolbar>
</AppBar>)
import * as React from 'react';
import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import Toolbar from '@mui/material/Toolbar';
import {Typography} from '@mui/material';
import Link from "next/link";

export default function BottomAppBar() {
    return (
        <React.Fragment>
            <CssBaseline />
            <Box bgcolor='primary.main' sx={{ top: 'auto', bottom: 0, borderTop: '2px solid #F8B700' }}>
                <Toolbar>
                    <Typography component='div' sx={{ mr: 1 }} color={'text.secondary'}>
                        Made with ❤️ in Nottingham by <Link href={"https://github.com/Zinbo"} style={{color: '#FFF'}}>Zinbo</Link>
                    </Typography>
                </Toolbar>
            </Box>
        </React.Fragment>
    );
}

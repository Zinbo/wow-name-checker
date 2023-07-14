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
                <Toolbar sx={{display: 'flex', flex: 1, justifyContent: 'space-between'}}>
                    <Typography color={'text.secondary'} sx={{fontSize: '12px'}}>icons by <Link style={{color: '#FFF'}} target="_blank" href="https://icons8.com">Icons8</Link></Typography>
                    <Typography component='div' sx={{ mr: 1 }} color={'text.secondary'}>
                        Made with ❤️ in Nottingham by <Link target="_blank" href={"https://github.com/Zinbo"} style={{color: '#FFF'}}>Zinbo</Link>
                    </Typography>
                    <Typography color={'text.secondary'} sx={{fontSize: '12px'}}><Link style={{color: '#FFF'}}  href="/privacy" target="_blank">Privacy Policy</Link></Typography>
                </Toolbar>
            </Box>
        </React.Fragment>
    );
}

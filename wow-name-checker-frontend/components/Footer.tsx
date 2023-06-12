import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import Toolbar from '@mui/material/Toolbar';

import FacebookIcon from '@mui/icons-material/Facebook';
import TwitterIcon from '@mui/icons-material/Twitter';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import { Typography } from '@mui/material';

export default function BottomAppBar() {
    return (
        <React.Fragment>
            <CssBaseline />
            <Box bgcolor='primary.main' sx={{ top: 'auto', bottom: 0, borderTop: '2px solid #F8B700' }}>
                <Toolbar>
                    <Typography component='div' sx={{ mr: 1 }}>
                        Made with ❤️ in Nottingham by Zinbo
                    </Typography>
                    <Box
                        sx={{
                            display: 'flex',
                            justifyContent: 'center',
                            flexGrow: 1,
                        }}
                    >
                    </Box>

                        <Typography component='div' sx={{ ml: 1 }}>
                            2022 All Rights Reserved
                        </Typography>
                </Toolbar>
            </Box>
        </React.Fragment>
    );
}

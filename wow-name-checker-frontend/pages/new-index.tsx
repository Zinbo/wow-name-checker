import {Container} from "@mui/system";
import {AppBar, Button, Toolbar, Typography} from "@mui/material";
import React from "react";
import Footer from "@/components/Footer";
import Header from "@/components/Header";
import Home from "@/components/Home";
import {createTheme, ThemeOptions, ThemeProvider} from '@mui/material/styles';

export default () => {

    const themeOptions: ThemeOptions = {
        palette: {
            mode: 'light',
            primary: {
                main: 'rgba(60,42,41,0.8)',
            },
            secondary: {
                main: '#f8b700',
            },
            background: {
                default: '#211510',
                paper: '#504137',
            },
            divider: '#f8b700',
            text: {
                secondary: 'rgba(255,255,255,0.87)',
                primary: '#f8b700',
            },
        },
    };

    const theme = createTheme(themeOptions);

    return (
        <ThemeProvider theme={theme}>
            <div style={{minHeight: '100vh', display: "flex", flexDirection: "column"}}>

                <Header/>
                <Container sx={{display: "flex", flexDirection: "column", flex: 1}}>
                    <Home/>
                </Container>
                <Footer/>
            </div>
        </ThemeProvider>
    )
}
import type {AppProps} from 'next/app'
import React from "react";
import {ThemeProvider} from '@mui/material/styles';
import {theme} from '@/utils/theme';
import {CssBaseline} from "@mui/material";
import { CacheProvider, EmotionCache } from '@emotion/react';
import createEmotionCache from "@/utils/createEmotionCache";
import Header from "@/components/Header";
import {Container} from "@mui/system";
import Home from "@/components/Home";
import Footer from "@/components/Footer";

// Client-side cache, shared for the whole session of the user in the browser.
const clientSideEmotionCache = createEmotionCache();

interface MyAppProps extends AppProps {
    emotionCache?: EmotionCache;
}

export default function MyApp(props: MyAppProps) {
    const { Component, emotionCache = clientSideEmotionCache, pageProps } = props;
    return (
        <CacheProvider value={emotionCache}>
            <ThemeProvider theme={theme}>
                <CssBaseline/>
                <div style={{minHeight: '100vh', display: "flex", flexDirection: "column"}}>
                    <Header/>
                    <Container sx={{display: "flex", flexDirection: "column", flex: 1}}>
                        <Component {...pageProps} />
                    </Container>
                    <Footer/>
                </div>
            </ThemeProvider>
        </CacheProvider>
    )
}

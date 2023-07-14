import type {AppProps} from 'next/app'
import React from "react";
import {ThemeProvider} from '@mui/material/styles';
import {theme} from '@/utils/theme';
import {CssBaseline} from "@mui/material";
import { CacheProvider, EmotionCache } from '@emotion/react';
import createEmotionCache from "@/utils/createEmotionCache";
import Header from "@/components/Header";
import {Container, styled} from "@mui/system";
import Footer from "@/components/Footer";

// Client-side cache, shared for the whole session of the user in the browser.
const clientSideEmotionCache = createEmotionCache();

interface MyAppProps extends AppProps {
    emotionCache?: EmotionCache;
}

const RootContainer = styled('div')({
    minHeight: '100vh', display: "flex", flexDirection: "column"
})

export default function MyApp(props: MyAppProps) {
    const { Component, emotionCache = clientSideEmotionCache, pageProps } = props;
    return (
        <CacheProvider value={emotionCache}>
            <ThemeProvider theme={theme}>
                <CssBaseline/>
                <RootContainer>
                    <Header/>
                    <Container sx={{display: "flex", flexDirection: "column", flex: 1}}>
                        <Component {...pageProps} />
                    </Container>
                    <Footer/>
                </RootContainer>
            </ThemeProvider>
        </CacheProvider>
    )
}

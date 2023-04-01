import type {AppProps} from 'next/app'
import React from "react";
import {ThemeProvider} from '@mui/material/styles';
import {theme} from '@/utils/theme';
import {CssBaseline} from "@mui/material";
import { CacheProvider, EmotionCache } from '@emotion/react';
import createEmotionCache from "@/utils/createEmotionCache";

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
                <Component {...pageProps} />
            </ThemeProvider>
        </CacheProvider>
    )
}

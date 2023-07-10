import {createTheme, ThemeOptions} from '@mui/material/styles';
import {Epilogue} from "@next/font/google";

const epilogue = Epilogue({
    weight: ["300", "400"],
    style: ["normal", "italic"],
    subsets: ["latin"],
});

export const theme = createTheme({
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
    typography: {
        fontFamily: epilogue.style.fontFamily
    },
});
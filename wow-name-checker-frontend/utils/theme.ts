import { createTheme } from '@mui/material/styles';


export const theme = createTheme({
    typography: {
        fontFamily: [
            '"Segoe UI"',
            'Roboto',
            '"Helvetica Neue"',
            'Arial',
            'sans-serif',
            '"Segoe UI Emoji"',
        ].join(','),
    },
    palette: {
        primary: {
            main: '#1976d2', // A blue color for primary elements
        },
        secondary: {
            main: '#f44336', // A red color for secondary elements
        },
    },
})
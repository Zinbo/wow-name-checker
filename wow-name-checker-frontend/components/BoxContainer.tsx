import React, {ReactNode} from "react";
import Box from "@mui/material/Box";

type Props = {
    children: ReactNode
}

export default ({children}: Props) => {

    return (
        <Box
        sx={{
            display: 'flex',
            flex: 1,
            justifyContent: 'center',
        }}
    >
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignSelf: 'center',
                position: 'relative',
                border: '2px solid #F8B700',
                flex: 1,
                // flexBasis: '90%',
                py: 10,
                px: 5
            }}
            bgcolor='background.paper'
        >
            {children}

            <div style={{
                zIndex: '-1',
                backgroundColor: '#183685',
                border: '2px solid #F8B700',
                position: 'absolute',
                top: '5px',
                bottom: '-9px',
                left: '5px',
                right: '-9px'
            }}
            />
            <div style={{
                zIndex: '-2',
                backgroundColor: '#74000D',
                border: '2px solid #F8B700',
                position: 'absolute',
                top: '10px',
                bottom: '-16px',
                left: '10px',
                right: '-16px'
            }}
            />
        </Box>

    </Box>);
}
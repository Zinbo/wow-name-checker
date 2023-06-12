import Box from "@mui/material/Box";
import React from "react";
import HomeBox from "@/components/HomeBox";

export default () => {
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
            <HomeBox/>
        </Box>
    )
}
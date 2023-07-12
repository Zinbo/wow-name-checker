import React from "react";
import {Typography} from "@mui/material";
import BoxContainer from "@/components/BoxContainer";

interface Props {
    name: string,
    realm: string,
    region: string
}
export default ({name, realm, region}:Props) => (<BoxContainer>
        <Typography variant="h1" gutterBottom color='text.secondary'
                    sx={{fontWeight: 'bold', fontSize: '40px', alignSelf: 'center', textAlign: 'center'}}>
            {name} is available on {realm} ({region})!
        </Typography>
        <Typography variant="h2" gutterBottom color='text.secondary' sx={{fontSize: '36px', alignSelf: 'center'}}>
            Go get it quick!
        </Typography>
    </BoxContainer>)
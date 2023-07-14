import React, {useEffect} from "react";
import {Typography} from "@mui/material";
import BoxContainer from "@/components/BoxContainer";
import getQuotes from "@/lib/quotes";

export async function getStaticProps() {
    const quotes = getQuotes();
    return {
        props: {
            quotes
        },
    };
}

interface Props {
    quotes: string[]
}

export default ({quotes} : Props) => {

    const [quote, setQuote] = React.useState(quotes[0]);

    useEffect(() => {
        setQuote(quotes[Math.floor(Math.random() * quotes.length)]);
    }, []);

    return (<BoxContainer>
        <Typography variant="h1" gutterBottom color='text.secondary'
                    sx={{fontWeight: 'bold', fontSize: '40px', alignSelf: 'center', textAlign: 'center'}}>
            {quote}
        </Typography>
        <Typography variant="h2" gutterBottom color='text.secondary' sx={{fontSize: '36px', alignSelf: 'center'}}>
            Page could not be found.
        </Typography>
    </BoxContainer>)
}
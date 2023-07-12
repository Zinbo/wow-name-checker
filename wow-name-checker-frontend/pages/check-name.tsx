import {useRouter} from "next/router";
import React, {useEffect, useState} from "react";
import {Typography} from "@mui/material";
import NotAvailableBox from "@/components/NotAvailableBox";

export default function Results() {
    const router = useRouter();
    const {name, region, realm} = router.query;
    const [isAvailable, setIsAvailable] = useState<boolean | null>(null);
    const [error, setError] = useState(null);

    const callAPI = async () => {
        const res = await fetch(`/profile?name=${name}&region=${region}&realm=${realm}`);
        if (res.status === 404) setIsAvailable(true);
        else if (res.ok) {
            setIsAvailable(false);
        } else {
            const data = await res.json();
            setError(data);
            console.log(data);
        }
    };

    useEffect(() => {
        if(router.isReady && !!name && !!region && !!realm) callAPI();
    }, [router.isReady]);

    const Result = () => {
        if (!!error) return (<>
            Error! <br/>
            {JSON.stringify(error)}
        </>);

        const notReady = isAvailable === null || !name || !region || !realm || typeof name !== "string" || typeof region !== "string" ||typeof realm !== "string";
        if (notReady) return <></>;

        if (isAvailable) return (
            <>
                <Typography variant="h1">{name} is available!</Typography>
                <Typography variant="h2">On realm {realm} and region {region}</Typography>
            </>
        )

        return (
            <NotAvailableBox name={name} region={region} realm={realm}/>
        )
    }

    return <Result/>;
}
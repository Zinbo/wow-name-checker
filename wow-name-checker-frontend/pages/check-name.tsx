import {useRouter} from "next/router";
import React, {useEffect, useState} from "react";
import {Typography} from "@mui/material";

export default function Results() {
    const router = useRouter();
    const {name, region, realm} = router.query;
    const [isAvailable, setIsAvailable] = useState<boolean | null>(null);
    const [lastLogin, setLastLogin] = useState<Date | null>(null);
    const [error, setError] = useState(null);

    const callAPI = async () => {
        const res = await fetch(`/profile?name=${name}&region=${region}&realm=${realm}`);
        if (res.status === 404) setIsAvailable(true);
        else if (res.ok) {
            setIsAvailable(false);
            const data = await res.json();
            setLastLogin(new Date(data.last_login_timestamp));
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

        if (isAvailable === null) return <></>;

        if (isAvailable) return (
            <>
                <Typography variant="h1">{name} is available!</Typography>
                <Typography variant="h2">On realm {realm} and region {region}</Typography>
            </>
        )

        return (
            <>
                <Typography variant="h1">{name} isn&apos;t available :(</Typography>
                <Typography variant="h2">Last login: {lastLogin?.toLocaleString()}</Typography>
            </>
        )
    }

    return <Result/>;
}
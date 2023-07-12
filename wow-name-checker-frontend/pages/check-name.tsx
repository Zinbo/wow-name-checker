import {useRouter} from "next/router";
import React, {useEffect, useState} from "react";
import NotAvailableBox from "@/components/NotAvailableBox";
import AvailableBox from "@/components/AvailableBox";
import ErrorDialog from "@/components/ErrorDialog";
import LoadingScreen from "@/components/LoadingScreen";

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
        if (!!error || !name || !region || !realm || typeof name !== "string" || typeof region !== "string" ||typeof realm !== "string") return <ErrorDialog errorTitle="Could not load WoW Character" hasError={true} />

        const isLoading = isAvailable === null;
        if (isLoading) return <LoadingScreen isLoading={isLoading}/>;
        if (isAvailable) return <AvailableBox name={name} region={region} realm={realm}/>
        return <NotAvailableBox name={name} region={region} realm={realm}/>
    }

    return <Result/>;
}
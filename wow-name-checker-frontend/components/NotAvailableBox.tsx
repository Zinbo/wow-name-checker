import React from "react";
import SubscriptionContainer from "@/components/SubscriptionContainer";

interface Props {
    name: string,
    realm: string,
    region: string
}
export default ({name, realm, region}:Props) => {

    return ( <SubscriptionContainer title="That name is taken!"
                                    subTitle="Do you want to get an email when it becomes available?" actionName="Subscribe"
                                    url="/alert" dto={{character: name, realm, region}}/>);
}
import React from "react";
import SubscriptionContainer from "@/components/SubscriptionContainer";

interface Props {
    name: string,
    realm: string,
    region: string
}
export default ({name, realm, region}:Props) => {

    return ( <SubscriptionContainer title="Do you want to unsubscribe?"
                                    subTitle="Enter your email address to unsubscribe" actionName="Unsubscribe"
                                    url="/alert/unsubscribe" dto={{character: name, realm, region}}/>);
}
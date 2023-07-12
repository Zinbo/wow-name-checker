import React from "react";
import SubscriptionContainer from "@/components/SubscriptionContainer";

export default function Results() {

    return (
        <SubscriptionContainer title="Do you want to unsubscribe?"
                               subTitle="Enter your email address to unsubscribe" actionName="Unsubscribe"
                               url="/alert/unsubscribe"/>
    );
}
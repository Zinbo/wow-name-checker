@echo off
set clientId=%WOW_CLIENT_ID%
set clientSecret=%WOW_CLIENT_SECRET%

kubectl create namespace wow-name-checker
kubectl label namespace wow-name-checker istio-injection=enabled
helm upgrade --install wow-name-checker ./wow-name-checker-chart -f ./wow-name-checker-chart/values-local.yaml --create-namespace -n wow-name-checker --set wow.clientId=%clientId% --set wow.clientSecret=%clientSecret%
[![workflow badge](https://github.com/wildmrbones/wow-name-checker/actions/workflows/main.yml/badge.svg)](https://github.com/wildmrbones/wow-name-checker/actions)
[![Known Vulnerabilities](https://snyk.io/test/github/wildmrbones/wow-name-checker/badge.svg)]


[![HitCount](https://hits.dwyl.com/wildmrbones/wow-name-checker.svg)](https://hits.dwyl.com/wildmrbones/wow-name-checker)

## Why? [![start with why](https://img.shields.io/badge/start%20with-why%3F-brightgreen.svg?style=flat)](https://www.ted.com/talks/simon_sinek_how_great_leaders_inspire_action)
People tend to create placeholder characters on World of Warcraft to hold names that they want to use in the future.  
Sometimes they then delete those characters if they no longer want the name, or if they haven't logged on for a long time Blizzard will release those names.  
If it's a popular name you'll need to know when it has been released so you can grab it.  
This app allows you to subscribe to a name on a specific realm and region so that you'll get notified when the name becomes available.

# wow-name-checker

Check if names are available in World of Warcraft

## Run Locally
1. Navigate to `/docker`
2. Run `docker-compose build`
3. Run `docker-compose up`
4. To restart a specific container run `docker-compose restart <container>`

### Debug Spring Boot Application
if you've loaded the project from the root folder in IntelliJ then you should already see a debug configuration called `Debug Spring Boot App`. If not, load this from `.idea/runConfiguration/Debug_Spring_boot_App.xml`.

## K8s

### Setup
1. Install istio on cluster: `istioctl install --set profile=demo -y`
2. Install dashboards: `kubectl apply -f samples/addons`

### To run
1. Start Docker
2. Start minikube: `minikube start`
3. build images: `docker build . -t wow-name-checker-backend:local` and `docker build ./wow-name-checker-frontend/. -t wow-name-checker-front:local`
4. Add images to local repo: `minikube image load wow-name-checker-backend:local` and `minikube image load wow-name-checker-frontend:local` 
5. Install chart: `helm install wow-name-checker ./wow-name-checker-chart -f ./wow-name-checker-chart/values-local.yaml`
6. Add `127.0.0.1 wow-name-checker.local` to `C:\Windows\System32\drivers\etc\hosts`
7. run minikube tunnel: `minikube tunnel`
8. Go to `wow-name-checker.local`

### Dashboards
See dashboards with command `istioctl dashboard <product>`, where product can be `grafana`, `prometheus`, `kiali`, `jaeger`

To view the dashboard run: `miikube dashboard`

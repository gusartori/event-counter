# event-counter
A simple producer/consumer kafka application with a controller for production/consumption status tested with TestContainers

### TestContainers
Make sure you have DOCKER_HOST properly configure before running component tests (CTest).
On my setup I am using Rancher:
```
DOCKER_HOST=unix:///$HOME/.rd/docker.sock
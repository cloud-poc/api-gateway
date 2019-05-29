# api-gateway

- add custom/dummy health check endpoint for testing of spring cloud config + spring cloud bus for dynamic config change
1. create a controller with two annotations - @RestController, @RefreshScope

    @Value("${health-check.enabled}")
    private boolean hcIndicator;

    @GetMapping("/health-check")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<String, Object>();

        if (hcIndicator) {
            response.put("health-check.status", "running");
        } else {
            response.put("health-check.enabled", hcIndicator);
        }
    
        return response;
     }
     
    
2. add config in config file: api-gateway-dev.yml -> health-check.enabled: false
3. start two app using different ports, eg. 8080 and 8081
4. use postman to test it, http:localhost:8080/health-check, response is 
> health-check.enabled: false
5. update health-check.enabled: true and check in to git repo
6. send a post request to http://localhost:8080/actuator/refresh
7. then test #4 on these two endpoints, you will get same response, 
> health-check.status: running
that mean even refresh the config on one endpoint, spring cloud bus will help to cascade the update to all registerred client through Message Queue

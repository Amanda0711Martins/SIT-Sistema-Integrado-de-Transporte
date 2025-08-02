    package com.sittransportadora.utilitary;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.env.Environment;
    import org.springframework.stereotype.Component;

    import jakarta.annotation.PostConstruct;

    @Component
    public class EurekaDebug {

        @Autowired
        private Environment env;

        @PostConstruct
        public void printEurekaUrl() {
            String eurekaUrl = env.getProperty("eureka.client.service-url.defaultZone");
            System.out.println(">>>>> Eureka URL: " + eurekaUrl);
        }
    }

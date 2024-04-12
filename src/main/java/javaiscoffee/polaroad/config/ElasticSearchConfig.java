package javaiscoffee.polaroad.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@Profile("prod")
public class ElasticSearchConfig extends ElasticsearchConfiguration {
    @Value("${spring.elasticsearch.uris}")
    private String elasticSearchUris;
    @Value("${spring.elasticsearch.username}")
    private String username;
    @Value("${spring.elasticsearch.password}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticSearchUris)
                .withBasicAuth(username,password)
                .build();
    }
}

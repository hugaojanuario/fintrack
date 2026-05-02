package br.com.fintrack.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic goalAchievedTopic() {
        return TopicBuilder.name("fintrack.goal.achieved")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic marketQuoteUpdatedTopic() {
        return TopicBuilder.name("fintrack.market.quote.updated")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

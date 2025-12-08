package com.example.orderservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;

@Service
public class SqsListener {

    private static final Logger logger = LoggerFactory.getLogger(SqsListener.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${sqs.queue.url}")
    private String queueUrl;

    public SqsListener() {
        this.sqsClient = SqsClient.create();
        this.objectMapper = new ObjectMapper();
    }

    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    public void pollMessages() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();
        for (Message message : messages) {
            try {
                Product product = objectMapper.readValue(message.body(), Product.class);
                handleProductEvent(product);

                // Delete the message after processing
                DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();
                sqsClient.deleteMessage(deleteRequest);
            } catch (JsonProcessingException e) {
                logger.error("Error processing message", e);
            }
        }
    }

    private void handleProductEvent(Product product) {
        logger.info("Received product event: {}", product);
        // Here you would typically process the event, e.g., create an order
    }
}

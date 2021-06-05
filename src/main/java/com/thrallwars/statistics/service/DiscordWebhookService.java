package com.thrallwars.statistics.service;

import club.minnced.discord.webhook.WebhookClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.dto.discord.WebhookMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Log4j2
public class DiscordWebhookService {

    private final ServiceConfig serviceConfig;
    private final ObjectMapper objectMapper;

    public DiscordWebhookService(ServiceConfig serviceConfig, ObjectMapper objectMapper) {
        this.serviceConfig = serviceConfig;
        this.objectMapper = objectMapper;
    }

    @Async
    public void publishInfo(String msg) {
        serviceConfig.getInfoWebhookUrls().forEach(url -> publishMessageForUrl(msg, url));
    }

    @Async
    public void publishFile(byte[] msg, String fileName) {
        serviceConfig.getInfoWebhookUrls().forEach(url -> publishFileForUrl(msg, fileName, url));
    }

    private void publishMessageForUrl(String msg, String url) {
        try {
            WebhookMessage webhookMessage = new WebhookMessage();
            webhookMessage.setContent(msg);
            String body = objectMapper.writeValueAsString(webhookMessage);
            HttpRequest request = HttpRequest.newBuilder(new URI(url))
                    .setHeader("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 204) {
                log.error("Failed to post message to discord, response is {} and status code {}", response.body(), response.statusCode());
            }
        } catch (Exception e) {
            log.error("Failed to post message to discord", e);
        }

    }

    public void publishFileForUrl(byte[] content, String fileName, String url) {
        WebhookClient client = WebhookClient.withUrl(url);
        client.send(content, fileName);
        client.close();
//        try {
//            Path tempFile = Files.createTempFile("discord", "msg");
//            Path file = Files.write(tempFile, msg);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", new ByteArrayResource(msg, "data.json.gzip"));
//            HttpEntity<MultiValueMap<String, Object>> requestEntity
//                    = new HttpEntity<>(body, headers);
//            ResponseEntity<String> response = new RestTemplate().postForEntity(url, requestEntity, String.class);
//            if (!response.getStatusCode().is2xxSuccessful()) {
//                log.error("Failed to post message to discord, response is {} and status code {}", response.getBody(), response.getStatusCodeValue());
//            }
//        } catch (Exception e) {
//            log.error("Failed to post message to discord", e);
//        }
    }
}

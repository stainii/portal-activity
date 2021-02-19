package be.stijnhooft.portal.activity.services;


import be.stijnhooft.portal.model.image.ImageDto;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;


/**
 * Service responsible for all communication with the recurring tasks module.
 */
@Service
@Slf4j
public class ImageService {

    public static final String API_CONTEXT_ROOT = "api/";
    public static final String TRANSFORMATION_DEFINITION = "[{ \"label\": \"thumbnail\", \"transformations\": [{ \"name\": \"resize\", \"width\": \"300\", \"height\": \"300\", \"crop\": true}] }]";

    private final RestTemplate restTemplate;

    @Getter
    private final String portalImageUrl;

    public ImageService(RestTemplate restTemplate, @Value("${portal.image.url}") String portalImageUrl) {
        this.restTemplate = restTemplate;
        this.portalImageUrl = portalImageUrl;
    }

    public String createThumbnail(@NonNull String imageContent) {
        ByteArrayResource image = new ByteArrayResource(decodeBase64Image(imageContent)) {
            @Override
            public String getFilename() {
                return UUID.randomUUID().toString();
            }
        };

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("image", image);
        requestBody.add("transformationDefinitions", TRANSFORMATION_DEFINITION);

        var url = portalImageUrl + API_CONTEXT_ROOT + "transform/";
        log.info("Creating thumbnail at " + url);

        ResponseEntity<List<ImageDto>> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(requestBody), new ParameterizedTypeReference<>() {
        });

        return Optional.ofNullable(response.getBody())
                .stream()
                .flatMap(Collection::stream) // transform Stream<List<ImageDto>> to Stream<ImageDto>
                .filter(imageDto -> imageDto.getLabel().equals("thumbnail"))
                .findFirst()
                .map(ImageDto::getName)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Could not find thumbnail in response of image: %s", response.getBody())));
    }

    public void rollbackCreateImage(String image) {
        log.warn("Creating or updating person failed. Reverting creation of image " + image);
        delete(image);
    }

    public void delete(@NonNull String imageName) {
        try {
            var url = portalImageUrl + API_CONTEXT_ROOT + "remove/" + imageName;
            log.info("Deleting image at " + url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException.NotFound ex)   {
            log.warn("Image microservice returns 404 when deleting image {}. Image might already have been deleted.", imageName);
        }
    }


    private byte[] decodeBase64Image(@NonNull String imageContent) {
        var beginIndex = imageContent.indexOf(",");
        var data = imageContent.substring(beginIndex + 1);
        return Base64.getDecoder().decode(data);
    }

}

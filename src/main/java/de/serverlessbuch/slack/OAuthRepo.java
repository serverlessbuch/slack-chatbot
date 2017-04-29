package de.serverlessbuch.slack;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Map;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthRepo {

    private static OAuthRepo instance;
    private static AmazonS3 amazonS3;

    private final String bucket;
    private final String key = "slackbot_access_tokens.json";

    private static ObjectMapper mapper = new ObjectMapper();

    public static OAuthRepo instance(String bucket) {
        if (null == instance) {
            instance = new OAuthRepo(bucket);
            amazonS3 = AmazonS3ClientBuilder.defaultClient();
        }
        return instance;
    }

    public void storeAccessToken(String teamId, String accessToken) {
        Map<String, Object> tokenBucket = getTokenBucket();
        tokenBucket.put(teamId, accessToken);
        storeTokenBucket(tokenBucket);
    }

    public String getAccessToken(String teamId) {
        Map<String, Object> tokenBucket = getTokenBucket();
        return (String) tokenBucket.get(teamId);
    }

    @SneakyThrows
    private Map<String, Object> getTokenBucket() {
        if (!amazonS3.doesObjectExist(bucket, key)) {
            amazonS3.putObject(bucket, key, "{}");
        }
        S3Object tokenBucket = amazonS3.getObject(bucket, key);
        Map<String, Object> tokens = mapper.readValue(tokenBucket.getObjectContent(), MapperUtil.mapTypeReference);
        tokenBucket.close();
        return tokens;
    }

    @SneakyThrows
    private void storeTokenBucket(Map<String, Object> tokenBucket) {
        String tokenString = mapper.writeValueAsString(tokenBucket);
        amazonS3.putObject(bucket, key, tokenString);
    }
}

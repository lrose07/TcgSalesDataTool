import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class AwsUtilities {

    private static String requestedSecretValue;

    private AwsUtilities() {
        throw new IllegalStateException("Utility class");
    }

    static void connectToS3() {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        List<Bucket> buckets = s3Client.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(bucket.getName());
        }
    }

    public static String getRequestedSecretValue(String secretName, String secretNameArn) {
        getSecret(secretName, secretNameArn);
        return requestedSecretValue;
    }

    private static void getSecret(String secretName, String secretNameArn) {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();

        try {
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretNameArn)
                    .build();

            GetSecretValueResponse valueResponse = client.getSecretValue(valueRequest);
            String secret = valueResponse.secretString();
            requestedSecretValue = getSecretFromJsonResponse(secret, secretName);

        } catch (SecretsManagerException e) {
            System.out.println("Error with getting secret:");
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    private static String getSecretFromJsonResponse(String jsonString, String secretName) {
        System.out.println("getting secret from JSON...");
        Gson gson = new Gson();
        Type secretResponseType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> secretData = gson.fromJson(jsonString, secretResponseType);
        return secretData.get(secretName);
    }
}

package org.aleks4ay.console;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.aleks4ay.domain.Product;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RunConsole {

    AWSLambda awsLambdaClient = AWSLambdaClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider())
            .withRegion(Regions.US_EAST_1)
            .build();

    private static final int productId = 5;
    private static final String functionName = "products-function";

    public static void main(String[] args) {

        RunConsole consoleApp = new RunConsole();

        consoleApp.printListLambdaFunctions();

        Product productFromAws = consoleApp.getProductById(functionName, productId);
        System.out.println("\n" + productFromAws);

        consoleApp.createProduct(functionName, new Product(7, "bucket", "https://bucket.jpg", 14));
        consoleApp.updateProduct(functionName, new Product(8, "bucket update", "https://bucket.jpg", 15));
    }


    private Product getProductById(String functionName, int productId) {

        String payload = getJsonGetRequest(productId).toString();

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(payload);

        InvokeResult invokeResult = awsLambdaClient.invoke(invokeRequest);
        String rawJsonProduct = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

        return parseProductFromJson(rawJsonProduct);
    }

    private void createProduct(String functionName, Product product) {
        String jsonPostRequest = getJsonPutOrPostRequest(product, "POST").toString();

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(jsonPostRequest);

        awsLambdaClient.invoke(invokeRequest);
    }

    private void updateProduct(String functionName, Product product) {
        String jsonPostRequest = getJsonPutOrPostRequest(product, "PUT").toString();

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(jsonPostRequest);

        InvokeResult invoke = awsLambdaClient.invoke(invokeRequest);
        System.out.println(invoke.getPayload());
    }

    private void printListLambdaFunctions() {
        for (FunctionConfiguration config : awsLambdaClient.listFunctions().getFunctions()) {
            System.out.printf("The function name is '%s'\n", config.getFunctionName());
        }
    }

    public JSONObject getJsonPutOrPostRequest(Product product, String mappingName) {

        Map<String, Object> jsonProduct = new HashMap<>();
        jsonProduct.put("id", product.getId());
        jsonProduct.put("name", product.getName());
        jsonProduct.put("pictureUrl", product.getPictureUrl());
        jsonProduct.put("price", product.getPrice());

        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("httpMethod", mappingName.equals("POST") ? "POST" : "PUT");
        jsonRequest.put("product", jsonProduct);

        return new JSONObject(jsonRequest);
    }

    public JSONObject getJsonGetRequest(int id) {
        Map<String, Object> jsonRequest = new HashMap<>();
        jsonRequest.put("httpMethod", "GET");
        jsonRequest.put("id", id);
        return new JSONObject(jsonRequest);
    }

    public Product parseProductFromJson(String jsonProduct) {
        JSONObject obj = new JSONObject(jsonProduct);

        int id = obj.getInt("id");
        String title = obj.getString("name");
        String pictureUrl = obj.getString("pictureUrl");
        int price = obj.getInt("price");
        return new Product(id, title, pictureUrl, price);
    }
}

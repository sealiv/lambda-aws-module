package org.aleks4ay;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import org.aleks4ay.domain.Product;
import org.aleks4ay.domain.ProductRequest;

public class App {

    public Object handleRequest(ProductRequest productRequest, Context context) throws ResourceNotFoundException {

        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.defaultClient();
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        Product product;

        if (productRequest.getHttpMethod().equals("GET") && productRequest.getId() != 0) {
            product = mapper.load(Product.class, productRequest.getId());
            if (product == null) {
                throw new ResourceNotFoundException("Product with id='" + productRequest.getId() + "' not found");
            }
            return product;
        }

        if (productRequest.getHttpMethod().equals("POST")) {
            product = productRequest.getProduct();
            mapper.save(product);
            return product;
        }

        if (productRequest.getHttpMethod().equals("PUT")) {
            product = productRequest.getProduct();

            Product productFromDb = mapper.load(Product.class, product.getId());
            if (productFromDb != null) {
                mapper.save(product);
                return product;
            } else {
                throw new ResourceNotFoundException("Product with id='" + product.getId() + "' not found");
            }
        }
        return null;
    }
}

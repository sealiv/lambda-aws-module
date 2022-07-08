package org.aleks4ay.domain;

public class ProductRequest {
    private String httpMethod;
    private int id;
    private Product product;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product student) {
        this.product = student;
    }
}

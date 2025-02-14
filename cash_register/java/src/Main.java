import java.util.*;

class Product {
    private int id;
    private String product;
    private double price;
    private int quantity;

    public Product (int id, String product, double price, int quantity) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        if (product == null || product.trim().isEmpty()) {
            throw new IllegalArgumentException("Product must be non-empty text.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be a positive number.");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be a non-negative integer.");
        }

        this.id = id;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    // getter method
    public int getId() {
        return this.id;
    }

    // getter method
    public String getProduct() {
        return this.product;
    }

    // getter method
    public double getPrice() {
        return this.price;
    }

    // getter method
    public int getQuantity() {
        return this.quantity;
    }

    // setter method
    public void setQuantity(int quantity) {
        this.quantity= quantity;
    }

    public static Product findById(List<Product> productList, int productId) {
        for (Product product : productList) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, PRODUCT: %s, PRICE: $%.2f QUANTITY: %d", id, product, price, quantity);
    }
}

class CashRegister {

    private final List<Map<String, Object>> listProduct;
    private double totalAmount;

    public CashRegister() {
        this.listProduct = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public void addProduct(Product product, int quantity) {
        double subtotal = product.getPrice() * quantity;
        Map<String, Object> productDetails = new HashMap<>();
        productDetails.put("ID", product.getId());
        productDetails.put("PRODUCT", product.getProduct());
        productDetails.put("PRICE", product.getPrice());
        productDetails.put("QUANTITY", product.getQuantity());
        productDetails.put("TOTAL", subtotal);
        listProduct.add(productDetails);
        totalAmount += subtotal;
    }

    public void updateProductQuantity (int productId, int newQuantity, List<Product> products){
        for (Map<String, Object> item : listProduct) {
            if ((int) item.get("ID") == productId) {
                Product product = Product.findById(products, productId);
                if (product != null && newQuantity <= product.getQuantity()) {
                    // Calculate the difference in the total
                    double oldTotal = (double) item.get("TOTAL");
                    double newTotal = (double) item.get("PRICE") * newQuantity;
                    totalAmount += (newTotal - oldTotal);

                    // Update product quantity and total
                    item.put("QUANTITY", newQuantity);
                    item.put("TOTAL", newTotal);
                    System.out.println(String.format("Quantity of %s updated to %d.", item.get("PRODUCT"), newQuantity));
                } else {
                    System.out.println("Not enough stock available.");
                }
                return;
            }

        }
        System.out.println("Product not found in the cart.");
    }

    public void removeProduct (int productId){
        for (Map<String, Object> item : listProduct) {
            if ((int) item.get("ID") == productId) {
                // Subtract the total of the removed product
                totalAmount -= (double) item.get("TOTAL");
                // Remove the product from the cart
                listProduct.remove(item);
                System.out.println(String.format("item '%s' removed from the cart.", item));
                return;
            }
        }
        System.out.println("Product not found in the cart.");
    }

    public void viewCart(){
        if (listProduct.isEmpty()){
            System.out.println("Cart Empty");
        }else {
            for (Map<String, Object> item : listProduct) {
                System.out.println(item);
                System.out.println(String.format("Total amount: $%s", totalAmount));
            }
        }
    }

    public void checkout () {
        if (listProduct.isEmpty()) {
            System.out.println("Cart empty. Nothing to checkout.");
        }else {
            viewCart();
            listProduct.clear();
            totalAmount = 0;
        }
    }

}

class ProductData {
    int id;
    String product;
    double price;
    int quantity;

    public ProductData(int id, String product, double price, int quantity) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }
}

public class Main {

    public static List <Product> loadProducts (List <ProductData> productsData){
        List<Product> productsList = new ArrayList<>();
        for (ProductData item : productsData) {
            try {
                Product product = new Product(item.id, item.product, item.price, item.quantity);
                productsList.add(product);
            } catch (IllegalArgumentException e) {
                System.out.println(String.format("Error in product %d: %s", item.id, e.getMessage()));
            }
        }
        return productsList;
    }

    public static void main(String[] args) {

        List <ProductData> productsData = Arrays.asList(
                new ProductData(1, "Apple", 8.0, 10),
                new ProductData(2, "Orange", 7.0, 20),
                new ProductData(3, "Pineapple", 10.0, 8),
                new ProductData(4, "Bananas", 5.0, 24),
                new ProductData(5, "Tangerines", 6.0, 6),
                new ProductData(6, "Kiwi", 6.0, 9),
                new ProductData(7, "Peach", 4.55, 10),
                new ProductData(8, "Melon", 7.35, 7),
                new ProductData(9, "Watermelon", 9.70, 10)
        );

        List<Product> products = loadProducts(productsData);
        for (Product p : products) {
            System.out.println(p.toString());
        }

        CashRegister cashRegister = new CashRegister();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. View list of products\n2. Add a product to the cart\n3. Update product quantity\n4. Remove product\n5. View the cart\n6. Checkout\n");
            try {
                int option = Integer.parseInt(scanner.nextLine());

                if (option == 1) {
                    for (Product p : products) {
                        System.out.println(p.toString());
                    }
                } else if (option == 2) {
                    try {
                        System.out.println("\nEnter the ID of the product: ");
                        int productId = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter the quantity: ");
                        int quantity = Integer.parseInt(scanner.nextLine());

                        Product product = Product.findById(products, productId);
                        if (product != null) {
                            if (quantity <= product.getQuantity()) {
                                cashRegister.addProduct(product, quantity);
                                cashRegister.viewCart();
                            } else {
                                System.out.println("Not enough stock available.");
                            }
                        } else {
                            System.out.println("Product not found.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                } else if (option == 3) {
                    try {
                        System.out.println("Enter the ID of the product to update: ");
                        int productId = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter the new quantity: ");
                        int newQuantity = Integer.parseInt(scanner.nextLine());
                        cashRegister.updateProductQuantity(productId, newQuantity, products);
                        cashRegister.viewCart();
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                } else if (option == 4) {
                    try {
                        System.out.println("Enter the ID of the product to remove: ");
                        int productId = Integer.parseInt(scanner.nextLine());
                        cashRegister.removeProduct(productId);
                        cashRegister.viewCart();
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                } else if (option == 5) {
                    cashRegister.viewCart();
                } else if (option == 6) {
                    System.out.println("\nChecking out...");
                    cashRegister.checkout();
                    System.out.println("Purchase completed successfully. Thank you for your purchase!");
                    break;
                } else {
                    System.out.println(String.format("'%d' is not a valid option. Please enter a number between 1 and 6.", option));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        scanner.close();
    }
}

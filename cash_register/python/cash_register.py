import logging

logger = logging.getLogger(__name__)

logging.basicConfig(
    level=logging.INFO,  
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        logging.StreamHandler() 
    ]
)

class Products:
    def __init__(self, id,product, price, quantity):

        if not isinstance(id, int) or id <= 0:
            raise ValueError("ID must be a positive integer.")
        
        if not isinstance(product, str) or len(product.strip()) == 0:
            raise ValueError("Product must be non-empty text.")
        
        if not isinstance(price, (int, float)) or price <= 0:
            raise ValueError("Price must be a positive number.")
        
        if not isinstance(quantity, int) or quantity < 0:
            raise ValueError("Quantity must be a non-negative integer.")

        self.__id = id
        self.__product = product.strip()
        self.__price  = float(price)
        self.quantity = int(quantity)
    
    @property
    def id(self): return self.__id

    @property
    def product(self): return self.__product

    @property
    def price(self): return self.__price

    @property
    def quantity(self): return self.__quantity  

    @quantity.setter
    def quantity(self, value: int):
        if not isinstance(value, int) or value < 0: raise ValueError("Quantity must be a non-negative integer.")
        self.__quantity = value

    @classmethod
    def find_by_id(cls, product_list, product_id):
        for product in product_list:
            if product.id == product_id:
                return product
        return None    

    def __str__(self):
        return f"ID: {self.id}, PRODUCT: {self.product}, PRICE: ${self.price:.2f} QUANTITY: {self.quantity}"


class CashRegister: 
    def __init__(self):
        self.list_product = []
        self.total_amount = 0
    
    def add_product(self, product, quantity):
        """
        Add a product to the cart and update the running total.
        
        Parameters:
            product (Products): Object of type Products.
            quantity (int): Quantity of the product to add.
        """
        subtotal = product.price * quantity
        self.list_product.append({"ID": product.id, "PRODUCT": product.product, "PRICE": product.price, "QUANTITY": product.quantity, "TOTAL": subtotal})
        self.total_amount += subtotal

    def update_product_quantity(self, product_id, new_quantity):
        """
        Update the quantity of a product in the cart.
        
        Parameters:
            product_id (int): ID of the product to update.
            new_quantity (int): New quantity of the product.
        """
        for item in self.list_product:
            if item["ID"] == product_id:

                product = Products.find_by_id(products, product_id)
                if product and new_quantity <= product.quantity:
                    # Calculate the difference in the total
                    old_total = item["TOTAL"]
                    new_total = item["PRICE"] * new_quantity
                    self.total_amount += (new_total - old_total)

                    # Update product quantity and total
                    item["QUANTITY"] = new_quantity
                    item["TOTAL"] = new_total

                    logger.info(f"Quantity of {item['PRODUCT']} updated to {new_quantity}.")
                else:
                    logger.info("Not enough stock available.")
                return

        logger.info("Product not found in the cart.")
    
    def remove_product(self, product_id):
        """
        Remove a product from the cart.
        
        Parameters:
            product_id (int): ID of the product to delete.
        """
        for item in self.list_product:            
            if item["ID"] == product_id:
                # Subtract the total of the removed product
                self.total_amount -= item["TOTAL"]
                # Remove product from cart
                self.list_product.remove(item)
                logger.info(f"{item['PRODUCT']} removed from the cart.")
                return

        logger.info(f"Product with ID {product_id} not found in the cart.") 

    def view_cart(self):
        """
        Shows the contents of the cart and the accumulated total.
        """
        if not self.list_product:
            logger.info("Cart empty.")
        else:
            for item in self.list_product:
                logger.info(f"ID: {item['ID']}, PRODUCT: {item['PRODUCT']}, PRICE: ${item['PRICE']}, QUANTITY: {item['QUANTITY']}, TOTAL: ${item['TOTAL']}")
            logger.info(f"Total amount: ${self.total_amount}")

    def checkout(self):
        """
        Complete the purchase and empty the cart.
        """
        if not self.list_product:
            logger.info("Cart empty. Nothing to checkout.")
        else:
            self.view_cart()            
            self.list_product.clear()  
            self.total_amount = 0         

def load_products(products_data):   
    """Function to load products from a dictionary list.""" 
    products_list = []    
    for item in products_data:
        try:
            product = Products(item["id"], item["product"], item["price"], item["quantity"])
            products_list.append(product)
        except ValueError as e: 
            logger.info(f"Error in product {item['id']}: {str(e)}")    
    return products_list
        

products_data = [
    {"id": 1, "product": 'Apple', "price": 8.0, "quantity": 10}, 
    {"id": 2, "product": 'Orange', "price": 7.0, "quantity": 20}, 
    {"id": 3, "product": 'Pineapple', "price": 10.0, "quantity": 8}, 
    {"id": 4, "product": 'Bananas', "price": 5.0, "quantity": 24}, 
    {"id": 5, "product": 'Tangerines', "price": 6.0, "quantity": 6}, 
    {"id": 6, "product": 'Kiwi', "price": 6.0, "quantity": 9}, 
    {"id": 7, "product": 'Peach', "price": 4.55, "quantity": 10}, 
    {"id": 8, "product": 'Melon', "price": 7.35, "quantity": 7}, 
    {"id": 9, "product": 'Watermelon', "price": 9.70, "quantity": 10} 
    ]

if __name__ == "__main__":
    print("List of Products Items:")
    products = load_products(products_data)
    for p in products: print(p)

    cash_register = CashRegister()

    while True:
        print("""\n1. View list of products\n2. Add a product to the cart\n3. Update product quantity\n4. Remove product\n5. View the cart\n6. Checkout\n""")
        try:
            option = int(input("Enter an option: "))  

            if option == 1:
                for p in products: print(p)
            
            elif option == 2:
                try:
                    product_id = int(input("\nEnter the ID of the product: "))
                    quantity = int(input("Enter the quantity: "))

                    product = Products.find_by_id(products, product_id)
                    if product:
                        if quantity <= product.quantity:               
                            cash_register.add_product(product=product, quantity=quantity)
                            
                            cash_register.view_cart()
                        else:
                            logger.info("Not enough stock available.")
                    else:
                        logger.info("Product not found.")
                except ValueError:
                    logger.error("Invalid input. Please enter a valid number.")

            elif option == 3:  
                try:
                    product_id = int(input("Enter the ID of the product to update: "))
                    new_quantity = int(input("Enter the new quantity: "))
                    cash_register.update_product_quantity(product_id, new_quantity)
                    cash_register.view_cart()
                except ValueError:
                    logger.error("Invalid input. Please enter a valid number.")

            elif option == 4:  
                try:
                    product_id = int(input("Enter the ID of the product to remove: "))
                    cash_register.remove_product(product_id)
                    cash_register.view_cart()
                except ValueError:
                    logger.error("Invalid input. Please enter a valid number.")        

            elif option == 5:
                cash_register.view_cart()
            
            elif option == 6:
                logger.info("\nChecking out...")
                cash_register.checkout()
                logger.info("Purchase completed successfully. Thank you for your purchase!")
                break
            
            else:
                logger.error(f"'{option}' is not a valid option. Please enter a number between 1 and 5.")

        except ValueError:
            logger.error("Invalid input. Please enter a valid number.")




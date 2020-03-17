/*
      Model
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class ElectronicStore {
    private final int MAX_PRODUCTS = 10; // Maximum number of products the store can have
    private int curStockProducts; // Initial products in stock and cart
    private String name;
    private Product[] stock; // Array to hold all products in stock
    private List<Product> cart; // List to hold all products in cart
    private List<Product> soldItems; // List to hold all sold products
    private double revenue;
    private double cartWorth; // Cart's total cost
    private int totalSales;

    public ElectronicStore(String initName) {
        revenue = 0.0;
        cartWorth = 0.0;
        name = initName;
        stock = new Product[MAX_PRODUCTS];
        cart = new ArrayList<Product>();
        soldItems = new ArrayList<Product>();
        curStockProducts = 0;
        totalSales = 0;
    }

    //Getters
    public String getName() {
        return name;
    }

    public double getCartWorth() {
        return cartWorth;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public double getRevenue() {
        return revenue;
    }

    public List<Product> getCartItems() {
        return cart;
    }

    // Helper function to get index of a product in stock array
    public int getIndex(Product selectedProduct) {
        for (int i = 0; i < MAX_PRODUCTS; i++) {
            if (selectedProduct == stock[i]) {
                return i;
            }
        }
        return 0;
    }

    // Helper function to check if stock contains a product
    public boolean stockContains(Product item) {
        for (Product element : stock) {
            if (element == item) {
                return true;
            }
        }
        return false;
    }

    // Helper function to check if soldItems contains a product
    public boolean soldItemsContains(Product item) {
        for (Product element : soldItems) {
            if (element == item) {
                return true;
            }
        }
        return false;
    }

    // Helper function to merge two Product arrays
    public static Product[] merge(Product[] arr1, Product[] arr2) {
        int len = arr1.length + arr2.length;
        Product[] newArr = new Product[len];
        System.arraycopy(arr1, 0, newArr, 0, arr1.length);
        System.arraycopy(arr2, 0, newArr, arr1.length, arr2.length);
        return newArr;
    }

    //Adds a product and returns true if there is space in the array
    //Returns false otherwise
    public boolean addProduct(Product newProduct) {
        if (curStockProducts < MAX_PRODUCTS) {
            stock[curStockProducts] = newProduct;
            curStockProducts++;
            return true;
        }
        return false;
    }

    // Return array without null values
    public Product[] getStockItems() {
        if (curStockProducts <= 0) {
            return new Product[0];
        }

        Product[] temp = new Product[curStockProducts];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = stock[i];
        }
        return temp;
    }

    // Get the amount of products in the cart
    public int getCartQuantity(Product product) {
        int counter = 1;
        for (Product p : cart) {
            if (p == product) {
                counter++;
            }
        }
        return counter;
    }

    // Add a product to the cart, and stop displaying it in stock if necessary
    public void addToCart(Product selectedProduct) {
        cartWorth += selectedProduct.getPrice();
        if (selectedProduct.getStockQuantity() > getCartQuantity(selectedProduct)) { // Edge case
            cart.add(selectedProduct);
        } else { // Edge case
            if (selectedProduct.getStockQuantity() == getCartQuantity(selectedProduct)) {
                removeFromStock(getIndex(selectedProduct));
                cart.add(selectedProduct);
            }
        }
    }

    // Remove a product from the cart and start displaying it in stock if necessary
    public void removeFromCart(Product selectedProduct) {
        cartWorth -= selectedProduct.getPrice();
        cart.remove(selectedProduct);
        if (!stockContains(selectedProduct)) {
            addProduct(selectedProduct);
        }
    }

    // Remove a product from the stock array by a given index
    public void removeFromStock(int index) {
        if (index >= 0 && index < curStockProducts) {
            stock[index] = null;
            for (int i = index + 1; i < curStockProducts; i++) {
                Product temp = stock[i - 1];
                stock[i - 1] = stock[i];
                stock[i] = temp;
            }
            curStockProducts--;
        }
    }

    // Custom sorter: sorts a List of Products based on each products sold quantity
    public void sortByQuantity(List<Product> soldItems) {
        int n = soldItems.size();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++)
                if (soldItems.get(j).getSoldQuantity() > soldItems.get(min).getSoldQuantity())
                    min = j;
            Collections.swap(soldItems, min, i);
        }
    }

    // Complete a sale, clear the cart, update the stock and revenue
    public void completeSale() {
        totalSales += 1;
        cartWorth = 0.0;
        for (Product p : cart) {
            revenue += p.sellUnits(1);
            if (!soldItemsContains(p)) {
                soldItems.add(p);
            }
        }
        cart.clear();
    }

    // Get up to a maximum of three popular items
    public Product[] getPopular() {
        Product[] temp; // Placeholder array to hold 1, 2 or 3 arbitrary products
        if (soldItems.size() == 0) {
            temp = Arrays.copyOfRange(getStockItems(), 0, 3);
            return temp;
        }

        int popSize; // Get the size of sold items to initialize popular products array
        if (soldItems.size() < 3) {
            popSize = soldItems.size();
        } else {
            popSize = 3;
        }

        Product[] pop = new Product[popSize]; // Initialize popular products array

        sortByQuantity(soldItems); // Sort the sold products array

        for (int i = 0; i < pop.length; i++) {
            pop[i] = soldItems.get(i); // Get the sorted values and put them in popular array
        }

        if (popSize == 1) { // If 1 item is sold
            temp = Arrays.copyOfRange(getStockItems(), 0, 2);
            return merge(pop, temp);
        }

        if (popSize == 2) { // If two items are sold
            temp = Arrays.copyOfRange(getStockItems(), 0, 1);
            return merge(pop, temp);
        }

        // If 3 or more items are sold
        return pop;
    }

    // Instantiate a store
    public static ElectronicStore createStore() {
        ElectronicStore store1 = new ElectronicStore("Watts Up Electronics");
        Desktop d1 = new Desktop(100, 10, 3.0, 16, false, 250, "Compact");
        Desktop d2 = new Desktop(200, 10, 4.0, 32, true, 500, "Server");
        Laptop l1 = new Laptop(150, 10, 2.5, 16, true, 250, 15);
        Laptop l2 = new Laptop(250, 10, 3.5, 24, true, 500, 16);
        Fridge f1 = new Fridge(500, 10, 250, "White", "Sub Zero", 15.5, false);
        Fridge f2 = new Fridge(750, 10, 125, "Stainless Steel", "Sub Zero", 23, true);
        ToasterOven t1 = new ToasterOven(25, 10, 50, "Black", "Danby", 8, false);
        ToasterOven t2 = new ToasterOven(75, 10, 50, "Silver", "Toasty", 12, true);
        store1.addProduct(d1);
        store1.addProduct(d2);
        store1.addProduct(l1);
        store1.addProduct(l2);
        store1.addProduct(f1);
        store1.addProduct(f2);
        store1.addProduct(t1);
        store1.addProduct(t2);
        return store1;
    }
}
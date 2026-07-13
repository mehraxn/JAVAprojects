# Application Layers Explained

## Learning goals

- Understand the application layer in Java backend design.
- Distinguish domain objects, services, operation classes, and repositories.
- Learn where authorization and workflow coordination usually belong.

## What is the application layer?

The application layer contains use cases. A use case is an operation that a caller wants the system to perform, such as:

- create an order;
- approve a request;
- borrow a book;
- transfer money;
- assign a product to a category.

The application layer does not usually store data directly and does not usually print UI output. It coordinates the work.

## Main class types

| Type | Responsibility |
|---|---|
| Domain object | Protects rules about one business concept |
| Service class | Coordinates a workflow |
| Operation class | Represents one specific command/use case |
| Repository class | Loads and saves domain objects |

## Example structure

```text
OrderController or Main
        ↓
OrderService
        ↓
Order, Product, Customer
        ↓
OrderRepository, ProductRepository
```

## Simple service example

```java
public final class ProductAssignmentService {
    private final ProductRepository products;
    private final CategoryRepository categories;

    public ProductAssignmentService(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    public ProductSnapshot assignToCategory(String productId, String categoryId) {
        Product product = products.findRequired(productId);
        Category category = categories.findRequired(categoryId);
        product.assignTo(category.id());
        products.save(product);
        return ProductSnapshot.from(product);
    }
}
```

## Common mistakes

- Letting `Main` perform all workflow steps.
- Letting repositories decide business permissions.
- Letting domain objects access repositories.
- Spreading the same validation across many classes.

## Mini exercise

For a book borrowing workflow, list which classes you would create and what each class owns.

## Quick summary

The application layer is the home of use-case coordination. It connects input, domain objects, validation, authorization, and persistence.

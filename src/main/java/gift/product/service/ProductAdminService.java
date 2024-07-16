package gift.product.service;

import gift.category.model.dto.Category;
import gift.category.service.CategoryService;
import gift.product.model.ProductRepository;
import gift.product.model.dto.CreateProductAdminRequest;
import gift.product.model.dto.Product;
import gift.user.model.dto.AppUser;
import gift.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductAdminService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    public ProductAdminService(ProductRepository productRepository, CategoryService categoryService,
                               UserService userService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @Transactional
    public void addProduct(CreateProductAdminRequest createProductAdminRequest) {
        Category category = categoryService.getCategory(createProductAdminRequest.categoryId());
        AppUser seller = userService.findUser(createProductAdminRequest.sellerId());
        Product product = new Product(createProductAdminRequest.name(), createProductAdminRequest.price(),
                createProductAdminRequest.imageUrl(), seller, category);
        productRepository.save(product);
    }
}

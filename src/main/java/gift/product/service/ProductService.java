package gift.product.service;

import gift.product.model.ProductRepository;
import gift.product.model.dto.CreateProductRequest;
import gift.product.model.dto.Product;
import gift.product.model.dto.ProductResponse;
import gift.product.model.dto.UpdateProductRequest;
import gift.user.exception.ForbiddenException;
import gift.user.model.dto.AppUser;
import gift.user.model.dto.Role;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Product findProduct(Long id) {
        return productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Product"));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProduct() {
        return productRepository.findAllByIsActiveTrue().stream()
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getPrice(),
                        product.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addProduct(AppUser appUser, CreateProductRequest createProductRequest) {
        Product product = new Product(createProductRequest.name(), createProductRequest.price(),
                createProductRequest.imageUrl(), appUser);
        productRepository.save(product);
    }

    @Transactional
    public void updateProduct(AppUser appUser, Long id, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Product"));
        checkProductOwner(appUser, product);

        product.setName(updateProductRequest.name());
        product.setPrice(updateProductRequest.price());
        product.setImageUrl(updateProductRequest.imageUrl());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(AppUser appUser, Long id) {
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Product"));
        checkProductOwner(appUser, product);

        product.setActive(false);
        productRepository.save(product);
    }

    private void checkProductOwner(AppUser appUser, Product product) {
        if (product.getSeller().equals(appUser) || appUser.getRole() == Role.ADMIN) {
            return;
        }
        throw new ForbiddenException("해당 상품에 대한 권한이 없습니다.");
    }
}
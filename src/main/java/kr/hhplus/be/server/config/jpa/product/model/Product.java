package kr.hhplus.be.server.config.jpa.product.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import kr.hhplus.be.server.config.jpa.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id", unique = true)
	private Long id;
	private String name;
	private ProductCategory category;
	private String description;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductOption> options = new ArrayList<>();

	public Product(String name, ProductCategory category, String description) {
		this.name = name;
		this.category = category;
		this.description = description;
		this.options = new ArrayList<>();
	}

	public static Product createProduct(String name, ProductCategory category, String description) {
		return new Product(name, category,description);
	}

	public void addOption(ProductOption option) {
		options.add(option);
		option.setProduct(this);
	}
}
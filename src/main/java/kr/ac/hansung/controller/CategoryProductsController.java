package kr.ac.hansung.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.hansung.entity.Category;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.exception.NotFoundException;
import kr.ac.hansung.service.CategoryService;
import kr.ac.hansung.service.ProductService;

import java.util.List;


/* API Endpoint for categories and products association
 *
 
 * Link / Unlink products
 * 
 * To see the current products for a given category, you can do a GET on
 * 		/api/categories/{categoryid}/products
 * 
 * To link / unlink products with categories you can use the following URL:
 * 		/api/categories/{categoryid}/products/{productid}
 */

@RestController
@RequestMapping(path = "/api/categories/{categoryid}/products")
public class CategoryProductsController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> retrieveAllProducts(@PathVariable Long categoryid) {
		// Getting all categories in application...
		final List<Product> products = productService.getAllProducts();

		if (products.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return ResponseEntity.ok(products);
		//return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
	}		
	
	@RequestMapping(path = "/{productid}", method = RequestMethod.POST)
	public ResponseEntity<?> addProduct(@PathVariable Long categoryid, @PathVariable Long productid) {

		// Getting the requiring category; or throwing exception if not found
		final Category category = categoryService.getCategoryById(categoryid);
		if (category == null)
			throw new NotFoundException(categoryid);

		// Getting the requiring product; or throwing exception if not found
		final Product product = productService.getProductById(productid);
		if (product == null)
			throw new NotFoundException(productid);

		// Validating if association does not exist...
		if (productService.hasCategory(product, category)) {
			throw new IllegalArgumentException(
					"product " + product.getId() + " already contains category " + category.getId());
		}

		// Associating product with category...
		productService.addCategory(product, category);

		return ResponseEntity.status(HttpStatus.CREATED).body(product);
	}

	@RequestMapping(path = "/{productid}", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeProduct(@PathVariable Long categoryid, @PathVariable Long productid) {
		// Getting the requiring category; or throwing exception if not found
		final Category category = categoryService.getCategoryById(categoryid);
		if (category == null)
			throw new NotFoundException(categoryid);

		// Getting the requiring product; or throwing exception if not found
		final Product product = productService.getProductById(productid);
		if (product == null)
			throw new NotFoundException(productid);

		// Validating if association does not exist...
		if (productService.hasCategory(product, category)) {
			throw new IllegalArgumentException(
					"product " + product.getId() + " already contains category " + category.getId());
		}
		// Deleting product from the application...
		productService.deleteProduct(product);

		return ResponseEntity.noContent().build();
		//return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
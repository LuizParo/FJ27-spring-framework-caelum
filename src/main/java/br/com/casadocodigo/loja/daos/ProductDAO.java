package br.com.casadocodigo.loja.daos;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import br.com.casadocodigo.loja.models.BookType;
import br.com.casadocodigo.loja.models.Product;

@Repository
public class ProductDAO {
	
	@PersistenceContext
	private EntityManager manager;
	
	public void save(Product product) {
		this.manager.persist(product);
	}
	
	public List<Product> list() {
		return this.manager.createQuery("select distinct(p) from Product p left join fetch p.prices", Product.class).getResultList();
		//return this.manager.createNativeQuery("select * from Product p left join Product_prices pp on p.id = pp.product_id", Product.class).getResultList();
	}

	public Product find(Integer id) {
		return this.manager.createQuery("select distinct(p) from Product p left join fetch p.prices where p.id = :id", Product.class)
				.setParameter("id", id)
				.getSingleResult();
	}

	public BigDecimal sumPricesPerType(BookType bookType) {
		TypedQuery<BigDecimal> query = this.manager
					.createQuery("select sum(price.value) from Product p join p.prices price where price.bookType = :bookType", BigDecimal.class)
					.setParameter("bookType", bookType);
		
		return query.getSingleResult();
	}
}
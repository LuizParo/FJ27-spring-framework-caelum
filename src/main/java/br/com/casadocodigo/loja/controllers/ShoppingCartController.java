package br.com.casadocodigo.loja.controllers;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import br.com.casadocodigo.loja.daos.ProductDAO;
import br.com.casadocodigo.loja.models.BookType;
import br.com.casadocodigo.loja.models.PaymentData;
import br.com.casadocodigo.loja.models.Product;
import br.com.casadocodigo.loja.models.ShoppingCart;
import br.com.casadocodigo.loja.models.ShoppingItem;

@Controller
@RequestMapping("/shopping")
public class ShoppingCartController {
	
	@Autowired
	private ProductDAO productDAO;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ShoppingCart shoppingCart;
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView add(Integer productId, @RequestParam BookType bookType) {
		ShoppingItem item = this.createItem(productId, bookType);
		this.shoppingCart.add(item);
		
		return new ModelAndView("redirect:/products");
	}
	
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public Callable<String> checkuot() {
		return () -> {
			BigDecimal total = this.shoppingCart.getTotal();
			String uriToPay = "http://book-payment.herokuapp.com/payment";
			try {
				String response = this.restTemplate.postForObject(uriToPay, new PaymentData(total), String.class);
				System.out.println(response);
				
				return "redirect:/products";
			} catch(HttpClientErrorException e) {
				System.out.println("ocorreu um erro ao criar o pagamento: " + e.getMessage());
				e.printStackTrace();
				return "redirect:/shopping";
			}
		};
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String list() {
		return "shoppingCart/items";
	}

	private ShoppingItem createItem(Integer productId, BookType bookType) {
		Product product = this.productDAO.find(productId);
		return new ShoppingItem(product, bookType);
	}
}
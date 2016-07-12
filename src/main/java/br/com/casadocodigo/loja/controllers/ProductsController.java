package br.com.casadocodigo.loja.controllers;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.casadocodigo.loja.daos.ProductDAO;
import br.com.casadocodigo.loja.infra.FileSaver;
import br.com.casadocodigo.loja.models.BookType;
import br.com.casadocodigo.loja.models.Product;

@Controller
@RequestMapping("/products")
public class ProductsController {
	@Autowired
	private ProductDAO productDAO;
	
	@Autowired
	private FileSaver fileSaver;
	
	@RequestMapping("/form")
	public ModelAndView form(@ModelAttribute Product product) {
		ModelAndView modelAndView = new ModelAndView("products/form");
		modelAndView.addObject("types", BookType.values());
		return modelAndView;
	}
	
	@CacheEvict(value = "lastProducts", allEntries = true)
	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView save(@Valid Product product, BindingResult br, RedirectAttributes redirectAttributes, MultipartFile summary) {
		if(br.hasErrors()) {
			return form(product);
		}
		
		String webPath = this.fileSaver.write("uploaded-summaries", summary);
		product.setSummaryPath(webPath);
		
		this.productDAO.save(product);
		redirectAttributes.addFlashAttribute("msg", "Produto cadastrado com sucesso!");
		return new ModelAndView("redirect:products");
	}
	
	@Cacheable("lastProducts")
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView modelAndView = new ModelAndView("/products/list");
		modelAndView.addObject("products", this.productDAO.list());
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public ModelAndView show(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView("products/show");
		modelAndView.addObject("product", this.productDAO.find(id));
		return modelAndView;
	}
	
	/*@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(new ProductValidator());
	}*/
}
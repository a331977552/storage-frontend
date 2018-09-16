package com.storage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.storage.entity.Product;
import com.storage.entity.Setting;
import com.storage.entity.custom.PageBean;
import com.storage.entity.custom.StorageResult;
import com.storage.remote.service.ProductRemoteService;
import com.storage.remote.service.SettingRemoteService;
import com.storage.utils.JsonUtils;

@RestController()
@RequestMapping("/product")
public class ProductController {

	
	
	@Autowired
	ProductRemoteService productService;
	@Autowired
	SettingRemoteService settingRemoteService;
	
	@RequestMapping("/getProducts")
	public StorageResult<PageBean<Product>> getProducts(@RequestBody(required=false) Product product,@RequestParam(required=false,value="currentPage") Integer currentPage,@RequestParam(value="pageSize",required=false)Integer pageSize){
			
		
		ResponseEntity<String> json = productService.getProduct(product, currentPage, pageSize);
		
		if(json.getStatusCodeValue()==200 &&json.hasBody()) {
			String body = json.getBody();	
			StorageResult<PageBean<Product>> products = JsonUtils.jsonToObject(body, new TypeReference<StorageResult<PageBean<Product>>>() {
			});
			StorageResult<Setting> one = settingRemoteService.getSetting();
			if(one.isSuccess()) {
				Float currencyRate = one.getResult().getCurrencyRate();
				PageBean<Product> result = products.getResult();
				List<Product> beans = result.getBeans();
				for (Product product2 : beans) {
					product2.setSellingprice((int)(currencyRate*product2.getSellingprice()));
				}				
			}
			return products;	
		}
		
		return StorageResult.failed(json.getStatusCodeValue() + json.getBody());
		
		
	}
	@RequestMapping("/getName/{categoryId}.json")
	@ResponseBody
	public Object getProductNamesByCategory(@PathVariable(name = "categoryId") Integer categoryId) {
		ResponseEntity<String> result = this.productService.getProductNamesByCategory(categoryId);

		return result.getBody();
	}

	
	
	
}

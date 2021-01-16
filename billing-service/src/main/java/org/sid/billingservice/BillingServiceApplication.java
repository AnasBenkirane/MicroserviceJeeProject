package org.sid.billingservice;

import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.entities.ProductItem;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.feign.ProductItemRestClient;
import org.sid.billingservice.model.Customer;
import org.sid.billingservice.model.Product;
import org.sid.billingservice.repository.BillRepository;
import org.sid.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner start(
			BillRepository billRepository,
			ProductItemRepository productItemRepository,
			CustomerRestClient customerRestClient,
			ProductItemRestClient productItemRestClient){
		return args -> {

			Customer customer = customerRestClient.getCustomerById(1L);
			Bill bill1=billRepository.save(new Bill(null,new Date(),null,customer.getId(),null));
			PagedModel<Product> productPagedModel = productItemRestClient.pageProducts();
			productPagedModel.forEach(p->{
				ProductItem productItem = new ProductItem();
				productItem.setPrice(p.getPrice());
				productItem.setQuantity(1+new Random().nextInt(100));
				productItem.setBill(bill1);
				productItem.setProductID(p.getId());
				productItemRepository.save(productItem);
			});

			Customer customer2 = customerRestClient.getCustomerById(2L);
			Bill bill2=billRepository.save(new Bill(null,new Date(),null,customer2.getId(),null));
			PagedModel<Product> productPagedModel2 = productItemRestClient.pageProducts();
			productPagedModel2.forEach(p->{
				ProductItem productItem2 = new ProductItem();
				productItem2.setPrice(p.getPrice());
				productItem2.setQuantity(1+new Random().nextInt(100));
				productItem2.setBill(bill2);
				productItem2.setProductID(p.getId());
				productItemRepository.save(productItem2);
			});

			Customer customer3 = customerRestClient.getCustomerById(3L);
			Bill bill3=billRepository.save(new Bill(null,new Date(),null,customer3.getId(),null));
			PagedModel<Product> productPagedModel3 = productItemRestClient.pageProducts();
			productPagedModel3.forEach(p->{
				ProductItem productItem3 = new ProductItem();
				productItem3.setPrice(p.getPrice());
				productItem3.setQuantity(1+new Random().nextInt(100));
				productItem3.setBill(bill3);
				productItem3.setProductID(p.getId());
				productItemRepository.save(productItem3);
			});
		};
	}
}

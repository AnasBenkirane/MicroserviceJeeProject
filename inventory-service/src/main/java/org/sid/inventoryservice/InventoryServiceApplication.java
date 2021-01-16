package org.sid.inventoryservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
 CommandLineRunner start(ProductRepository productRepository, RepositoryRestConfiguration restConfiguration){
        restConfiguration.exposeIdsFor(Product.class);
        return args -> {
         productRepository.save(new Product(null,"Ordinateur",1200.0,120));
         productRepository.save(new Product(null,"Imprimante",1200.0,120));
         productRepository.save(new Product(null,"Souris",1200.0,120));
         productRepository.findAll().forEach(c->{
             System.out.println(c.getName());
         });
        };
 }
}

@Entity
@Data @AllArgsConstructor @NoArgsConstructor @ToString
class Product{
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private double quantity;
}

@RepositoryRestResource
@CrossOrigin
interface ProductRepository extends JpaRepository<Product,Long>{

}
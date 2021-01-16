package org.sid.billingservice.web;

import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.entities.ProductItem;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.feign.ProductItemRestClient;
import org.sid.billingservice.model.Customer;
import org.sid.billingservice.model.Product;
import org.sid.billingservice.repository.BillRepository;
import org.sid.billingservice.repository.ProductItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;




@RestController
@CrossOrigin
public class BillingRestController {
    private BillRepository billRepository;
    private ProductItemRepository productItemRepository;
    private CustomerRestClient customerRestClient;
    private ProductItemRestClient productItemRestClient;

    public BillingRestController(BillRepository billRepository, ProductItemRepository productItemRepository, CustomerRestClient customerRestClient, ProductItemRestClient productItemRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productItemRestClient = productItemRestClient;
    }

    @GetMapping(path = "/fullBill/{id}")
    public Bill getBill(@PathVariable(name = "id") Long id){
       Bill bill = billRepository.findById(id).get();
        Customer customer = customerRestClient.getCustomerById(bill.getCustomerID());
        bill.setCustomer(customer);
       bill.getProductItems().forEach(productItem -> {
           Product product = productItemRestClient.getProductById(productItem.getProductID());
           productItem.setProduct(product);
       });
       return bill;
    }

    @GetMapping(path = "/fullBills")
    Page<Bill> getAllBills(Pageable pageable){
        Page<Bill> bills= billRepository.findAll(pageable);
        bills.forEach(bill -> {
            Customer customer = customerRestClient.getCustomerById(bill.getCustomerID());
           bill.setCustomer(customer);

            bill.getProductItems().forEach(productItem -> {
                Product product = productItemRestClient.getProductById(productItem.getProductID());
                productItem.setProduct(product);
            });
        });
        return bills;
    }
}

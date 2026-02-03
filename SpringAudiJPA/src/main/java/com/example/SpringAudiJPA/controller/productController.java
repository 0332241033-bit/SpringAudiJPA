package com.example.SpringAudiJPA.controller;

import com.example.SpringAudiJPA.entity.product;
import com.example.SpringAudiJPA.repository.productRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//es usado para evitar inyectar dependencias con el autowired
//lombok lo hace

public class productController {

    private final productRepository productRepository;

    //CREATE PRODUCT

    @PostMapping("/create")
    public ResponseEntity<String> addProduct(@RequestBody product produ){


        this.productRepository.save(produ);
            return new ResponseEntity<>("Product added successfully", HttpStatus.CREATED);

    }

    // FIND PRODUCT

    @GetMapping("/findById")
    public ResponseEntity<product> findById(Integer id){
        this.productRepository.findById(id);
        return ResponseEntity.ok().build();
    }





    //UPDATE PRODUCT

   @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProduct(@RequestBody product prod,@PathVariable int id){
        product productFound  = this.productRepository.findById(id).orElseThrow();
        productFound.setName(prod.getName());
        productFound.setPrice(prod.getPrice());
        this.productRepository.save(productFound);
        return new ResponseEntity<>("Product Update",HttpStatus.OK);
    }


    //DELETE PRODUCT

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id){
        this.productRepository.deleteById(id);
        return new ResponseEntity<>("Product Deleted",HttpStatus.OK);
    }





}

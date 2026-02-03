package com.example.SpringAudiJPA.repository;

import com.example.SpringAudiJPA.entity.product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface productRepository extends CrudRepository<product,Integer> {
                                            //JPA REPOSITORY
                                           //TAMBIEN SE PUEDE USAR
    //TIENE DIFERENTES PROPOSITOS PERO ES LO MISMO


}

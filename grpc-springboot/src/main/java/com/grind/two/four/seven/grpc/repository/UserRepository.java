package com.grind.two.four.seven.grpc.repository;

import com.grind.two.four.seven.grpc.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
}

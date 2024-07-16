package com.main.vlbi.repositorys;

import com.main.vlbi.models.User;
import com.main.vlbi.models.planning.Observation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository("UserRepository")
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

	ArrayList<String> findByRolesName(String string);

	ArrayList<User> findByObservations(Observation obs);


}

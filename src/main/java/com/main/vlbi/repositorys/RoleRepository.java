package com.main.vlbi.repositorys;

import com.main.vlbi.models.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("RoleRepository")
public interface RoleRepository extends CrudRepository<Role, Long> {

	Role findByName(String string);
}

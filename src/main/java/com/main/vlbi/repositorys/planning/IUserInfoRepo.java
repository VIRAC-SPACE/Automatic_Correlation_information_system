package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.User;
import com.main.vlbi.models.planning.UserInfo;
@Repository("IUserInfoRepo")
public interface IUserInfoRepo extends CrudRepository<UserInfo, Long> {

	UserInfo findByUser(User mainUser);

}

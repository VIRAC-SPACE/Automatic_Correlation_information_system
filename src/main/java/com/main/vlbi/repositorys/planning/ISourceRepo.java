package com.main.vlbi.repositorys.planning;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.Source;


@Repository("ISourceRepo")
public interface ISourceRepo extends CrudRepository<Source, Long> {


	ArrayList<Source> findByTitleContains(String title);

	boolean existsByTitle(String title);

	Source findByTitle(String source);

}

package com.main.vlbi.repositorys.planning;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.DataProcessing;
@Repository("IDataProcessingRepo")
public interface IDataProcessingRepo extends CrudRepository<DataProcessing, Long> {

	ArrayList<DataProcessing> findByObservationsExpcode(String title);

}


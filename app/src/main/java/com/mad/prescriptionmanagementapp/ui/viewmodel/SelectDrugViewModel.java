package com.mad.prescriptionmanagementapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.mad.prescriptionmanagementapp.data.mapper.DrugMapper;
import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.data.repository.DrugRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

public class SelectDrugViewModel extends AndroidViewModel {
    private final DrugRepository drugRepository;
    @Getter
    private final LiveData<List<SimpleDrug>> originalDrugList;

    public SelectDrugViewModel(@NonNull Application application) {
        super(application);
        this.drugRepository = new DrugRepository(application);
        this.originalDrugList = Transformations.map(this.drugRepository.getCachedDrugs(), entities -> {
            if (entities == null) return null;
            return entities.stream()
                    .map(DrugMapper::cacheToResponse)
                    .collect(Collectors.toList());
        });;
    }
}

package com.mad.prescriptionmanagementapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.mad.prescriptionmanagementapp.data.remote.dto.response.SimpleDrug;
import com.mad.prescriptionmanagementapp.util.Frequency;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugInPres implements Parcelable {
    private SimpleDrug simpleDrug;
    private Unit unit;
    private String startDate;
    private List<TimeDosage> timeDosages = new ArrayList<>();
    private Frequency frequency = Frequency.DAILY;
    private Integer everyNDays; // nếu type = EVERY_N_DAYS
    private List<Integer> specificDays; // nếu type = SPECIFIC_DATES
    private String note;

    public DrugInPres(SimpleDrug simpleDrug) {
        this.simpleDrug = simpleDrug;
        this.timeDosages = new ArrayList<>();
    }

    protected DrugInPres(Parcel in) {
        simpleDrug = in.readParcelable(SimpleDrug.class.getClassLoader());
        startDate = in.readString();
        everyNDays = in.readInt();
        note = in.readString();
    }

    public static final Creator<DrugInPres> CREATOR = new Creator<DrugInPres>() {
        @Override
        public DrugInPres createFromParcel(Parcel in) {
            return new DrugInPres(in);
        }

        @Override
        public DrugInPres[] newArray(int size) {
            return new DrugInPres[size];
        }
    };

    public void addTimeDosage(TimeDosage timeDosage) {
       this.timeDosages.add(timeDosage);
    }

    public DrugInPres(DrugInPres original) {
        this.simpleDrug = original.simpleDrug;
        this.timeDosages = new ArrayList<>(original.timeDosages); // shallow copy, đủ xài nếu TimeDosage immutable
    }

    public Drug getDrug() {
        Drug res = new Drug();
        res.setId(this.simpleDrug.getId());
        res.setName(this.simpleDrug.getName());
        return res;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(simpleDrug, i);
        parcel.writeString(startDate);
        parcel.writeInt(everyNDays);
        parcel.writeString(note);
    }
}

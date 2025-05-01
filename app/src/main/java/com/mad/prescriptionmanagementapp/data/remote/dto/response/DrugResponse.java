package com.mad.prescriptionmanagementapp.data.remote.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugResponse implements Parcelable {
    private Long id;
    private String name;

    protected DrugResponse(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
    }

    public static final Creator<DrugResponse> CREATOR = new Creator<DrugResponse>() {
        @Override
        public DrugResponse createFromParcel(Parcel in) {
            return new DrugResponse(in);
        }

        @Override
        public DrugResponse[] newArray(int size) {
            return new DrugResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(name);
    }
}

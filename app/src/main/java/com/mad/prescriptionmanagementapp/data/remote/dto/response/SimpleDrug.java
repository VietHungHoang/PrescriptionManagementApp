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
public class SimpleDrug implements Parcelable {
    private Long id;
    private String name;

    protected SimpleDrug(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
    }

    public static final Creator<SimpleDrug> CREATOR = new Creator<SimpleDrug>() {
        @Override
        public SimpleDrug createFromParcel(Parcel in) {
            return new SimpleDrug(in);
        }

        @Override
        public SimpleDrug[] newArray(int size) {
            return new SimpleDrug[size];
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

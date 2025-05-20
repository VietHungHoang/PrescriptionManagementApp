package com.mad.prescriptionmanagementapp.data.remote.dto.response.kiet;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MedicineResponse implements Parcelable {
    private String date;
    private List<TimeDosage> timeDosages;

    public String getDate() {
        return date;
    }

    public List<TimeDosage> getTimeDosages() {
        return timeDosages;
    }

    // Parcelable constructor
    protected MedicineResponse(Parcel in) {
        date = in.readString();
        timeDosages = in.createTypedArrayList(TimeDosage.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeTypedList(timeDosages);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MedicineResponse> CREATOR = new Creator<MedicineResponse>() {
        @Override
        public MedicineResponse createFromParcel(Parcel in) {
            return new MedicineResponse(in);
        }

        @Override
        public MedicineResponse[] newArray(int size) {
            return new MedicineResponse[size];
        }
    };

    public static class TimeDosage implements Parcelable {
        private Long id;
        private String time;
        private int status;
        private boolean editted;
        private List<Drug> drugs;

        public Long getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

        public int getStatus() {
            return status;
        }

        public boolean isEditted() {
            return editted;
        }

        public List<Drug> getDrugs() {
            return drugs;
        }

        protected TimeDosage(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readLong();
            }
            time = in.readString();
            status = in.readInt();
            editted = in.readByte() != 0;
            drugs = in.createTypedArrayList(Drug.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (id == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(id);
            }
            dest.writeString(time);
            dest.writeInt(status);
            dest.writeByte((byte) (editted ? 1 : 0));
            dest.writeTypedList(drugs);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TimeDosage> CREATOR = new Creator<TimeDosage>() {
            @Override
            public TimeDosage createFromParcel(Parcel in) {
                return new TimeDosage(in);
            }

            @Override
            public TimeDosage[] newArray(int size) {
                return new TimeDosage[size];
            }
        };
    }

    public static class Drug implements Parcelable {
        private String name;
        private double dosage;
        private String unit;

        public String getName() {
            return name;
        }

        public double getDosage() {
            return dosage;
        }

        public String getUnit() {
            return unit;
        }

        protected Drug(Parcel in) {
            name = in.readString();
            dosage = in.readDouble();
            unit = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeDouble(dosage);
            dest.writeString(unit);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Drug> CREATOR = new Creator<Drug>() {
            @Override
            public Drug createFromParcel(Parcel in) {
                return new Drug(in);
            }

            @Override
            public Drug[] newArray(int size) {
                return new Drug[size];
            }
        };
    }
}

package com.example.hsebastian.popularmoviesvol1;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hsebastian on 10/13/15.
 */
// access modifiers, accessors and regular constructors ommited for brevity
class ParcelableMovieInfo implements Parcelable {

    private final String LOG_TAG = ParcelableMovieInfo.class.getSimpleName();
    private HashMap<String, String> mMovieInfo;

    public ParcelableMovieInfo(HashMap movieInfo){
        mMovieInfo = movieInfo;
    }

    private ParcelableMovieInfo(Parcel in) {

        //initialize your map before
        int size = in.readInt();
        if (size > 0) {
            mMovieInfo = new HashMap<>();
            for(int i = 0; i < size; i++){
                String key = in.readString();
                String value = in.readString();
                mMovieInfo.put(key,value);
            }
        }
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeInt(mMovieInfo.size());
        for(Map.Entry<String, String> entry : mMovieInfo.entrySet()){
            out.writeString(entry.getKey());
            out.writeString(entry.getValue());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ParcelableMovieInfo> CREATOR =
        new Parcelable.Creator<ParcelableMovieInfo>() {

        @Override
        public ParcelableMovieInfo createFromParcel(Parcel source) {
            // Using the private parcelable constructor
            return new ParcelableMovieInfo(source);
        }

        @Override
        public ParcelableMovieInfo[] newArray(int size) {
            return new ParcelableMovieInfo[size];
        }
    };
}



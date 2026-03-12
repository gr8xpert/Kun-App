package com.example.kunworld.data.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) {
            return null;
        }
        return gson.toJson(list);
    }

    @TypeConverter
    public static Set<Integer> toIntegerSet(String value) {
        if (value == null) {
            return new HashSet<>();
        }
        Type setType = new TypeToken<Set<Integer>>() {}.getType();
        return gson.fromJson(value, setType);
    }

    @TypeConverter
    public static String fromIntegerSet(Set<Integer> set) {
        if (set == null) {
            return null;
        }
        return gson.toJson(set);
    }
}

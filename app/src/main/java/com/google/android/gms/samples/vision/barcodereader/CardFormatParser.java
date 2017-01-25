package com.google.android.gms.samples.vision.barcodereader;

/**
 * Created by James on 1/19/2017.
 */

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CardFormatParser {
    public List<CardFormat> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public CardFormat findInJsonStream(InputStream in, ValueTypeEnum valueType, int length) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return checkMessagesArray(reader, valueType, length);
        } finally {
            reader.close();
        }
    }

    //parse the array of formats and see if any match the given parameters
    public CardFormat checkMessagesArray(JsonReader reader, ValueTypeEnum valueType, int length) throws IOException {
        CardFormat message = null;

        reader.beginArray();
        while (reader.hasNext() && message == null) {
            message = checkMessage(reader, valueType, length);
        }
        reader.endArray();
        return message;
    }

    public List<CardFormat> readMessagesArray(JsonReader reader) throws IOException {
        List<CardFormat> messages = new ArrayList<CardFormat>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    public CardFormat checkMessage(JsonReader reader, ValueTypeEnum valueType, int length) throws IOException {
        String formatName = "";
        int id = 0;
        String formatValue = "";
        ValueTypeEnum selfValueType = null;
        int valueLength = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                formatName= reader.nextString();
            } else if (name.equals("formatInt")) {
                id = reader.nextInt();
            } else if (name.equals("formatValue")) {
                formatValue = reader.nextString();
            } else if (name.equals("valueType")) {
                selfValueType = ValueTypeEnum.valueOf(reader.nextString());
            } else if (name.equals("valueLength")) {
                valueLength = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        CardFormat parsedFormat = new CardFormat(formatName, id, formatValue, selfValueType, valueLength);
        if(parsedFormat.valueType.equals(valueType) && parsedFormat.length == length) {
            return parsedFormat;
        }
        return null;
    }

    public CardFormat readMessage(JsonReader reader) throws IOException {
        String formatName = "";
        int id = 0;
        String formatValue = "";
        ValueTypeEnum valueType = null;
        int valueLength = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                formatName= reader.nextString();
            } else if (name.equals("formatInt")) {
                id = reader.nextInt();
            } else if (name.equals("formatValue")) {
                formatValue = reader.nextString();
            } else if (name.equals("valueType")) {
                valueType = ValueTypeEnum.valueOf(reader.nextString());
            } else if (name.equals("valueLength")) {
                valueLength = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new CardFormat(formatName, id, formatValue, valueType, valueLength);
    }
}
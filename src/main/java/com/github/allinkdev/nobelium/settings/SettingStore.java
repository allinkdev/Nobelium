package com.github.allinkdev.nobelium.settings;

import com.github.allinkdev.nobelium.Nobelium;
import com.github.allinkdev.nobelium.settings.enums.TypeOfService;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SettingStore implements JsonSerializer<SettingStore>, JsonDeserializer<SettingStore> {
    private static final Path PARENT = Path.of("config", "nobelium");
    private static final Path PATH = PARENT.resolve("nobelium.json");
    public final Setting<TypeOfService> typeOfService;
    public final Setting<Boolean> fastOpen;
    public final Setting<Boolean> disableSocketKeepAlive;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(new TypeToken<SettingStore>() {
                //
            }.getType(), this)
            .create();

    public SettingStore() {
        final SettingFactory settingFactory = new SettingFactory(this);

        this.typeOfService = settingFactory.create(TypeOfService.Flash);
        this.fastOpen = settingFactory.create(Boolean.TRUE);
        this.disableSocketKeepAlive = settingFactory.create(Boolean.TRUE);
    }

    public static SettingStore createNew() {
        return new SettingStore();
    }

    private void checkExists() throws IOException {
        if (!Files.exists(PATH)) {
            Files.createDirectories(PARENT);
            this.serialize();
        }
    }

    public SettingStore deserializeInto() throws IOException {
        checkExists();

        final InputStream inputStream = Files.newInputStream(PATH);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        return gson.fromJson(inputStreamReader, SettingStore.class);
    }

    public synchronized void save() {
        try {
            this.serialize();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save settings", e);
        }
    }

    public void serialize() throws IOException {
        final String jsonified = gson.toJson(this);
        final byte[] jsonBytes = jsonified.getBytes(StandardCharsets.UTF_8);

        final OutputStream outputStream = Files.newOutputStream(PATH);

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            bufferedOutputStream.write(jsonBytes);
            bufferedOutputStream.flush();
        }
    }

    @SuppressWarnings("java:S4968")
    private Field[] getFields() {
        final Class<? extends SettingStore> clazz = this.getClass();
        return clazz.getDeclaredFields();
    }

    @SuppressWarnings({"java:S3011", "unchecked"})
    @Override
    public SettingStore deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Not a json object!");
        }

        final JsonObject jsonObject = json.getAsJsonObject();
        final Field[] fields = this.getFields();

        for (final Field field : fields) {
            final String fieldName = field.getName();
            final Object value;

            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                Nobelium.LOGGER.warn("Could not get the value of setting store field {} while trying to deserialize it", fieldName, e);
                continue;
            }

            if (!(value instanceof final Setting<?> setting)) {
                continue;
            }

            if (!jsonObject.has(fieldName)) {
                Nobelium.LOGGER.warn("Using defaults for {} as it is not present in the JSON object...", fieldName);
                continue;
            }

            final JsonElement settingElement = jsonObject.get(fieldName);
            final Setting<?> deserialized = setting.deserialize(this.gson, settingElement, this);
            final Setting<Object> settingField;

            try {
                settingField = (Setting<Object>) field.get(this);
            } catch (IllegalAccessException e) {
                Nobelium.LOGGER.error("Failed to get setting field!", e);
                break;
            }

            final Object deserializedValue = deserialized.get().join();
            settingField.set(deserializedValue);
        }

        return this;
    }

    @Override
    public JsonElement serialize(final SettingStore src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        final Field[] fields = this.getFields();

        for (final Field field : fields) {
            final String fieldName = field.getName();
            final Object value;

            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                Nobelium.LOGGER.warn("Could not get the value of setting store field {} while trying to serialize it", fieldName, e);
                continue;
            }

            if (!(value instanceof final Setting<?> setting)) {
                continue;
            }

            jsonObject.add(fieldName, setting.serialize(context));
        }

        return jsonObject;
    }
}
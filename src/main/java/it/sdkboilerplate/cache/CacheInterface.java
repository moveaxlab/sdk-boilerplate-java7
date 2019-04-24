package it.sdkboilerplate.cache;

public interface CacheInterface {
    String get(String key);

    void set(String key, String value);

    void delete(String key);
}

package me.fertiz.netflux.util;

import java.util.function.Function;

public class Result<T> {
    private final T value;
    private final Throwable error;

    private Result(T value, Throwable error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> of(SupplierWithException<T> supplier) {
        try {
            return new Result<>(supplier.get(), null);
        } catch (Throwable e) {
            return new Result<>(null, e);
        }
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (error != null) {
            return new Result<>(null, error);
        }
        return new Result<>(mapper.apply(value), null);
    }

    public <E extends Throwable> Result<T> exception(Class<E> exceptionType, Function<E, T> recovery) {
        if (exceptionType.isInstance(error)) {
            return new Result<>(recovery.apply(exceptionType.cast(error)), null);
        }
        return this;
    }

    public T recover(T defaultValue) {
        return error != null ? defaultValue : value;
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Throwable;
    }
}

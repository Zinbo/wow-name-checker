package com.stacktobasics.wownamechecker.alert.infra;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public sealed interface Result<T> permits Result.Ok, Result.Error {

    static <T> Result<T> ok(final T value) {
        requireNonNull(value, "The value of a Result cannot be null");
        return new Ok<>(value);
    }

    static <T, E extends Throwable> Result<T> error(final E throwable) {
        requireNonNull(throwable, "The error of a Result cannot be null");
        return new Error<>(throwable);
    }

    static <T> Result<T> of(final Supplier<T> supplier) {
        requireNonNull(supplier, "The value supplier cannot be null");

        try {
            return ok(supplier.get());
        } catch (final Exception error) {
            return error(error);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> Result<T> of(final Optional<T> optional) {
        requireNonNull(optional, "The optional value cannot be null");

        return optional
                .map(Result::ok)
                .orElseGet(() -> error(new NoSuchElementException("No value present when unwrapping the optional")));
    }

    static <T> Result<T> ofNullable(final T value) {
        return ofNullable(value, () -> new NullPointerException("The result was initialized with a null value"));
    }

    static <T> Result<T> ofNullable(final T value, final Supplier<? extends Throwable> errorSupplier) {
        requireNonNull(errorSupplier, "The error supplier cannot be null");

        return nonNull(value)
                ? ok(value)
                : error(errorSupplier.get());
    }

    boolean isOk();

    void ifOk(final Consumer<T> consumer);

    void ifOkOrElse(final Consumer<T> okFunc, Consumer<Throwable> errorFunc);

    boolean isError();

    void ifError(final Consumer<Throwable> consumer);

    Result<T> switchIfError(final Function<Throwable, Result<T>> fallbackMethod);

    <U> Result<U> map(final Function<? super T, ? extends U> mapper);

    <U> Result<U> flatMap(final Function<? super T, Result<U>> mapper);

    Result<T> mapError(final Function<Throwable, ? extends Throwable> mapper);

    T get();

    T getOrElse(final Supplier<T> supplier);

    Throwable getError();

    record Ok<T>(T value) implements Result<T> {

        static Ok<Void> emptyOk = new Ok<Void>(null);

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public void ifOk(final Consumer<T> consumer) {
            requireNonNull(consumer, "The value consumer cannot be null");
            consumer.accept(value);
        }

        @Override
        public void ifOkOrElse(Consumer<T> okFunc, Consumer<Throwable> errorFunc) {
            ifOk(okFunc);
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public void ifError(final Consumer<Throwable> consumer) {
            // Do nothing when trying to consume the error of an Ok result.
        }

        @Override
        public Result<T> switchIfError(final Function<Throwable, Result<T>> fallbackMethod) {
            return new Ok<>(value);
        }

        @Override
        public <U> Result<U> map(final Function<? super T, ? extends U> mapper) {
            requireNonNull(mapper, "The value mapper cannot be null");
            return new Ok<>(mapper.apply(value));
        }

        @Override
        public <U> Result<U> flatMap(final Function<? super T, Result<U>> mapper) {
            requireNonNull(mapper, "The value flat-mapper cannot be null");
            return mapper.apply(value);
        }

        @Override
        public Result<T> mapError(final Function<Throwable, ? extends Throwable> mapper) {
            return new Ok<>(value);
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public T getOrElse(final Supplier<T> supplier) {
            return value;
        }

        @Override
        public Throwable getError() {
            throw new NoSuchElementException("Result contains a value: " + value.toString());
        }

    }

    record Error<T>(Throwable throwable) implements Result<T> {

        @SuppressWarnings("unchecked")
        private <E extends Throwable> T propagate(final Throwable throwable) throws E {
            throw (E) throwable;
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public void ifOk(final Consumer<T> consumer) {
            // Do nothing when trying to consume the value of an Error result.
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public void ifError(final Consumer<Throwable> consumer) {
            requireNonNull(consumer, "The error consumer cannot be null");
            consumer.accept(throwable);
        }

        @Override
        public void ifOkOrElse(Consumer<T> okFunc, Consumer<Throwable> errorFunc) {
            ifError(errorFunc);
        }

        @Override
        public Result<T> switchIfError(final Function<Throwable, Result<T>> fallbackMethod) {
            requireNonNull(fallbackMethod, "The fallback method cannot be null");
            return fallbackMethod.apply(throwable);
        }

        @Override
        public <U> Result<U> map(final Function<? super T, ? extends U> mapper) {
            return new Error<>(throwable);
        }

        @Override
        public <U> Result<U> flatMap(final Function<? super T, Result<U>> mapper) {
            return new Error<>(throwable);
        }

        @Override
        public Result<T> mapError(final Function<Throwable, ? extends Throwable> mapper) {
            requireNonNull(mapper, "The error mapper cannot be null");
            return new Error<>(mapper.apply(throwable));
        }

        @Override
        public T get() {
            return propagate(throwable);
        }

        @Override
        public T getOrElse(final Supplier<T> supplier) {
            requireNonNull(supplier);
            return supplier.get();
        }

        @Override
        public Throwable getError() {
            return throwable;
        }
    }
}

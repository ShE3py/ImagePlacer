package fr.she3py.iplacer.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class Arguments {
	@Contract(value = "false, _, _ -> fail", pure = true)
	public static <E extends RuntimeException, T> void require(boolean op, Function<T, E> exception, T description) throws E {
		if(!op)
			throw exception.apply(description);
	}
	
	@Contract(value = "false, _ -> fail", pure = true)
	public static void require(boolean op, String message) {
		require(op, IllegalArgumentException::new, message);
	}
	
	@Contract(value = "_, null -> fail", pure = true)
	public static void requireEmpty(String name, @Nullable Collection<?> obj) {
		requireNonNull(name, obj);
		require(obj.isEmpty(), name + " must be empty");
	}
	
	@Contract(value = "_, null -> fail", pure = true)
	public static void requireEmpty(String name, @Nullable Map<?, ?> obj) {
		requireNonNull(name, obj);
		require(obj.isEmpty(), name + " must be empty");
	}
	
	@Contract(value = "_, null -> fail", pure = true)
	public static void requireEmpty(String name, @Nullable String obj) {
		requireNonNull(name, obj);
		require(obj.isEmpty(), name + " must be empty");
	}
	
	public static <T, S extends T> void requireEqual(String name, @Nullable S a, T b) {
		require(Objects.equals(a, b), name + " must be equal to " + b.toString() + ", got " + Optional.ofNullable(a).map(Object::toString).orElse("null"));
	}
	
	@Contract(value = "true, _, _ -> fail", pure = true)
	public static <E extends RuntimeException, T> void requireFalse(boolean op, Function<T, E> exception, T description) {
		require(!op, exception, description);
	}
	
	@Contract(value = "true, _ -> fail", pure = true)
	public static void requireFalse(boolean op, String message) {
		require(!op, message);
	}
	
	@Contract(value = "_, true -> fail", pure = true)
	public static void requireFalse(String name, boolean val) {
		require(!val, name + " must be false");
	}
	
	@Contract(value = "_, null -> fail", pure = true)
	public static void requireNonEmpty(String name, @Nullable Collection<?> obj) {
		requireNonNull(name, obj);
		require(!obj.isEmpty(), name + " must not be empty");
	}
	
	@Contract(value = "_, null -> fail", pure = true)
	public static void requireNonEmpty(String name, @Nullable Map<?, ?> obj) {
		requireNonNull(name, obj);
		require(!obj.isEmpty(), name + " must not be empty");
	}
	
	@Contract(value = "_, null -> fail", pure = true)
	public static void requireNonEmpty(String name, @Nullable String obj) {
		requireNonNull(name, obj);
		require(!obj.trim().isEmpty(), name + " must not be empty");
	}
	
	public static <T, S extends T> void requireNonEqual(String name, @Nullable S a, T b) {
		require(a == null || !a.equals(b), name + " must not be equal to " + b.toString());
	}
	
	@Contract(value = "_, null -> fail", pure = true)
	public static <T> void requireNonNull(String name, @Nullable T obj) {
		require(obj != null, NullPointerException::new, name + " must not be null");
	}
	
	@Contract(value = "_, !null -> fail", pure = true)
	public static <T> void requireNull(String name, @Nullable T obj) {
		require(obj == null, name + " must be null");
	}
	
	@Contract(value = "_, false -> fail", pure = true)
	public static void requireTrue(String name, boolean val) {
		require(val, name + " must be true");
	}
	
	@Contract(value = "_, _ -> fail", pure = true)
	public static <E extends RuntimeException, T> void fail(Function<T, E> exception, T description) {
		//noinspection ConstantConditions
		require(false, exception, description);
	}
	
	@Contract(value = "_ -> fail", pure = true)
	public static void fail(String message) {
		//noinspection ConstantConditions
		require(false, message);
	}
}
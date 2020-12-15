package fr.she3py.iplacer.storage.base;

import java.util.Stack;

import org.jetbrains.annotations.NotNull;

import fr.she3py.iplacer.util.Arguments;

public class CompositionStack extends Stack<IBinarySerializable> {
	@Override
	public synchronized void addElement(IBinarySerializable object) {
		Arguments.requireNonNull("object", object);
		
		super.addElement(object);
	}
	
	@NotNull
	public <E extends IBinarySerializable> E peek(Class<E> klass) {
		return klass.cast(peek());
	}
	
	public void ensureInside(Class<? extends IBinarySerializable> klass) {
		peek(klass);
	}
	
	@NotNull
	public <E extends IBinarySerializable> E pop(Class<E> klass) {
		return klass.cast(pop());
	}
	
	@Override
	public CompositionStack clone() {
		return (CompositionStack) super.clone();
	}
}

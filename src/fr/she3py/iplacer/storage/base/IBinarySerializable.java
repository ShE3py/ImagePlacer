package fr.she3py.iplacer.storage.base;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IBinarySerializable {
	public void serialize(IBinaryWriter writer) throws IOException;
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface SerializeWithChecksum {
	
	}
}

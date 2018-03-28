package cc.co.llabor.system;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;
 


/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  04.04.2011::10:43:09<br> 
 */
public class ClientLauncher implements Runnable, Instrumentation {
 
	@Override
	public void run(){
 
	}
 
	@Override
	public void addTransformer(ClassFileTransformer transformer,
			boolean canRetransform) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		}
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		}
	}

	@Override
	public boolean removeTransformer(ClassFileTransformer transformer) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		return false;
		}
	}

	@Override
	public boolean isRetransformClassesSupported() {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		return false;
		}
	}

	@Override
	public void retransformClasses(Class<?>... classes)
			throws UnmodifiableClassException {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		}
	}

	@Override
	public boolean isRedefineClassesSupported() {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		return false;
		}
	}

	@Override
	public void redefineClasses(ClassDefinition... definitions)
			throws ClassNotFoundException, UnmodifiableClassException {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		}
	}

	@Override
	public boolean isModifiableClass(Class<?> theClass) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		return false;
		}
	}

	@Override
	public Class[] getAllLoadedClasses() {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		return null;
		}
	}

	@Override
	public Class[] getInitiatedClasses(ClassLoader loader) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		return null;
		}
	}

 
	@Override
	public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		}
	}

	@Override
	public void appendToSystemClassLoaderSearch(JarFile jarfile) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		}
	}

	@Override
	public boolean isNativeMethodPrefixSupported() {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		return false;
		}
	}

	@Override
	public void setNativeMethodPrefix(ClassFileTransformer transformer,
			String prefix) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 17.10.2012");
		else {
		}
	}

	@Override
	public long getObjectSize(Object objectToSize) {
		// TODO Auto-generated method stub
		return 0;
	}

}


 
package org.red5.server.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is a facade for Constructor and Method
 * - they're so similar to each other that it helps to
 * be able to treat them in the same way.
 * <p>
 * Unless otherwise documented, each method calls the
 * equivalent method on either the contained Method or
 * the contained Constructor.
 * <p>
 * Convenience methods are also provided to give
 * equivalents of Class.getConstructors etc. Each convenience
 * method merely wraps the result(s) of its equivalent method in
 * Class within an/some Invokable facade(s).
 */
public class Invokable
{
	/** Method this is a facade for, if any */
	private final Method method;
	
	/** Constructor this is a facade for, if any */
	private final Constructor ctor;
	
	/** 
	 * Whether or not this is a facade for a method.
	 * The <code>method</code> is non-null if and only
	 * if this is true.
	 */
	private final boolean isMethodFacade;
	
	/**
	 * Constructor making an Invokable facade for a Constructor.
	 * 
	 * @param ctor constructor to construct a facade for
	 * 
	 * @throws NullPointerException if ctor is null
	 */
	public Invokable (Constructor ctor)
	{
		if (ctor == null)
		    throw new NullPointerException 
		    ("Null ctor passed to Invokable constructor");
		
		this.ctor = ctor;
        method=null;
		isMethodFacade=false;
	}

	/**
	 * Constructor making an Invokable facade for a Method.
	 * 
	 * @param method method to construct a facade for
	 * 
	 * @throws NullPointerException if method is null
	 */
	public Invokable (Method method)
	{
		if (method == null)
		    throw new NullPointerException 
		    ("Null method passed to Invokable constructor");
		
		this.method = method;
		ctor=null;
		isMethodFacade=true;
	}
	
	public boolean equals (Object o)
	{
		if (o==null || o.getClass() != this.getClass())
		    return false;
		Invokable inv = (Invokable) o;
		
		if (inv.isMethodFacade ^ this.isMethodFacade)
		    return false;
		    
		return isMethodFacade ? this.method.equals (inv.method) :
		                  this.ctor.equals (inv.ctor);
	}
	
	public int hashCode ()
	{
		return isMethodFacade ? method.hashCode() : ctor.hashCode();
	}
	
	public Class getDeclaringClass()
	{
		return isMethodFacade ? method.getDeclaringClass() : ctor.getDeclaringClass();
	}
	
	public Class[] getExceptionTypes()
	{
		return isMethodFacade ? method.getExceptionTypes() : ctor.getExceptionTypes();
	}
	
	public int getModifiers()
	{
		return isMethodFacade ? method.getModifiers() : ctor.getModifiers();
	}
	
	public String getName()
	{
		return isMethodFacade ? method.getName() : ctor.getName();
	}
	
	public Class[] getParameterTypes()
	{
		return isMethodFacade ? method.getParameterTypes() : ctor.getParameterTypes();
	}
	
	public String toString()
	{
		return isMethodFacade ? method.toString() : ctor.toString();
	}
	
	public Class getReturnType()
	{
		return isMethodFacade ? method.getReturnType() : ctor.getDeclaringClass();
	}
	
	/**
	 * Invokes the method or constructor. Note that due to this dual
	 * nature, InstantiationException is in the thrown exception list,
	 * even though a normal Method.invoke can't throw this. If you
	 * know for sure that you're only dealing with a method, feel free
	 * to just catch it in a no-op catch block.
	 * 
	 * @param obj the object to invoke the method on. This is ignored
	 * if the method is static or if this is a facade for a constructor
	 * 
	 * @param params the parameters to pass into the method or constructor
	 */
	public Object invoke(Object obj, Object[] params)
		throws IllegalAccessException, InstantiationException, InvocationTargetException
	{
		return isMethodFacade ? method.invoke (obj, params) : ctor.newInstance (params);
	}
	
	/**
	 * Returns whether this is a facade for a method (true) or
	 * a constructor (false).
	 */
	public boolean isMethod()
	{
		return isMethodFacade;
	}
	
	/**
	 * Returns whether this is a facade for a constructor (true) or
	 * a method (false). This apparently redundant method
	 * is available purely as an aid to code readability - the
	 * intention of <code>if (inv.isConstructor()) {...}</code> is 
	 * clearer than that of <code>if (!inv.isMethod()) {...}</code>,
	 * in my opinion.
	 */
	public boolean isConstructor()
	{
		return !isMethod();
	}

	/**
	 * Utility method to mirror Class.getConstructor, but returning an Invokable
	 * facade for the result.
	 */
	public static Invokable getConstructor (Class clazz, Class[] parameterTypes)
	    throws NoSuchMethodException
	{
		return new Invokable (clazz.getConstructor (parameterTypes));
	}

	/**
	 * Convenience method to mirror Class.getConstructors, but returning an Invokable
	 * facade for the result.
	 */
	public static Invokable[] getConstructors (Class clazz)
	{
		Constructor[] ctors = clazz.getConstructors();
		Invokable [] ret = new Invokable [ctors.length];
		for (int i=0; i < ctors.length; i++)
		    ret[i]=new Invokable (ctors[i]);
		return ret;
	}
	
	/**
	 * Convenience method to mirror Class.getDeclaredConstructor, but returning an Invokable
	 * facade for the result.
	 */
	public static Invokable getDeclaredConstructor (Class clazz, Class[] parameterTypes)
	    throws NoSuchMethodException
	{
		return new Invokable (clazz.getDeclaredConstructor (parameterTypes));
	}

	/**
	 * Convenience method to mirror Class.getDeclaredConstructors, but returning an Invokable
	 * facade for the result.
	 */
	public static Invokable[] getDeclaredConstructors (Class clazz)
	{
		Constructor[] ctors = clazz.getDeclaredConstructors();
		Invokable [] ret = new Invokable [ctors.length];
		for (int i=0; i < ctors.length; i++)
		    ret[i]=new Invokable (ctors[i]);
		return ret;
	}

	/**
	 * Convenience method to mirror Class.getMethod, but returning an Invokable
	 * facade for the result.
	 */
	public static Invokable getMethod (Class clazz, 
	                                   String name, 
	                                   Class[] parameterTypes)
	    throws NoSuchMethodException
	{
		return new Invokable (clazz.getMethod (name, parameterTypes));
	}

	/**
	 * Convenience method to mirror Class.getMethods, but returning an Invokable
	 * facade for the result.
	 */
	public static Invokable[] getMethods (Class clazz)
	{
		Method[] ctors = clazz.getMethods();
		Invokable [] ret = new Invokable [ctors.length];
		for (int i=0; i < ctors.length; i++)
		    ret[i]=new Invokable (ctors[i]);
		return ret;
	}
	
	/**
	 * Convenience method to mirror Class.getDeclaredMethod, but returning an Invokable
	 * facade for the result.
	 */
	public static Invokable getDeclaredMethod (Class clazz, 
	                                           String name, 
	                                           Class[] parameterTypes)
	    throws NoSuchMethodException
	{
		return new Invokable (clazz.getDeclaredMethod (name, parameterTypes));
	}

	/**
	 * Convenience method to mirror Class.getDeclaredMethods, but returning an Invokable
	 * facade for the result.
	 */
	public static Invokable[] getDeclaredMethods (Class clazz)
	{
		Method[] methods = clazz.getDeclaredMethods();
		Invokable [] ret = new Invokable [methods.length];
		for (int i=0; i < methods.length; i++)
		    ret[i]=new Invokable (methods[i]);
		return ret;
	}
	
	/**
	 * Convenience method to get all constructors <b>and</b> methods
	 * from a class - the result is the aggregate of getMethods and getConstructors
	 */
	public static Invokable[] getInvokables (Class clazz)
	{
		Invokable[] r1 = getConstructors (clazz);
		Invokable[] r2 = getMethods (clazz);
		
		Invokable[] ret = new Invokable[r1.length+r2.length];
		
		System.arraycopy (r1, 0, ret, 0, r1.length);
		System.arraycopy (r2, 0, ret, r1.length, r2.length);
		return ret;
	}

	/**
	 * Convenience method to get all declared constructors <b>and</b> methods
	 * from a class - the result is the aggregate of getMethods and getConstructors
	 */
	public static Invokable[] getDeclaredInvokables (Class clazz)
	{
		Invokable[] r1 = getDeclaredConstructors (clazz);
		Invokable[] r2 = getDeclaredMethods (clazz);
		
		Invokable[] ret = new Invokable[r1.length+r2.length];
		
		System.arraycopy (r1, 0, ret, 0, r1.length);
		System.arraycopy (r2, 0, ret, r1.length, r2.length);
		return ret;
	}
}

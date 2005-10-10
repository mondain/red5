package org.red5.server.service;

import java.lang.reflect.Modifier;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConversionException;

/**
 * Utility class to perform some tricks with reflection. This
 * class is particularly handy for configuration utilities,
 * as it allows easy conversion between classes.
 */
public class ReflectionUtil
{
    /**
	 * Private constructor to stop anyone from instantiating
	 * this class - the static methods should be used
	 * explicitly.
	 */
	private ReflectionUtil()
	{
	}
	
	private static final Class[] PRIMITIVES = 
	{   
		boolean.class, byte.class, char.class, short.class, 
		int.class, long.class, float.class, double.class
	};
	
	private static final Class[] WRAPPERS =
	{	
		Boolean.class, Byte.class, Character.class, Short.class,
		Integer.class, Long.class, Float.class, Double.class
	};

	private static final Class[][] PARAMETER_CHAINS =
	{
		{boolean.class, null},
		{byte.class, Short.class},
		{char.class, Integer.class},
		{short.class, Integer.class},
		{int.class, Long.class},
		{long.class, Float.class},
		{float.class, Double.class},
		{double.class, null}
	};
	
	/** Mapping of primitives to wrappers */	
	private static Map primitiveMap = new HashMap();
	/** Mapping of wrappers to primitives */
	private static Map wrapperMap = new HashMap();
	/** 
	 * Mapping from wrapper class to appropriate parameter types (in order) 
	 * Each entry is an array of Classes, the last of which is either null
	 * (for no chaining) or the next class to try
	 */
	private static Map parameterMap = new HashMap();

	/** Default number format */
	private static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
	
	static
	{
		for (int i=0; i < PRIMITIVES.length; i++)
		{
			primitiveMap.put (PRIMITIVES[i], WRAPPERS[i]);
			wrapperMap.put (WRAPPERS[i], PRIMITIVES[i]);
			parameterMap.put (WRAPPERS[i], PARAMETER_CHAINS[i]);
		}
	}
	
	/**
	 * Converts a String to an instance of a specified wrapper
	 * class, if possible. Note that for boolean 
	 * targets with a String source, the convertToBoolean method is 
	 * called, so any valid argument to convertToBoolean is a valid
	 * source. The default NumberFormat (as obtained by 
	 * NumberFormat.getInstance()) is used if the appropriate decode(String) 
	 * method fails. If parsing with the
	 * NumberFormat is successful, convertToWrapper (Number, target, boolean)
	 * is called, with a value of "false" for the lossOfAccuracy parameter
	 * (so that only lossless conversions are allowed).
	 * 
	 * @param source String to convert
	 * @param target wrapper class to convert to
	 * 
	 * @throws NullPointerException if either target or source are null
	 * @throws ConversionException if the source cannot be converted
	 * @throws IllegalArgumentException if the target is not a wrapper class
	 */
	public static Object convertToWrapper (String source, Class target)
	    throws ConversionException
	{
		// Check arguments
		if (target==null)
		    throw new NullPointerException 
		    ("Null target class passed to convertToWrapper");
		
		if (source==null)
		    throw new NullPointerException
		    ("Null source string passed to convertToWrapper");
		
		if (!wrapperMap.containsKey (target))
		    throw new IllegalArgumentException
		    ("Non-wrapper target class passed to convertToWrapper");
		
		String str = ((String) source).trim();
		// First try the most obvious method
		try
		{
			if (target.equals (Boolean.class))
			     return convertToBoolean (str) ? 
			         Boolean.TRUE : Boolean.FALSE;
			else if (target.equals (Byte.class))
			     return Byte.decode (str);
			else if (target.equals (Short.class))
			     return Short.decode (str);
			else if (target.equals (Integer.class))
			     return Integer.decode (str);
			else if (target.equals (Long.class))
			     return Long.decode (str);
			else if (target.equals (Float.class))
			     return new Float (str);
			else if (target.equals (Double.class))
			     return new Double (str);
			else if (target.equals (Character.class))
				if (((String)source).length()==1)
				    return new Character (((String)source).charAt(0));
		}
		catch (NumberFormatException e)
		{
		}
		
		// Then try the NumberFormat...
		try
		{
			return convertToWrapper (NUMBER_FORMAT.parse (str), target, false);
		}
		catch (ParseException e)
		{
		    throw new ConversionException ("Unable to convert "+source+
		                                   " to "+target.getName());
		}
	}
	
	/**
	 * Converts a Number to an instance of a specified wrapper 
	 * class, if possible. If any loss of accuracy would be involved, 
	 * the conversion is rejected if allowLossOfAccuracy is false.
	 * 
	 * @param source String to convert
	 * @param target wrapper class to convert to
	 * @param allowLossOfAccuracy if true, "lossy" conversions are allowed;
	 * if false, "lossy" conversions will throw an IllegalArgumentException
	 * 
	 * @throws NullPointerException if either target or source are null
	 * @throws ConversionException if the source cannot be converted to the target
	 * @throws IllegalArgumentException if the target is not a wrapper class
	 */
	public static Object convertToWrapper (Number source, 
	                                       Class target, 
	                                       boolean allowLossOfAccuracy)
	    throws ConversionException
	{
		// Check arguments
		if (target==null)
		    throw new NullPointerException 
		    ("Null target class passed to convertToWrapper");
		
		if (source==null)
		    throw new NullPointerException
		    ("Null source string passed to convertToWrapper");
		
		if (!wrapperMap.containsKey (target))
		    throw new IllegalArgumentException
		    ("Non-wrapper target class passed to convertToWrapper");
		
		// Check for no-op case
		if (target.isInstance (source))
		    return source;
		
		// Check for character case (special as it's not a Number)
		if (target.equals (Character.class))
		{
			if (source.doubleValue() != source.longValue())
			    throw new ConversionException 
			    ("Unable to convert "+source+" to a character");
			
			long l = source.longValue();
			if ((l < Character.MIN_VALUE || l > Character.MAX_VALUE) && 
			    !allowLossOfAccuracy)
			    throw new ConversionException 
			    ("Unable to convert "+source+
			     " to a character without loss of accuracy");
			return new Character ((char) l);			
		}
		
		Number result=null;
		if (target.equals (Byte.class))
		    result = new Byte (source.byteValue());
		else if (target.equals (Short.class))
		    result = new Short (source.shortValue());
		else if (target.equals (Integer.class))
		    result = new Integer (source.intValue());
		else if (target.equals (Long.class))
		    result = new Long (source.longValue());
		else if (target.equals (Float.class))
		    result = new Float (source.floatValue());
		else if (target.equals (Double.class))
		    result = new Double (source.doubleValue());
		else
		    throw new IllegalArgumentException ("Unhandled wrapper class "+target.getName());
		    
		if ((result.doubleValue()!=source.doubleValue() ||
		    result.longValue()!=source.longValue()) &&
		    !allowLossOfAccuracy)
		    throw new ConversionException 
		        ("Conversion of "+source+" to "+target.getName()+
		        " would result in a loss of accuracy");
		    
		return result;
	}
	
	/**
	 * Attempts to create an instance of the given class from
	 * the object passed in, in a generous fashion. If the object 
	 * is already of the appropriate type, it is returned as-is. Otherwise, 
	 * the following steps are taken:
	 * <ol>
	 * <li>If the target is a "primitive class" (eg int.class) 
	 * it is converted into the appropriate wrapper class.
	 * <li>Wrapped classes are special-cased using the convertToWrapper
	 * methods - if the source is a Number, only lossless conversions
	 * are allowed.
	 * <li>An attempt is made to find a public constructor of the desired
	 * class which takes a single parameter of the type of object
	 * specified (or a supertype). For instance, passing in a String
	 * in order to create a java.text.MessageFormat will cause the
	 * method to call java.text.MessageFormat's constructor taking
	 * a String.
	 * <li>An attempt is made to find a public static method of the desired
	 * class which takes a single parameter of the type of object
	 * specified (or a supertype) and returns an instance of the 
	 * target class (or a subtype). For instance, passing in any object
	 * to be converted into a String (and assuming that there isn't an
	 * appropriate String constructor) will invoke String.valueOf(source).
	 * </ol>
	 * Note that if the source is an instance of a wrapper class,
	 * any attempts to find appropriate methods to call with the source
	 * as a parameter will also attempt to find a similar method to call
	 * with the primitive value of the wrapper (eg if an Integer is passed
	 * in, attempts to find methods with a single int parameter will also
	 * be made). Attempts will be made to find other compatible methods 
	 * with primitives (eg if an Integer is passed in, an attempt will 
	 * be made to find methods with a single long parameter.) In the case of
	 * long->double conversion, this may introduce a loss of accuracy. If a
	 * more specific method is present, however, it will always be called
	 * - for instance, if an Integer is passed in, a method taking an Integer
	 * parameter will be used in preference to one taking an int parameter,
	 * which will be used in preference to one taking a long parameter. The
	 * most specific possible method is also used in cases where there
	 * is more than one matching method and the source is a normal object.
	 * For instance, new Foo (String) is preferred to new Foo (Object)
	 * if the source is a String. However, if there is no most-specific
	 * method (if the source implements multiple interfaces, for instance)
	 * the order of preference between unrelated interfaces is unspecified.
	 * If multiple methods with different names are found in step 4, they 
	 * are sorted by specificity first, then alphabetically.
	 * <p>
	 * If any one attempt for conversion fails (eg an exception is thrown),
	 * further attempts will be made. This should be considered carefully
	 * for side-effects, as it means that many methods may be called while
	 * trying to find a valid conversion.
	 * 
	 * @param source object to be "converted" to the target class
	 * @param target class of requested object
	 * 
	 * @return an instance of the target class representing the
	 * source object (or an instance of the appropriate wrapper class
	 * if the target class is a primitive class). null may be returned
	 * if an appropriate-looking static method has been found but it returns
	 * null.
	 * 
	 * @throws NullPointerException if either target or source are null.
	 * @throws ConversionException if no way of converting the source to
	 * the target class can be found
	 */
	public static Object convertObject (Object source, Class target)
	    throws ConversionException
	{
		// Check arguments
		if (target==null)
		    throw new NullPointerException 
		    ("Null target class passed to convertObject");
		
		if (source==null)
		    throw new NullPointerException
		    ("Null source object passed to convertObject");

		// Convert primitive classes into wrappers
		if (target.isPrimitive())
		{
			Class realTarget = (Class) primitiveMap.get (target);
			if (realTarget==null)
			    throw new ConversionException 
			    ("Unhandled primitive class "+target.getName());
			target = realTarget;
		}
		
		// Check for no-op case
		if (target.isInstance (source))
		    return source;

		Class sourceClass = source.getClass();
		String sourceClassName = sourceClass.getName();
		
		// Special-case wrapper targets	
		if (wrapperMap.containsKey (target))
		{
			if (source instanceof String)
			    return convertToWrapper ((String) source, target);
			if (source instanceof Number)
			    return convertToWrapper ((Number) source, target, false);
			throw new ConversionException ("Unable to convert "+source+" to "+
			                               target.getName());
		}
		
		// Map of converted values from the source to each of the appropriate
		// wrapper classes for which the source is a valid type to
		// call a method on (eg with a source of Integer, there would be
		// entries in here from int.class to Integer, long.class to Long
		// etc).
		// For non-wrapper sources, this will be null.
		Map convertedPrimitives = createPrimitiveMap(source);
		
		// List of types of parameters we've found - used for
		// sorting.
		Set foundParameterTypes = new HashSet();
		
		// Get *all* the methods and constructors...
		Invokable[] allInvokables = Invokable.getInvokables (target);
		
		// Now filter them...
		List filteredInvokables = new ArrayList();
		for (int i=0; i < allInvokables.length; i++)
		{
			Invokable inv = allInvokables[i];
			
			// Filter out instance methods
			if (inv.isMethod() && !Modifier.isStatic (inv.getModifiers()))
			    continue;
			    
			// Now filter by parameter types:
			Class[] paramTypes = inv.getParameterTypes();
			
			// Must have exactly one parameter
			if (paramTypes.length != 1)
			    continue;
			    
			// Must be a compatible parameter type
			if (!(paramTypes[0].isInstance (source) ||
			    convertedPrimitives.containsKey (paramTypes[0])))
			    continue;
			    
			// Success - add it to the two lists. Note
			// that the order isn't important
			filteredInvokables.add (inv);
			foundParameterTypes.add (paramTypes[0]);
		}
		
		// Now sort the parameter types by specificity
		List sortedTypes = sortBySpecificity (foundParameterTypes);
		
		// Now (for speed of sorting the filtered invokables)
		// create a map from the parameter type to the index within
		// sortedTypes
		final Map sortIndexMap = new HashMap();
		for (int i=0; i < sortedTypes.size(); i++)
		{
			sortIndexMap.put (sortedTypes.get (i), new Integer (i));
		}
		
		// Sort the filtered invokables
		Collections.sort (filteredInvokables, new Comparator()
		    {
		    	public int compare (Object o1, Object o2)
		    	{
		    		Invokable inv1 = (Invokable) o1;
		    		Invokable inv2 = (Invokable) o2;
		    		
		    		// Constructors go first
		    		if (inv1.isConstructor() && inv2.isMethod())
		    		    return -1;
		    		if (inv2.isConstructor() && inv1.isMethod())
		    		    return 1;
		    		    
		    		// Both the same type, so check the parameters (there
		    		// will definitely be exactly one each due to previous
		    		// filtering)
		    		Class param1 = inv1.getParameterTypes()[0];
		    		Class param2 = inv2.getParameterTypes()[0];
		    		
		    		// If the parameter types are the same, compare 
		    		// the methods by name.
		    		if (param1.equals (param2))
		    		    return inv1.getName().compareTo (inv2.getName());
		    		
		    		// Different parameter types, so compare by index within
		    		// sortedTypes
		    	    int i1 = ((Integer)sortIndexMap.get(param1)).intValue();
		    	    int i2 = ((Integer)sortIndexMap.get(param2)).intValue();
		    	    
		    	    return i1-i2;
		    	}
		    });
		
		try
		{
			return invokeInOrder(filteredInvokables, null, new Object[]{source});
		}
		catch (NoSuchMethodException e)
		{
			throw new ConversionException ("Unable to convert "+source+
			                               " to "+target.getName());
		}
	}

	/**
	 * Creates a map from primitive classes to wrapper instances
	 * where the value of the instance is equivalent (as far as
	 * possible) to the source object. Only "upwardly compatible"
	 * classes are mapped, ie giving an Integer as a source maps
	 * int, long, float, double, whereas giving a Float as a source
	 * maps just float and double. The purpose of this method is to find
	 * possible values for method invocation - for instance, as an int
	 * variable can be passed as a parameter to a method requiring
	 * a double, there is a mapping created for the double primitive
	 * class. If there is no mapping for a primitive class, it means
	 * that the source object could not have been a wrapper for a
	 * primitive type that could legally be used to call a method
	 * with that value as an actual parameter and that primitive
	 * class as the parameter type.
	 * 
	 * @param source source object, which needn't be a wrapper instance
	 * but should be non-null
	 * 
	 * @throws NullPointerException if source is null
	 * 
	 * @return a Map containing all appropriate mappings from primitive
	 * classes to wrapper instances. If the source object isn't a wrapper
	 * instance, this map will be empty.
	 */
	public static Map createPrimitiveMap (Object source)
	{
		Map ret = new HashMap();
		
		// If the source is not a wrapper, return the empty map.
		if (!wrapperMap.containsKey (source.getClass()))
		    return ret;
		
		while (true)
		{
			Class[] chainLink = (Class[]) parameterMap.get (source.getClass());
			if (chainLink==null || chainLink.length !=2 || chainLink[0]==null)
			    throw new RuntimeException 
			        ("Error traversing parameter chain with source of type "+
			         source.getClass());
			
			ret.put (chainLink[0], source);
			
			if (chainLink[1]==null)
			    break;
			        
			// Character is a special-case. Convert it straight
			// to an Integer
			if (source.getClass().equals (Character.class))
			    source = new Integer (((Character)source).charValue());
			else
			{
				try
				{
			        source = convertToWrapper ((Number) source, chainLink[1], true);
				}
				catch (ConversionException e)
				{
					throw new RuntimeException 
					    ("ConversionException where it should be impossible: "+
					     e.getMessage());
				}
			}
		}
		return ret;		
	}

	/**
	 * Sorts the given set of Classes by specificity and returns it as a list; 
	 * more specific classes will come before less specific classes (eg FileInputStream
	 * comes before InputStream comes before Object). Normal classes come before
	 * interfaces, which come before primitive classes. The ordering within
	 * interfaces is partially undefined if two or more unrelated classes/interfaces
	 * are involved, but each subclass will come before its superclass, etc.
	 * The current implementation of this method is speed O(n^2) and space O(n) so 
	 * care should be taken not to call it with huge lists. Expected usage is for small
	 * lists (for instance parameters of possible methods) so this shouldn't be a problem.
	 * I may rewrite this to sort in-place and in a rather faster time at a later date.
	 * If you wish to do this yourself, I'd be grateful if you'd mail me the code at
	 * <a href="mailto:skeet@pobox.com">skeet@pobox.com</a> if you don't mind me including
	 * it in future releases (with appropriate credits, of course).
	 */
	public static List sortBySpecificity (Set classes)
	{
		List sorted = new ArrayList(classes.size());
		
		for (Iterator it = classes.iterator(); it.hasNext(); )
		{
			Class current = (Class) it.next();
			
			// We'll add the primitives right at the end, straight from the list
			if (current.isPrimitive())
			    continue;
			
			boolean ifaceCurrent = current.isInterface();
			
			ListIterator tests=null;
			for (tests = sorted.listIterator(); tests.hasNext(); )
			{
				Class test = (Class) tests.next();
				
				boolean ifaceTest = test.isInterface();
				
				if (ifaceCurrent && !ifaceTest)
				    continue;
				if (!ifaceCurrent && ifaceTest)
				{
					tests.previous();
					break;
				}
				
				if (test.isAssignableFrom (current))
				{
					tests.previous();
					break;
				}
			}
			tests.add (current);
		}
		
		for (int i=0; i < PRIMITIVES.length; i++)
		    if (classes.contains (PRIMITIVES[i]))
		        sorted.add (PRIMITIVES[i]);
		        
		return sorted;        
	}

	/**
	 * Invokes a list of Invokables in turn with the same
	 * parameters each time (or the appropriate wrappers for
	 * primitive classes) until one succeeds (ie doesn't throw
	 * an exception during invocation).
	 * 
	 * @param invokables list of Invokable objects
	 * @param obj object to invoke methods on
	 * @param params array of parameter objects. Each is converted to
	 * wrappers for primitive classes where needed.
	 * 
	 * @throws IllegalArgumentException if any of the Invokables for 
	 * which invocation is attempted takes the wrong number of parameters 
	 * or if any of the target parameters cannot be converted from the 
	 * parameters given. Note that this is only thrown directly within the
	 * method itself - any IllegalArgumentExceptions caused by the
	 * actual invocation are swallowed.
	 * 
	 * @throws NullPointerException if invokables or params is null,
	 * or if any element of invokables for which invocation is attempted
	 * is null
	 * 
	 * @throws ClassCastException if any element of invokables is not
	 * an Invokable
	 * 
	 * @throws NoSuchMethodException if none of the given Invokables
	 * succeeded
	 * 
	 * @return the result of the first successful invocation
	 */
	public static Object invokeInOrder (List invokables,
	                            Object obj,
	                            Object[] params)
	    throws NoSuchMethodException
	{
		if (invokables==null)
		    throw new NullPointerException 
		    ("Null invokables parameter passed to invokeInOrder");
		    
		if (params==null)
		    throw new NullPointerException
		    ("Null params parameter passed to invokeInOrder");
		
		// Create conversion maps
		Map[] conversionMaps = new Map [params.length];
		for (int i=0; i < params.length; i++)
		{
			if (params[i] != null)
			    conversionMaps[i]=createPrimitiveMap (params[i]);
			else
				conversionMaps[i]=new HashMap();
		}
		
		Object[] parameters = new Object [params.length];
		
		for (Iterator it = invokables.iterator(); it.hasNext() ;)
		{
			Invokable inv = (Invokable) it.next();
			if (inv==null)
			    throw new NullPointerException
			    ("Null element in invokables parameter passed to invokeInOrder");
			    
			Class[] paramClasses = inv.getParameterTypes();
			
			if (paramClasses.length != params.length)
			    throw new IllegalArgumentException
			    ("Invokable parameter takes "+paramClasses.length+
			    "parameters; "+params.length+" parameters given.");
			
			// Set up the parameters
			for (int i=0; i < params.length; i++)
			{
				if (primitiveMap.containsKey (paramClasses[i]))
				{
					parameters[i]=conversionMaps[i].get (paramClasses[i]);
					if (parameters[i]==null)
					    throw new IllegalArgumentException
					    ("Unable to convert parameter "+i+" of specified parameters to "+
					     paramClasses[i].getName());
				}
				else
				    parameters[i]=params[i];
			}
			
			try
			{
				return inv.invoke (obj, parameters);
			}
			catch (Throwable e)
			{
				// Keep going...
			}
		}
		throw new NoSuchMethodException ("Unable to invoke any listed method.");
	}
	
    /**
     * Attempts to convert a String parameter to a boolean
     * value. Valid parameters are "on", "true", "yes", "off",
     * "false" and "no", and the parameter is trimmed and 
     * turned into lower case before being converted. While this 
     * is not strictly a method involving reflection, it is in 
     * this class as it fits in well with the other methods.
	 *
     * @throws IllegalArgumentException if the value cannot be
     * converted.
     * 
     * @return the boolean value of the string
     */
    public static boolean convertToBoolean (String value)
        throws IllegalArgumentException
    {
        String val = value.trim().toLowerCase();
        if (val.equals ("true") || val.equals ("on") || val.equals ("yes"))
            return true;
        if (val.equals ("false") || val.equals ("off") || val.equals ("no"))
            return false;
        throw new IllegalArgumentException
            ("Invalid boolean val "+value);
    }

}

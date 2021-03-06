/*
 * Copyright (c) 2010-2011 Stripe (http://stripe.com)
 * Copyright (c) 2015 Base, Inc. (http://binc.jp/)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package jp.pay.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;

import jp.pay.model.Charge;
import jp.pay.net.APIResource;
import jp.pay.net.RequestOptions;
import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Simple test to make sure payjp-java provides consistent bindings.
 */
public class StandardizationTest {
	public Collection<Class> getAllModels() throws IOException {
		Class<Charge> chargeClass = Charge.class;
		ClassPath classPath = ClassPath.from(chargeClass.getClassLoader());
		ImmutableSet<ClassPath.ClassInfo> topLevelClasses = classPath.getTopLevelClasses(chargeClass.getPackage().getName());
		List<Class> classList = Lists.newArrayListWithExpectedSize(topLevelClasses.size());
		for (ClassPath.ClassInfo classInfo : topLevelClasses) {
			Class c = classInfo.load();
			// Skip things that aren't APIResources
			if (!APIResource.class.isAssignableFrom(c)) {
				continue;
			}
			// Skip the APIResource itself
			if (APIResource.class == c) {
				continue;
			}
			classList.add(classInfo.load());
		}
		return classList;
	}

	@Test
	public void allNonDeprecatedMethodsTakeOptions() throws IOException, NoSuchMethodException {
		for (Class aClass : getAllModels()) {
			HashSet<Class<?>> interfaces = new HashSet<Class<?>>(Arrays.<Class<?>>asList(aClass.getInterfaces()));
			for (Method method : aClass.getMethods()) {
				// Skip methods not declared on the base class.
				if (method.getDeclaringClass() != aClass) {
					continue;
				}
				// Skip equals
				if (method.getName().equals("equals")) {
					continue;
				}
				// Skip setters
				if (method.getName().startsWith("set")) {
					continue;
				}
				// Skip getters
				if (method.getName().startsWith("get")) {
					continue;
				}

				// If more than one method with the same parameter types is declared in a class, and one of these
				// methods has a return type that is more specific than any of the others, that method is returned;
				// otherwise one of the methods is chosen arbitrarily.
				Method mostSpecificMethod = aClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
				if (!method.equals(mostSpecificMethod)) {
					continue;
				}

				Invokable<?, Object> invokable = Invokable.from(method);
				// Skip private methods.
				if (invokable.isPrivate()) {
					continue;
				}
				// Skip deprecated methods - we need to keep them around, but aren't asserting their type.
				if (invokable.isAnnotationPresent(Deprecated.class)) {
					continue;
				}
				ImmutableList<Parameter> parameters = invokable.getParameters();
				// Skip empty parameter lists - assume the author is using default values for the RequestOptions
				if (parameters.isEmpty()) {
					continue;
				}
				Parameter lastParam = parameters.get(parameters.size() - 1);
				Class<?> finalParamType = lastParam.getType().getRawType();

				// Skip methods that have exactly one param which is a map.
				if (Map.class.equals(finalParamType) && parameters.size() == 1) {
					continue;
				}

				// Skip `public static Foo retrieve(String id) {...` helper methods
				if (String.class.equals(finalParamType) && parameters.size() == 1 && "retrieve".equals(method.getName())) {
					continue;
				}

				// Skip the `public static Card createCard(String id) {...` helper method on Customer.
				if (String.class.equals(finalParamType) && parameters.size() == 1 && "createCard".equals(method.getName())) {
					continue;
				}

				if (RequestOptions.class.isAssignableFrom(finalParamType)) {
					continue;
				}
				Assert.assertTrue(
						String.format("Methods on %ss like %s.%s should take a final parameter as a %s parameter.%n", APIResource.class.getSimpleName(), aClass.getSimpleName(), method.getName(), RequestOptions.class.getSimpleName()),
						RequestOptions.class.isAssignableFrom(finalParamType));
			}
		}
	}
}

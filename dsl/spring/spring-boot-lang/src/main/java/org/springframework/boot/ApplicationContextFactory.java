/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot;

import java.util.function.Supplier;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Strategy interface for creating the {@link ConfigurableApplicationContext} used by a
 * {@link SpringApplication}. Created contexts should be returned in their default form,
 * with the {@code SpringApplication} responsible for configuring and refreshing the
 * context.
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @since 2.4.0
 */
@FunctionalInterface
public interface ApplicationContextFactory {

	/**
	 * A default {@link ApplicationContextFactory} implementation that will create an
	 * appropriate context for the {@link WebApplicationType}.
	 */
	//ApplicationContextFactory DEFAULT = new DefaultApplicationContextFactory();
	ApplicationContextFactory DEFAULT = (webApplicationType) -> {
		try {
			switch (webApplicationType) {
				case SERVLET:
					return new AnnotationConfigServletWebServerApplicationContext();
				case REACTIVE:
					return new AnnotationConfigReactiveWebServerApplicationContext();
				default:
					return new AnnotationConfigApplicationContext();
			}
		}
		catch (Exception ex) {
			throw new IllegalStateException("Unable create a default ApplicationContext instance, "
					+ "you may need a custom ApplicationContextFactory", ex);
		}
	};

	/**
	 * Return the {@link Environment} type expected to be set on the
	 * {@link #create(WebApplicationType) created} application context. The result of this
	 * method can be used to convert an existing environment instance to the correct type.
	 * @param webApplicationType the web application type
	 * @return the expected application context type or {@code null} to use the default
	 * @since 2.6.14
	 */
	default Class<? extends ConfigurableEnvironment> getEnvironmentType(WebApplicationType webApplicationType) {
		return null;
	}

	/**
	 * Create a new {@link Environment} to be set on the
	 * {@link #create(WebApplicationType) created} application context. The result of this
	 * method must match the type returned by
	 * {@link #getEnvironmentType(WebApplicationType)}.
	 * @param webApplicationType the web application type
	 * @return an environment instance or {@code null} to use the default
	 * @since 2.6.14
	 */
	default ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
		return null;
	}

	/**
	 * Creates the {@link ConfigurableApplicationContext application context} for a
	 * {@link SpringApplication}, respecting the given {@code webApplicationType}.
	 * @param webApplicationType the web application type
	 * @return the newly created application context
	 */
	ConfigurableApplicationContext create(WebApplicationType webApplicationType);

	/**
	 * Creates an {@code ApplicationContextFactory} that will create contexts by
	 * instantiating the given {@code contextClass} through its primary constructor.
	 * @param contextClass the context class
	 * @return the factory that will instantiate the context class
	 * @see BeanUtils#instantiateClass(Class)
	 */
	static ApplicationContextFactory ofContextClass(Class<? extends ConfigurableApplicationContext> contextClass) {
		return of(() -> BeanUtils.instantiateClass(contextClass));
	}

	/**
	 * Creates an {@code ApplicationContextFactory} that will create contexts by calling
	 * the given {@link Supplier}.
	 * @param supplier the context supplier, for example
	 * {@code AnnotationConfigApplicationContext::new}
	 * @return the factory that will instantiate the context class
	 */
	static ApplicationContextFactory of(Supplier<ConfigurableApplicationContext> supplier) {
		return (webApplicationType) -> supplier.get();
	}

}
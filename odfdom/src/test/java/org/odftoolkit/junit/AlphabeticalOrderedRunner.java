/*
 * Copyright 2012 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * Guarantees that the test methods of a test class are being executed in alphabetical order.
 * Activated by annotating the test class using <code>@RunWith(AlphabeticalOrderedRunner.class)</code>.
 *
 * @author svanteschubert
 */
public class AlphabeticalOrderedRunner extends BlockJUnit4ClassRunner {

	//private static final Logger LOG = Logger.getLogger(OrderedRunner.class.getName());

	/*
	 * default initializer
	 */
	public AlphabeticalOrderedRunner(Class _class) throws InitializationError {
		super(_class);
	}

	/**
	 * The initializer just pipes through to the superclass. Pretty standard
	 * stuff. The interesting part is in overriding the computeTestMethods method.
	 */
	@Override
	protected List computeTestMethods() {
		List lst = super.computeTestMethods();
		List methodList = new ArrayList(lst);

		Collections.sort(methodList, new AlphabeticalOrder());

		return methodList;
	}

	/*
	 * Class for alphabetical ordering of a list
	 */
	public class AlphabeticalOrder implements Comparator {

		public int compare(Object o1, Object o2) {
			FrameworkMethod f1 = (FrameworkMethod) o1;
			FrameworkMethod f2 = (FrameworkMethod) o2;

			return f1.getName().compareTo(f2.getName());
		}
	}
}

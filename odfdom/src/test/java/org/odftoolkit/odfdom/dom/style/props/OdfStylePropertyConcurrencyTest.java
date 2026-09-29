/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2026 The Document Foundation.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.dom.style.props;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.pkg.OdfName;

/**
 * Regression tests for concurrent registration of style properties, see
 * https://github.com/tdf/odftoolkit/issues/442
 */
public class OdfStylePropertyConcurrencyTest {

  private static final int THREADS = 32;
  private static final int OPERATIONS_PER_THREAD = 5_000;
  private static final long TIMEOUT_SECONDS = 60;

  private static final String[] STYLE_PROPERTIES_ELEMENTS = {
    "StyleChartPropertiesElement",
    "StyleDrawingPagePropertiesElement",
    "StyleGraphicPropertiesElement",
    "StyleHeaderFooterPropertiesElement",
    "StyleListLevelPropertiesElement",
    "StylePageLayoutPropertiesElement",
    "StyleParagraphPropertiesElement",
    "StyleRubyPropertiesElement",
    "StyleSectionPropertiesElement",
    "StyleTableCellPropertiesElement",
    "StyleTableColumnPropertiesElement",
    "StyleTablePropertiesElement",
    "StyleTableRowPropertiesElement",
    "StyleTextPropertiesElement"
  };

  /** Many threads register different new properties at the same time. */
  @Test
  public void testConcurrentRegistration() throws Exception {
    OdfStylePropertiesSet[] propSets = OdfStylePropertiesSet.values();
    List<List<OdfStyleProperty>> results =
        runConcurrently(
            worker -> {
              List<OdfStyleProperty> props = new ArrayList<>(OPERATIONS_PER_THREAD);
              for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                OdfStylePropertiesSet propSet = propSets[(worker + i) % propSets.length];
                OdfName name =
                    OdfName.newName(
                        OdfDocumentNamespace.TEXT, "race-unique-property-" + worker + "-" + i);
                OdfStyleProperty prop = OdfStyleProperty.get(propSet, name);
                assertSame(propSet, prop.getPropertySet());
                assertSame(name, prop.getName());
                props.add(prop);
              }
              return props;
            });
    // all properties have been registered and are found again
    for (List<OdfStyleProperty> props : results) {
      for (OdfStyleProperty prop : props) {
        assertSame(prop, OdfStyleProperty.get(prop.getPropertySet(), prop.getName()));
      }
    }
  }

  /** Many threads register the same new properties at the same time and share one instance. */
  @Test
  public void testConcurrentCanonicalization() throws Exception {
    List<List<OdfStyleProperty>> results =
        runConcurrently(
            worker -> {
              List<OdfStyleProperty> props = new ArrayList<>(OPERATIONS_PER_THREAD);
              for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                props.add(
                    OdfStyleProperty.get(
                        OdfStylePropertiesSet.TextProperties,
                        OdfName.newName(OdfDocumentNamespace.TEXT, "race-shared-property-" + i)));
              }
              return props;
            });
    List<OdfStyleProperty> expected = results.get(0);
    for (List<OdfStyleProperty> props : results) {
      assertEquals(OPERATIONS_PER_THREAD, props.size());
      for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
        assertSame(expected.get(i), props.get(i));
      }
    }
  }

  /**
   * Initializes the generated Style*PropertiesElement classes concurrently, as their static
   * initializers register their properties. This is only a real cold start, if no earlier test of
   * the same JVM has already loaded these classes.
   */
  @Test
  public void testConcurrentStylePropertiesElementInitialization() throws Exception {
    ClassLoader loader = getClass().getClassLoader();
    List<List<Class<?>>> results =
        runConcurrently(
            worker -> {
              List<Class<?>> classes = new ArrayList<>(STYLE_PROPERTIES_ELEMENTS.length);
              for (int i = 0; i < STYLE_PROPERTIES_ELEMENTS.length; i++) {
                String className =
                    "org.odftoolkit.odfdom.dom.element.style."
                        + STYLE_PROPERTIES_ELEMENTS[(worker + i) % STYLE_PROPERTIES_ELEMENTS.length];
                classes.add(Class.forName(className, true, loader));
              }
              return classes;
            });
    for (List<Class<?>> classes : results) {
      assertEquals(STYLE_PROPERTIES_ELEMENTS.length, classes.size());
    }
  }

  private interface Worker<T> {
    T run(int worker) throws Exception;
  }

  /** Runs the given work in THREADS threads, all started at the same moment. */
  private static <T> List<T> runConcurrently(Worker<T> work) throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(THREADS);
    try {
      CountDownLatch ready = new CountDownLatch(THREADS);
      CountDownLatch start = new CountDownLatch(1);
      List<Future<T>> futures = new ArrayList<>(THREADS);
      for (int thread = 0; thread < THREADS; thread++) {
        final int worker = thread;
        Callable<T> task =
            () -> {
              ready.countDown();
              start.await();
              return work.run(worker);
            };
        futures.add(pool.submit(task));
      }
      assertTrue(
          "Worker threads did not start in time", ready.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
      start.countDown();

      List<T> results = new ArrayList<>(THREADS);
      for (Future<T> future : futures) {
        // rethrows any failure of a worker thread as ExecutionException
        results.add(future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
      }
      return results;
    } finally {
      pool.shutdownNow();
    }
  }
}

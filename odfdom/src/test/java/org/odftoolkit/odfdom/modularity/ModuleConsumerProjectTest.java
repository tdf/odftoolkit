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
package org.odftoolkit.odfdom.modularity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

public class ModuleConsumerProjectTest {

  private static final String CONSUMER_MODULE = "org.odftoolkit.odfdom.consumer";
  private static final String CONSUMER_MAIN = CONSUMER_MODULE + "/org.odftoolkit.odfdom.consumer.Smoke";

  @Test
  public void testMinimalModularConsumerCompilesAndRuns() throws Exception {
    Path consumerSourceRoot =
        Paths.get("src", "test", "resources", "module-consumer", "src").toAbsolutePath();
    Assert.assertTrue(
        "Missing consumer sources in " + consumerSourceRoot, Files.isDirectory(consumerSourceRoot));

    Path compiledModules = Files.createTempDirectory("odfdom-module-consumer-classes");
    String modulePath = buildModulePath();
    Assert.assertFalse("Module path must not be empty", modulePath.isEmpty());

    List<String> javacCommand = new ArrayList<>();
    javacCommand.add(getJavaTool("javac"));
    javacCommand.add("--module-path");
    javacCommand.add(modulePath);
    javacCommand.add("--module-source-path");
    javacCommand.add(consumerSourceRoot.toString());
    javacCommand.add("-d");
    javacCommand.add(compiledModules.toString());
    javacCommand.add("-m");
    javacCommand.add(CONSUMER_MODULE);
    CommandResult javacResult = run(javacCommand);
    Assert.assertEquals("javac failed:\n" + javacResult.output, 0, javacResult.exitCode);

    List<String> javaCommand = new ArrayList<>();
    javaCommand.add(getJavaTool("java"));
    javaCommand.add("--module-path");
    javaCommand.add(modulePath + File.pathSeparator + compiledModules);
    javaCommand.add("--module");
    javaCommand.add(CONSUMER_MAIN);
    CommandResult javaResult = run(javaCommand);
    Assert.assertEquals("java failed:\n" + javaResult.output, 0, javaResult.exitCode);
    Assert.assertTrue(
        "Expected modular consumer output, got:\n" + javaResult.output,
        javaResult.output.contains("JPMS_CONSUMER_OK"));
  }

  private static String buildModulePath() {
    Set<String> entries = new LinkedHashSet<>();
    addIfPresent(entries, Paths.get("target", "classes").toAbsolutePath());
    String classPath = System.getProperty("java.class.path", "");
    for (String classPathEntry : classPath.split(Pattern.quote(File.pathSeparator))) {
      if (classPathEntry.endsWith(".jar")) {
        addIfPresent(entries, Paths.get(classPathEntry));
      }
    }
    return String.join(File.pathSeparator, entries);
  }

  private static void addIfPresent(Set<String> entries, Path entry) {
    if (Files.exists(entry)) {
      entries.add(entry.toAbsolutePath().toString());
    }
  }

  private static String getJavaTool(String tool) {
    Path javaHome = Paths.get(System.getProperty("java.home"));
    Path unixToolPath = javaHome.resolve("bin").resolve(tool);
    if (Files.isExecutable(unixToolPath)) {
      return unixToolPath.toString();
    }
    Path windowsToolPath = javaHome.resolve("bin").resolve(tool + ".exe");
    if (Files.isExecutable(windowsToolPath)) {
      return windowsToolPath.toString();
    }
    return tool;
  }

  private static CommandResult run(List<String> command) throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);
    Process process = builder.start();
    String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    if (!process.waitFor(120, TimeUnit.SECONDS)) {
      process.destroyForcibly();
      return new CommandResult(-1, output + "\nProcess timed out");
    }
    return new CommandResult(process.exitValue(), output);
  }

  private static class CommandResult {
    private final int exitCode;
    private final String output;

    private CommandResult(int exitCode, String output) {
      this.exitCode = exitCode;
      this.output = output;
    }
  }
}

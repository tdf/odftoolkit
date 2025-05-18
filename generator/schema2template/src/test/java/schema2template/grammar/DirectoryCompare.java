/*
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the
 * License.
 *
 */
package schema2template.grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;

/**
 * Compares a directory with all its subdirectories if its text files are equal by line. Comparing
 * our test results with our references, which were once test files of running tests!
 */
final class DirectoryCompare {

  private static final Logger LOG = Logger.getLogger(DirectoryCompare.class.getName());

  private DirectoryCompare() {
    // utility class
  }

  public static void assertDirectoriesEqual(String RefFileDir, String newFileDir) throws Exception {
    // ******** Reference Test *************
    LOG.log(Level.INFO, "Comparing generated Files:\n\t{0}", RefFileDir);
    assertDirectoriesEqual(Paths.get(RefFileDir).toAbsolutePath(), Paths.get(newFileDir).toAbsolutePath());
  }

  /**
   * Compare the contents of two directories to determine if they are equal or not. If one of the
   * paths don't exist, the contents aren't equal and this method returns false.
   */
  private static void assertDirectoriesEqual(Path dir1, Path dir2) throws IOException {
    boolean dir1Exists = Files.exists(dir1) && Files.isDirectory(dir1);
    boolean dir2Exists = Files.exists(dir2) && Files.isDirectory(dir2);

    Assert.assertTrue(String.format(
      "The directory %s does not exist or is not a directory.", dir1),
      dir1Exists
    );
    Assert.assertTrue(String.format(
      "The directory %s does not exist or is not a directory.", dir2),
      dir2Exists
    );

    HashMap<Path, Path> dir1Paths = new HashMap<>();
    HashMap<Path, Path> dir2Paths = new HashMap<>();

    // Map the path relative to the base directory to the complete path.
    for (Path p : listPaths(dir1)) {
      dir1Paths.put(dir1.relativize(p), p);
    }

    for (Path p : listPaths(dir2)) {
      dir2Paths.put(dir2.relativize(p), p);
    }

    // The directories cannot be equal if the numbers of files aren't equal.
    if (dir1Paths.size() != dir2Paths.size()) {
      LOG.log(
          Level.SEVERE,
          "\nThe file size differ:\n{0} files exist in \n{1}\n{2} files exist in\n{3}\n\n",
          new Object[] {dir1Paths.size(), dir1, dir2Paths.size(), dir2});
    }

    // For each file in dir1, check if also dir2 contains this file and if
    // their contents are equal.
    for (Entry<Path, Path> pathEntry : dir1Paths.entrySet()) {
      Path relativePath = pathEntry.getKey();
      Path absolutePath = pathEntry.getValue();

      Assert.assertTrue(
        String.format("The file%n%s%ndoes not exist in%n%s", relativePath, dir2),
        dir2Paths.containsKey(relativePath)
      );

      assertFileContentsEqual(absolutePath, dir2Paths.get(relativePath));

      // remove it to be able to show the superset of dir2Paths in the end
      dir2Paths.remove(relativePath);
    }

    // if there is a superset of dir2Paths
    if (!dir2Paths.isEmpty()) {
      Assert.fail(
        String.format("The following files exist in%n%s%n%nbut not in%n%s:%n%n%s", dir2, dir1,
          dir2Paths.keySet().stream().map(Path::toString).collect(Collectors.joining("\n"))
        )
      );
    }
  }

  /**
   * Recursively finds all paths with given extensions in the given directory and all of its
   * sub-directories.
   */
  private static List<Path> listPaths(Path path, String... extensions) throws IOException {
    if (path == null) {
      return new ArrayList<>();
    }

    if (extensions.length == 0) {
      try (Stream<Path> stream = Files.walk(path)) {
        return stream
          .filter(p -> !p.getFileName().toString().equals(".DS_Store")) // macOS system file
          .collect(Collectors.toList());
      }
    }

    try (Stream<Path> stream = Files.walk(path)) {
      return stream
        .filter(p -> !Files.isDirectory(p))
        .filter(p -> !p.getFileName().toString().equals(".DS_Store")) // macOS system file
        .filter(p -> {
          String filename = p.getFileName().toString().toLowerCase(Locale.ROOT);
          return Stream.of(extensions)
            .anyMatch(filename::endsWith);
        })
        .collect(Collectors.toList());
      }
  }

  private static void assertFileContentsEqual(Path p1, Path p2) throws IOException {
    boolean isDirectory = Files.isDirectory(p1);
    Assert.assertEquals(
      String.format("Should be a %s: %s", isDirectory ? "directory" : "regular file", p2),
      isDirectory,
      Files.isDirectory(p2)
    );

    if (!isDirectory) {
      try (Stream<String> stream1 = Files.lines(p1); Stream<String> stream2 = Files.lines(p2)) {
        List<String> lines1 = stream1.collect(Collectors.toList());
        List<String> lines2 = stream2.collect(Collectors.toList());
        int n = Math.min(lines1.size(), lines2.size());
        for (int i = 0; i < n; i++) {
          Assert.assertEquals(
            String.format("Files %s and %s differ in content at line %d", p1, p2, i + 1),
            lines1.get(i).stripTrailing(),
            lines2.get(i).stripTrailing()
          );
        }
        Assert.assertEquals(
          String.format("The number of lines in the files %s and %s differ", p1, p2),
          lines1.size(), lines2.size()
        );
      }
    }
  }
}

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
package schema2template.example.odf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

class DirectoryCompare {

  private static final Logger LOG = Logger.getLogger(DirectoryCompare.class.getName());
  /**
   * Compare the contents of two directories to determine if they are equal or not. If both paths
   * don't exist, the contents aren't equal and this method returns false.
   */
  static boolean directoryContentEquals(Path dir1, Path dir2) throws IOException {
    boolean dir1Exists = Files.exists(dir1) && Files.isDirectory(dir1);
    boolean dir2Exists = Files.exists(dir2) && Files.isDirectory(dir2);

    if (dir1Exists && dir2Exists) {
      HashMap<Path, Path> dir1Paths = new HashMap<>();
      HashMap<Path, Path> dir2Paths = new HashMap<>();

      // Map the path relative to the base directory to the complete path.
      for (Path p : listPaths(dir1)) {
        dir1Paths.put(dir1.relativize(p), p);
      }

      for (Path p : listPaths(dir2)) {
        dir2Paths.put(dir2.relativize(p), p);
      }

      // The directories cannot be equal if the number of files aren't equal.
      if (dir1Paths.size() != dir2Paths.size()) {
        return false;
      }

      // For each file in dir1, check if also dir2 contains this file and if
      // their contents are equal.
      for (Entry<Path, Path> pathEntry : dir1Paths.entrySet()) {
        Path relativePath = pathEntry.getKey();
        Path absolutePath = pathEntry.getValue();
        if (!dir2Paths.containsKey(relativePath)) {
          return false;
        } else {
          if (!contentEquals(absolutePath, dir2Paths.get(relativePath))) {
            LOG.severe(
                "There is a difference between:\n\t"
                    + absolutePath.toString()
                    + "\n and \n\t"
                    + relativePath.toAbsolutePath().toString());
            return false;
          }
        }
      }
      return true;
    }

    return false;
  }

  /**
   * Recursively finds all files with given extensions in the given directory and all of its
   * sub-directories.
   */
  private static List<Path> listPaths(Path file, String... extensions) throws IOException {
    if (file == null) {
      return null;
    }

    List<Path> paths = new ArrayList<>();
    listPaths(file, paths, extensions);

    return paths;
  }

  /**
   * Recursively finds all paths with given extensions in the given directory and all of its
   * sub-directories.
   */
  private static void listPaths(Path path, List<Path> result, String... extensions)
      throws IOException {
    if (path == null) {
      return;
    }

    if (Files.isReadable(path)) {
      // If the path is a directory try to read it.
      if (Files.isDirectory(path)) {
        // The input is a directory. Read its files.
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
        for (Path p : directoryStream) {
          listPaths(p, result, extensions);
        }
        directoryStream.close();
      } else {
        String filename = path.getFileName().toString();
        if (extensions.length == 0) {
          result.add(path);
        } else {
          for (String extension : extensions) {
            if (filename.toLowerCase().endsWith(extension)) {
              result.add(path);
              break;
            }
          }
        }
      }
    }
  }

  /**
   * Compares the contents of the two given paths. If both paths don't exist, the contents aren't
   * equal and this method returns false.
   */
  private static boolean contentEquals(Path p1, Path p2) throws IOException {
    if (!Files.exists(p1) || !Files.exists(p2)) {
      return false;
    }

    if (Files.isDirectory(p1) && Files.isDirectory(p2)) {
      return directoryContentEquals(p1, p2);
    }

    if (p1.equals(p2)) {
      // same filename => true
      return true;
    }

    if (Files.size(p1) != Files.size(p2)) {
      // different size =>false
      return false;
    }

    InputStream in1 = null;
    InputStream in2 = null;
    try {
      in1 = Files.newInputStream(p1);
      in2 = Files.newInputStream(p2);

      int expectedByte = in1.read();
      while (expectedByte != -1) {
        if (expectedByte != in2.read()) {
          return false;
        }
        expectedByte = in1.read();
      }
      if (in2.read() != -1) {
        return false;
      }
      return true;
    } finally {
      if (in1 != null) {
        try {
          in1.close();
        } catch (IOException e) {
          return false;
        }
      }
      if (in2 != null) {
        try {
          in2.close();
        } catch (IOException e) {
          return false;
        }
      }
    }
  }
}

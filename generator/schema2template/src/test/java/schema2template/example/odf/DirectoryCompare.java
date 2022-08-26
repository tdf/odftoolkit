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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;

/**
 * Compares a directory with all its subdirectories if its text files are equal by line. Comparing
 * our test results with our references, which were once test files of running tests!
 */
class DirectoryCompare {

  private static final Logger LOG = Logger.getLogger(DirectoryCompare.class.getName());

  public static boolean compareDirectories(String newFileDir, String RefFileDir) {
    boolean directoriesEqual = true;
    try {
      // ******** Reference Test *************
      // generated sources must be equal to the previously generated reference sources
      String targetPath = Paths.get(newFileDir).toAbsolutePath().toString();
      String referencePath = Paths.get(RefFileDir).toAbsolutePath().toString();

      LOG.log(
          Level.INFO,
          "\n\nComparing new generated Files:\n\t{0}\nwith their reference:\n\t{1}\n",
          new Object[] {targetPath, referencePath});
      directoriesEqual =
          DirectoryCompare.compareDirectories(Paths.get(newFileDir), Paths.get(RefFileDir));
      Assert.assertTrue(
          "The new generated sources\n\t"
              + targetPath
              + "\ndiffer from their reference:\n\t"
              + referencePath,
          directoriesEqual);
    } catch (IOException ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
    return directoriesEqual;
  }

  /**
   * Compare the contents of two directories to determine if they are equal or not. If one of the
   * paths don't exist, the contents aren't equal and this method returns false.
   */
  private static boolean compareDirectories(Path dir1, Path dir2) throws IOException {
    boolean dir1Exists = Files.exists(dir1) && Files.isDirectory(dir1);
    boolean dir2Exists = Files.exists(dir2) && Files.isDirectory(dir2);
    Boolean areEqual = Boolean.TRUE;

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
        LOG.log(
            Level.SEVERE,
            "\nThe file size differ:\n{0} files exist in \n{1}\n{2} files exist in\n{3}\n\n",
            new Object[] {dir1Paths.size(), dir1, dir2Paths.size(), dir2});
        areEqual = Boolean.FALSE;
      }

      // For each file in dir1, check if also dir2 contains this file and if
      // their contents are equal.
      for (Entry<Path, Path> pathEntry : dir1Paths.entrySet()) {
        Path relativePath = pathEntry.getKey();
        Path absolutePath = pathEntry.getValue();
        if (!dir2Paths.containsKey(relativePath)) {
          areEqual = Boolean.FALSE;
          LOG.log(
              Level.SEVERE,
              "\nThe file\n{0}\ndoes not exist in\n{1}\n\n",
              new Object[] {relativePath, dir2});
        } else {
          if (!textFilesEquals(absolutePath, dir2Paths.get(relativePath))) {
            // error msg within textFilesEquals with line difference
            // LOG.log(Level.SEVERE, "There is a difference between:\n{0}\n and \n{1}\n\n", new
            // Object[]{absolutePath.toString(), relativePath.toAbsolutePath().toString()});
            areEqual = Boolean.FALSE;
          }
          // remove it to be able to show the superset of dir2Paths in the end
          dir2Paths.remove(relativePath);
        }
      }
      // if there is a superset of dir2Paths
      if (!dir2Paths.isEmpty()) {
        areEqual = Boolean.FALSE;
        for (Entry<Path, Path> pathEntry2 : dir2Paths.entrySet()) {
          Path relativePath2 = pathEntry2.getKey();
          LOG.log(
              Level.SEVERE,
              "\nThe file\n{0}\ndoes not exist in\n{1}\n\n",
              new Object[] {relativePath2, dir1});
        }
      }
      return areEqual;
    } else {
      areEqual = Boolean.FALSE;
      if (!dir1Exists) {
        LOG.log(
            Level.SEVERE,
            "\nThe following input directory does not exist:\n{0}\n\n",
            new Object[] {dir1});
      }
      if (!dir2Exists) {
        LOG.log(
            Level.SEVERE,
            "\nThe following input directory does not exist:\n{0}",
            new Object[] {dir2});
      }
    }

    return areEqual;
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
        if (extensions.length == 0) {
          result.add(path);
        }
        try ( // The input is a directory. Read its files.
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
          for (Path p : directoryStream) {
            listPaths(p, result, extensions);
          }
        }
      } else {
        if (extensions.length == 0) {
          result.add(path);
        } else {
          String filename = path.getFileName().toString();
          for (String extension : extensions) {
            if (filename.toLowerCase().endsWith(extension)) {
              result.add(path);
              break;
            }
          }
        }
      }
    } else {
      System.err.println("Not readable:" + path);
    }
  }

  private static boolean textFilesEquals(Path p1, Path p2) throws IOException {
    boolean areEqual = Boolean.TRUE;
    if (Files.isDirectory(p1) || Files.isDirectory(p2)) {
      if (!Files.isDirectory(p1)) {
        LOG.log(
            Level.SEVERE,
            "\nOne file is a directory:\n{0}\nwhile the other is a file:\n{1}\n\n",
            new Object[] {p2.toString(), p1.toString()});
        areEqual = Boolean.FALSE;
      }
      if (!Files.isDirectory(p2)) {
        LOG.log(
            Level.SEVERE,
            "\nOne file is a directory:\n{0}\nwhile the other is a file:\n{1}\n\n",
            new Object[] {p1.toString(), p2.toString()});
        areEqual = Boolean.FALSE;
      }
    } else {
      try (BufferedReader reader1 = Files.newBufferedReader(p1)) {
        try (BufferedReader reader2 = Files.newBufferedReader(p2)) {
          String line1 = reader1.readLine();
          String line2 = reader2.readLine();
          int lineNum = 1;
          while (line1 != null || line2 != null) {
            if (line1 == null || line2 == null) {
              areEqual = Boolean.FALSE;
              break;
            } else if (!line1.equals(line2)) {
              areEqual = Boolean.FALSE;
              break;
            }
            line1 = reader1.readLine();
            line2 = reader2.readLine();
            lineNum++;
          }
          if (!areEqual) {
            LOG.log(
                Level.SEVERE,
                "\nTwo files have different content:\n{0}\nhas at line {1}:\n{2}\nand\n{3}\nhas at line {4}:\n{5}\n\n",
                new Object[] {p1.toString(), lineNum, line1, p2.toString(), lineNum, line2});
          }
        }
      }
    }
    return areEqual;
  }
}

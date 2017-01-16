import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FolderContents {

    static class DirectoryNotFoundException extends RuntimeException {
        void printWarning() {
            System.out.println("Directory does not exist.");
        }
    }

    static String readFolderPath() throws DirectoryNotFoundException {
        System.out.println("This program lists all the files contained in a given folder. \n" +
                "Please enter directory path to be looked through.");
        Scanner thisScanner = new Scanner(System.in);
        String theFolderPath = thisScanner.nextLine();
        thisScanner.close();
        if ( ! new File(theFolderPath).isDirectory()) {
            throw new DirectoryNotFoundException();
        }
        System.out.println("\n");
        return theFolderPath;
    }

    private static void makeBuildList(String theFolderPath) throws IOException {
        try{
            final File theFolder = new File(theFolderPath);
            FileWriter out = new FileWriter(theFolder.toString() + "\\build_list.txt");
            //FileWriter out = new FileWriter("build_list.txt");

            Tier total = new Tier();
            Tier current = new Tier();

            Directory dir = new Directory(theFolder, current);
            total.add(dir.numberOfFiles() - 1);
            current.add(-1);

            int i = 0;
            int k = 0;


            outerLoop:
            while (current.last() < total.last() ) {
                current.incrementLast();

                if (dir.currentFile().isFile()) {
                    System.out.println(format(dir.currentFile(), theFolderPath));
                    out.write(format(dir.currentFile(), theFolderPath) + "\r\n");
                    i++;
                } else if (dir.currentFile().isDirectory() &&
                        dir.numberOfFilesInCurrent() != 0) {
                    dir = dir.levelDown();
                    total.add(dir.numberOfFiles() - 1);
                    k++;
                } else if (dir.currentFile().isDirectory() &&
                        dir.numberOfFilesInCurrent() == 0) {
                    System.out.println(format(dir.currentFile(), theFolderPath)
                            + " (Empty folder)");
                    /*out.write(format(dir.currentFile(), theFolderPath)
                            + " (Empty folder) \r\n"); */
                    k++;
                }

                //System.out.println(Arrays.toString(total.toArray()));
                //System.out.println(Arrays.toString(current.toArray()));

                while (current.last() == total.last()) {
                    if (total.size() != 1) {
                        dir = dir.levelUp();
                        total.cutLast();
                    } else {
                        break outerLoop;
                    }
                }
            }

            out.close();
            System.out.println("Number of files: " + i + ", number of folders: " + k);

        } catch (DirectoryNotFoundException e1) {
            e1.printWarning();
        } catch (NullPointerException e2) {
            System.out.println("Directory is empty.");
        }
    }

    static String format(File file, String root) {
        return '.' + file.toString().substring(root.length()).replace('\\', '/');
    }

    public static void main(String[] args) throws IOException {
        String theFolderPath = readFolderPath();
        makeBuildList(theFolderPath);
    }

    static class Directory {

        File file;
        Tier current;

        Directory(File file, Tier current) {
            this.file = file;
            this.current = current;
        }

        File[] listFiles() {return this.file.listFiles();}

        int numberOfFiles() {return this.listFiles().length;}

        File currentFile() {return this.listFiles()[current.last()];}

        int numberOfFilesInCurrent() {return this.currentFile().listFiles().length;}

        Directory levelUp() {
            Directory parentDir = new Directory (this.file.getParentFile(), this.current);
            current.cutLast();
            return parentDir;
        }

        Directory levelDown() {
            Directory newDir = new Directory (this.currentFile(), this.current);
            current.add(-1);
            return newDir;
        }
    }

    static class Tier extends ArrayList<Integer> {
        Integer last() {return this.get(this.size() - 1);}

        void cutLast() {this.remove(this.size() - 1);}

        void incrementLast () {this.set(this.size() - 1, this.last() + 1);}
    }
}
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class FolderContents {

    public static void main(String[] args) {
        FolderContents folderContents = new FolderContents();
        folderContents.run();
    }

    public void run() {
        String theFolderPath = readFolderPath();
        makeBuildList(theFolderPath);
    }

    private String readFolderPath() {
        return "c:\\Users\\syber\\Desktop\\";
//        System.out.println("This program lists all the files contained in a given folder. \n" +
//                "Please enter directory path to be looked through.");
//        Scanner thisScanner = new Scanner(System.in);
//        String theFolderPath = thisScanner.nextLine();
//        thisScanner.close();
//        if ( ! new File(theFolderPath).isDirectory()) {
//            throw new RuntimeException("Directory does not exist");
//        }
//        System.out.println("\n");
//        return theFolderPath;
    }

    private void makeBuildList(String theFolderPath) {
        try (FileWriter outBuildList = new FileWriter(theFolderPath + "\\Folder_Contents.txt")) {
            final File theFolder = new File(theFolderPath);
            Tier total = new Tier();
            Tier current = new Tier();
            Directory dir = new Directory(theFolder, current);
            total.add(dir.numberOfFiles() - 1);
            current.add(-1);

            int i = 0;
            int k = 0;

            outerLoop:
            while (current.last() < total.last()) {
                current.incrementLast();

                if (dir.currentFile().isFile()) {
                    System.out.println(format(dir.currentFile(), theFolderPath));
                    outBuildList.write(format(dir.currentFile(), theFolderPath) + "\r\n");
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
                    k++;
                }

                while (current.last().equals(total.last())) {
                    if (total.size() != 1) {
                        dir = dir.levelUp();
                        total.cutLast();
                    } else {
                        break outerLoop;
                    }
                }
            }
            System.out.println("Number of files: " + i + ", number of folders: " + k);
        } catch (IOException e) {
            System.out.println("IOException when making FolderContents");
            e.printStackTrace();
        }
    }

    private String format(File file, String root) {
        return '.' + file.toString().substring(root.length()).replace('\\', '/');
    }

    static class Tier extends ArrayList<Integer> {
        Integer last() {return this.get(this.size() - 1);}

        void cutLast() {this.remove(this.size() - 1);}

        void incrementLast () {this.set(this.size() - 1, this.last() + 1);}
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
}